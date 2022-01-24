package moomoo.hgtp.grouptalk.session.base;

import network.definition.NetAddress;
import network.socket.SocketProtocol;
import util.module.ByteUtil;

import java.nio.charset.StandardCharsets;

public class UserInfo {

    private final String userId;
    // groupsocket 의 Destination 추가에 필요한 sessionId
    private final long sessionId;
    private final NetAddress hgtpNetAddress;
    private final NetAddress httpNetAddress;
    private final long expireTime;
    private final long createTime;

    private boolean isRegister = false;
    private String roomId = "";

    public UserInfo(String userId, String listenIp, short hgtpListenPort, short httpListenPort, long expireTime) {
        this.userId = userId;
        byte[] userIdByteData = userId.getBytes(StandardCharsets.UTF_8);
        this.sessionId = ByteUtil.bytesToLong(userIdByteData, true);
        this.hgtpNetAddress = new NetAddress(listenIp, hgtpListenPort, true, SocketProtocol.UDP);
        this.httpNetAddress = new NetAddress(listenIp, httpListenPort, true, SocketProtocol.TCP);
        this.expireTime = expireTime;
        this.createTime = System.currentTimeMillis();
    }

    public String getUserId() {
        return userId;
    }

    public long getSessionId() {return sessionId;}

    public NetAddress getHgtpNetAddress() {return hgtpNetAddress;}

    public NetAddress getHttpNetAddress() {return httpNetAddress;}

    public long getExpireTime() {
        return expireTime;
    }

    public long getCreateTime() {return createTime;}

    public boolean isRegister() {return isRegister;}
    public void setRegister() {isRegister = true;}

    public String getRoomId() {
        return roomId;
    }

}
