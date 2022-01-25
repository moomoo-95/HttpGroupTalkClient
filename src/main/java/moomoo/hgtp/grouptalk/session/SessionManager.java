package moomoo.hgtp.grouptalk.session;

import moomoo.hgtp.grouptalk.config.ConfigManager;
import moomoo.hgtp.grouptalk.network.NetworkManager;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.base.HgtpMessageType;
import moomoo.hgtp.grouptalk.service.AppInstance;
import moomoo.hgtp.grouptalk.session.base.RoomInfo;
import moomoo.hgtp.grouptalk.session.base.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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


    public short addUserInfo(String userId, long expire) {
        if (userInfoHashMap.containsKey(userId)) {
            log.warn("({}) () () UserInfo already exist.", userId);
            return HgtpMessageType.BAD_REQUEST;
        }

        // PortResourceManager에서 채널 할당
        short httpServerPort = (short) networkManager.getBaseEnvironment().getPortResourceManager().takePort();
        short httpClientPort = (short) networkManager.getBaseEnvironment().getPortResourceManager().takePort();

        ConfigManager configManager = appInstance.getConfigManager();
        UserInfo userInfo = new UserInfo(userId, configManager.getLocalListenIp(), configManager.getHgtpListenPort(), httpServerPort, httpClientPort, expire);
        synchronized (userInfoHashMap) {
            userInfoHashMap.put(userId, userInfo);
        }
        log.debug("({}) () () UserInfo is created.", userId);

        if (httpServerPort <= 0 || httpClientPort <= 0) {
            log.warn("({}) () () PortResourceManager's port is not available. [RECV:{}/SEND:{}]", userId, httpServerPort, httpClientPort);
            return HgtpMessageType.SERVER_UNAVAILABLE;
        } else {
            networkManager.addHttpSocket(userId, userInfo.getHttpServerNetAddress(), true);
            networkManager.addHttpSocket(userId, userInfo.getHttpClientNetAddress(), false);
        }

        if (appInstance.getConfigManager().getUserMaxSize() < userInfoHashMap.size()) {
            log.warn("({}) () () Unavailable add UserInfo", userId);
            return HgtpMessageType.SERVER_UNAVAILABLE;
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

                if (networkManager.getHttpSocket(userId) != null){
                    networkManager.removeHttpSocket(userId, true);
                    networkManager.removeHttpSocket(userId, false);
                }
            }
            log.debug("({}) () () UserInfo is deleted.", userId);
        }
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
        if (!roomInfoHashMap.get(roomId).getManagerId().equals(managerId)) {
            log.warn("({}) ({}) () UserInfo is not room manager.", managerId, roomId);
            return HgtpMessageType.BAD_REQUEST;
        }
        if (roomInfoHashMap.containsKey(roomId)) {
            synchronized (roomInfoHashMap) {
                roomInfoHashMap.remove(roomId);
            }
            log.debug("({}) ({}) () RoomInfo is deleted.", managerId, roomId);
        }
        return HgtpMessageType.OK;
    }

    public int getUserInfoSize() {
        return userInfoHashMap.size();
    }

    public int getRoomInfoSize() { return roomInfoHashMap.size(); }

    public UserInfo getUserInfo(String userId) {
        if ( userInfoHashMap.containsKey(userId) ) {
            return userInfoHashMap.get(userId);
        } else {
            return null;
        }
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
}
