package moomoo.hgtp.client.session;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioDatagramChannel;
import moomoo.hgtp.client.network.NetworkManager;
import moomoo.hgtp.client.network.handler.HgtpChannelHandler;
import moomoo.hgtp.client.protocol.hgtp.message.base.HgtpMessageType;
import moomoo.hgtp.client.service.AppInstance;
import moomoo.hgtp.client.session.base.RoomInfo;
import moomoo.hgtp.client.session.base.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private static final Logger log = LoggerFactory.getLogger(SessionManager.class);
    private static AppInstance appInstance = AppInstance.getInstance();
    private static SessionManager sessionManager = null;

    private final ConcurrentHashMap<String, UserInfo> userInfoHashMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, RoomInfo> roomInfoHashMap = new ConcurrentHashMap<>();

    // HGTP 통신을 위한 채널초기화 변수
    private ChannelInitializer<NioDatagramChannel> channelInitializer = new ChannelInitializer<NioDatagramChannel>() {
        @Override
        protected void initChannel(NioDatagramChannel datagramChannel) {
            final ChannelPipeline channelPipeline = datagramChannel.pipeline();
            channelPipeline.addLast(new HgtpChannelHandler());
        }
    };

    public SessionManager() {
        // nothing
    }

    public static SessionManager getInstance() {
        if (sessionManager == null) {
            sessionManager = new SessionManager();
        }
        return sessionManager;
    }


    public short addUserInfo(String userId, String userIp, short userPort, long expire) {
        if (userInfoHashMap.containsKey(userId)) {
            log.warn("({}) () () UserInfo already exist.", userId);
        }

        if (appInstance.getConfigManager().getUserMaxSize() < userInfoHashMap.size()) {
            log.warn("({}) () () Unavailable add UserInfo", userId);
            return HgtpMessageType.SERVER_UNAVAILABLE;
        }
        UserInfo userInfo = new UserInfo(userId, userIp, userPort, expire);
        synchronized (userInfoHashMap) {
            userInfoHashMap.put(userId, userInfo);
        }
        NetworkManager.getInstance().getHgtpGroupSocket().addDestination(userInfo.getUserNetAddress(), null, userInfo.getSessionId(), channelInitializer);
        return HgtpMessageType.OK;
    }

    public void deleteUserInfo(String userId) {
        if (userInfoHashMap.containsKey(userId)) {
            UserInfo userInfo = null;
            synchronized (userInfoHashMap) {
                userInfo = userInfoHashMap.remove(userId);
            }
            if (userInfo != null) {
                NetworkManager.getInstance().getHgtpGroupSocket().removeDestination(userInfo.getSessionId());
            }
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
            log.debug("({}) ({}) () RoomInfo was delete.", managerId, roomId);
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
