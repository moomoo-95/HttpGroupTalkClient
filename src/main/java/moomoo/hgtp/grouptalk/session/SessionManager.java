package moomoo.hgtp.grouptalk.session;

import moomoo.hgtp.grouptalk.config.ConfigManager;
import moomoo.hgtp.grouptalk.fsm.HgtpEvent;
import moomoo.hgtp.grouptalk.fsm.HgtpState;
import moomoo.hgtp.grouptalk.network.NetworkManager;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.base.HgtpMessageType;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.request.HgtpRemoveUserFromRoomRequest;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.request.handler.HgtpRequestHandler;
import moomoo.hgtp.grouptalk.service.AppInstance;
import moomoo.hgtp.grouptalk.session.base.RoomInfo;
import moomoo.hgtp.grouptalk.session.base.UserInfo;
import moomoo.hgtp.grouptalk.util.NetworkUtil;
import network.definition.NetAddress;
import network.socket.SocketProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.ResourceManager;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class SessionManager {

    private static final Logger log = LoggerFactory.getLogger(SessionManager.class);

    private static SessionManager sessionManager = null;

    private final ConcurrentHashMap<String, UserInfo> userInfoHashMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, RoomInfo> roomInfoHashMap = new ConcurrentHashMap<>();

    private AppInstance appInstance = AppInstance.getInstance();
    private NetworkManager networkManager = NetworkManager.getInstance();

    public SessionManager() {
        // nothing
    }

    public static SessionManager getInstance() {
        if (sessionManager == null) {
            sessionManager = new SessionManager();
        }
        return sessionManager;
    }


    public short addUserInfo(String userId, String hostName, long expire) {
        if (userInfoHashMap.containsKey(userId)) {
            log.warn("({}) () () UserInfo already exist.", userId);
            return HgtpMessageType.BAD_REQUEST;
        }

        if (appInstance.getConfigManager().getUserMaxSize() < userInfoHashMap.size()) {
            log.warn("({}) () () Unavailable add UserInfo", userId);
            return HgtpMessageType.SERVER_UNAVAILABLE;
        }


        String duplicateUserId = getDuplicateUserId(hostName);

        // PortResourceManager에서 채널 할당
        ConfigManager configManager = appInstance.getConfigManager();
        ResourceManager resourceManager = appInstance.getResourceManager();

        // 포트가 정상적으로 할당될 수 있는 경우 할당받을 때 까지 반복
        short httpServerPort = 0;
        short httpClientPort = 0 ;
        NetAddress serverNetAddress = new NetAddress(configManager.getLocalListenIp(), httpServerPort, true, SocketProtocol.TCP);
        NetAddress clientNetAddress = new NetAddress(configManager.getLocalListenIp(), httpClientPort, true, SocketProtocol.TCP);

        boolean isTaken = false;
        while (!isTaken) {
            httpServerPort = (short) resourceManager.takePort();
            if (httpServerPort <= 0) {
                log.warn("({}) () () PortResourceManager's port is not available. [RECV:{}]", userId, httpServerPort);
                return HgtpMessageType.SERVER_UNAVAILABLE;
            }
            serverNetAddress.setPort(httpServerPort);
            isTaken = networkManager.addHttpSocket(userId, serverNetAddress, true);
        }
        isTaken = false;
        while (!isTaken) {
            httpClientPort = (short) resourceManager.takePort();
            if (httpClientPort <= 0) {
                log.warn("({}) () () PortResourceManager's port is not available. [SEND:{}]", userId, httpClientPort);
                return HgtpMessageType.SERVER_UNAVAILABLE;
            }
            clientNetAddress.setPort(httpClientPort);
            isTaken = networkManager.addHttpSocket(userId, clientNetAddress, false);
        }

        UserInfo userInfo = new UserInfo(userId, configManager.getLocalListenIp(), configManager.getHgtpListenPort(), httpServerPort, httpClientPort, expire);
        userInfo.setHostName(hostName);
        synchronized (userInfoHashMap) {
            userInfoHashMap.put(userId, userInfo);
        }
        appInstance.getStateManager().addStateUnit(
                userInfo.getHgtpStateUnitId(), appInstance.getStateHandler().getName(),
                HgtpState.IDLE, userInfo
        );
        log.debug("({}) () () UserInfo is created.", userId);

        if (!duplicateUserId.equals("")) {
            log.warn("({}) () () {} is duplicate with [{}] in UserInfoMap ", userId, hostName,  duplicateUserId);
            return HgtpMessageType.DECLINE;
        }

        return HgtpMessageType.OK;
    }

    public void deleteUserInfo(String userId) {
        if (userInfoHashMap.containsKey(userId)) {
            UserInfo userInfo;
            synchronized (userInfoHashMap) {
                userInfo = userInfoHashMap.remove(userId);
            }
            if (userInfo != null) {
                if (networkManager.getHgtpGroupSocket().getDestination(userInfo.getSessionId()) != null) {
                    networkManager.getHgtpGroupSocket().removeDestination(userInfo.getSessionId());
                }

                if (networkManager.getHttpSocket(userId, true) != null){
                    networkManager.removeHttpSocket(userId, false);
                }
                if (networkManager.getHttpSocket(userId, false) != null){
                    networkManager.removeHttpSocket(userId, false);
                }
            }

            appInstance.getStateManager().removeStateUnit( userInfo.getHgtpStateUnitId() );
            log.debug("({}) () () UserInfo is deleted.", userId);
        }
    }

    /**
     * @fn getDuplicateUserId
     * @brief userInfoMap 내 중복된 hostName이 존재하는지 확인하는 메서드
     * @param hostName
     * @return 중복된 이미 존재하는 UserInfo의 UserId
     */
    private String getDuplicateUserId(String hostName) {
        for (UserInfo userInfo : userInfoHashMap.values()) {
            if (userInfo.getHostName().equals(hostName)) {
                return userInfo.getUserId();
            }
        }
        return "";
    }

    public short addRoomInfo(String roomId, String managerId) {
        if (roomId.equals("")) {
            log.warn("({}) ({}) () RoomId is null", managerId, roomId);
            return HgtpMessageType.BAD_REQUEST;
        }

        if (roomInfoHashMap.containsKey(roomId)) {
            log.warn("({}) ({}) () RoomInfo already exist.", managerId, roomId);
            return HgtpMessageType.BAD_REQUEST;
        }
        if (appInstance.getConfigManager().getRoomMaxSize() < roomInfoHashMap.size()) {
            log.warn("({}) ({}) () Unavailable add RoomInfo", managerId, roomId);
            return HgtpMessageType.SERVER_UNAVAILABLE;
        }

        RoomInfo roomInfo = new RoomInfo(roomId, managerId);
        synchronized (roomInfoHashMap) {
            roomInfoHashMap.put(roomId, roomInfo);
        }
        UserInfo userInfo = getUserInfo(managerId);
        userInfo.setRoomId(roomId);

        log.debug("({}) ({}) () RoomInfo is created.", managerId, roomId);
        return  HgtpMessageType.OK;
    }

    public short deleteRoomInfo(String roomId, String managerId) {
        if (roomId.equals("")) {
            log.warn("({}) ({}) () RoomId is null", managerId, roomId);
            return HgtpMessageType.BAD_REQUEST;
        }

        if (!roomInfoHashMap.containsKey(roomId)) {
            log.warn("({}) ({}) () RoomInfo already deleted.", managerId, roomId);
            return HgtpMessageType.BAD_REQUEST;
        }
        RoomInfo roomInfo = roomInfoHashMap.get(roomId);
        if (!roomInfo.getManagerId().equals(managerId)) {
            log.warn("({}) ({}) () UserInfo is not room manager.", managerId, roomId);
            return HgtpMessageType.BAD_REQUEST;
        }
        if (!roomInfo.isUserGroupSetEmpty()) {
            log.warn("({}) ({}) () UserInfo is not empty", managerId, roomId);
            HgtpRequestHandler hgtpRequestHandler = new HgtpRequestHandler();

            Set<String> removeSet = roomInfo.getUserGroupSet();
            removeSet.remove(managerId);

            removeSet.forEach(userId -> {
                UserInfo userInfo = sessionManager.getUserInfo(userId);
                if (userInfo != null) {
                    appInstance.getStateHandler().fire(HgtpEvent.REMOVE_USER_ROOM, appInstance.getStateManager().getStateUnit(userInfo.getHgtpStateUnitId()));
                    // create request remove user from room
                    HgtpRemoveUserFromRoomRequest hgtpRemoveUserFromRoomRequest = new HgtpRemoveUserFromRoomRequest(
                            userId, AppInstance.SEQ_INCREMENT, roomInfo.getRoomId(), NetworkUtil.messageEncoding(userInfo.getHostName())
                    );
                    hgtpRequestHandler.sendRemoveUserFromRoomRequest(hgtpRemoveUserFromRoomRequest);
                    appInstance.getStateHandler().fire(HgtpEvent.REMOVE_USER_ROOM_SUC, appInstance.getStateManager().getStateUnit(userInfo.getHgtpStateUnitId()));
                }
            });
        }

        if (roomInfoHashMap.containsKey(roomId)) {
            synchronized (roomInfoHashMap) {
                roomInfoHashMap.remove(roomId);
            }
            log.debug("({}) ({}) () RoomInfo is deleted.", managerId, roomId);
        }
        UserInfo userInfo = getUserInfo(managerId);
        userInfo.initRoomId();

        return HgtpMessageType.OK;
    }

    public int getUserInfoSize() { return userInfoHashMap.size(); }

    public int getRoomInfoSize() { return roomInfoHashMap.size(); }

    public UserInfo getUserInfo(String userId) {
        if ( userInfoHashMap.containsKey(userId) ) {
            return userInfoHashMap.get(userId);
        } else {
            return null;
        }
    }

    public UserInfo getUserInfoWithHostName(String hostName) {
        List<UserInfo> userInfos = userInfoHashMap.values().stream().filter(userInfo -> userInfo.getHostName().equals(hostName)).collect(Collectors.toList());

        return userInfos.size() <= 0 ? null : userInfos.get(0);
    }

    public RoomInfo getRoomInfo(String roomId) {
        if ( roomInfoHashMap.containsKey(roomId) ) {
            return roomInfoHashMap.get(roomId);
        } else {
            return null;
        }
    }

    public Map<String, UserInfo> getUserInfoHashMap() {
        return userInfoHashMap;
    }

    public Set<String> getHostNameSet() {
        return userInfoHashMap.values().stream()
                .map(userInfo -> NetworkUtil.messageEncoding(userInfo.getHostName()))
                .collect(Collectors.toSet());
    }

    public Set<String> getHostNameSet(Set<String> userIdSet) {
        return userInfoHashMap.values().stream().filter(userInfo -> userIdSet.contains(userInfo.getUserId()))
                .map(userInfo -> NetworkUtil.messageEncoding(userInfo.getHostName()))
                .collect(Collectors.toSet());
    }

    public Map<String, RoomInfo> getRoomInfoHashMap() {
        return roomInfoHashMap;
    }
}
