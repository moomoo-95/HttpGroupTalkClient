package moomoo.hgtp.grouptalk.session.base;

import moomoo.hgtp.grouptalk.gui.GuiManager;
import moomoo.hgtp.grouptalk.network.NetworkManager;
import moomoo.hgtp.grouptalk.service.AppInstance;
import moomoo.hgtp.grouptalk.service.base.ProcessMode;
import moomoo.hgtp.grouptalk.session.SessionManager;
import network.definition.NetAddress;
import network.socket.SocketProtocol;
import util.module.ByteUtil;

import java.nio.charset.StandardCharsets;

public class UserInfo {

    private final String userId;
    // groupsocket 의 Destination 추가에 필요한 sessionId
    private final long sessionId;
    // fsm 식별자
    private final String hgtpStateUnitId;
    private final NetAddress hgtpLocalNetAddress;
    private final NetAddress httpServerNetAddress;
    private final NetAddress httpClientNetAddress;
    private final long expireTime;
    private final long createTime;

    private String hostName = "";

    private NetAddress hgtpTargetNetAddress = null;
    private NetAddress httpTargetNetAddress = null;

    private boolean isRegister = false;
    private String roomId = "";

    public UserInfo(String userId, String listenIp, short hgtpListenPort, short httpServerPort, short httpClientPort, long expireTime) {
        this.userId = userId;
        byte[] userIdByteData = userId.getBytes(StandardCharsets.UTF_8);
        this.sessionId = ByteUtil.bytesToLong(userIdByteData, true);
        this.hgtpStateUnitId = userId + "_HGTP";
        this.hgtpLocalNetAddress = new NetAddress(listenIp, hgtpListenPort, true, SocketProtocol.UDP);
        this.httpServerNetAddress = new NetAddress(listenIp, httpServerPort, true, SocketProtocol.TCP);
        this.httpClientNetAddress = new NetAddress(listenIp, httpClientPort, true, SocketProtocol.TCP);
        this.expireTime = expireTime;
        this.createTime = System.currentTimeMillis();
    }

    public String getUserId() {return userId;}

    public long getSessionId() {return sessionId;}

    public String getHgtpStateUnitId() {return hgtpStateUnitId;}

    public NetAddress getHgtpLocalNetAddress() {return hgtpLocalNetAddress;}

    public NetAddress getHttpServerNetAddress() {return httpServerNetAddress;}

    public NetAddress getHttpClientNetAddress() { return httpClientNetAddress; }

    public long getExpireTime() {
        return expireTime;
    }

    public long getCreateTime() {return createTime;}

    public String getHostName() { return hostName; }
    public void setHostName(String hostName) {this.hostName = hostName;}
    public void initHostName() { this.hostName = ""; }

    public NetAddress getHgtpTargetNetAddress() {return hgtpTargetNetAddress;}

    public void setHgtpTargetNetAddress(String targetIp, short targetPort) {
        this.hgtpTargetNetAddress = new NetAddress(targetIp, targetPort, true, SocketProtocol.UDP);
        NetworkManager.getInstance().addDestinationHgtpSocket(this);
    }

    public NetAddress getHttpTargetNetAddress() {return httpTargetNetAddress;}

    public void setHttpTargetNetAddress(String targetIp, short targetPort) {
        this.httpTargetNetAddress = new NetAddress(targetIp, targetPort, true, SocketProtocol.TCP);
        NetworkManager.getInstance().addDestinationHttpSocket(this);
    }

    public boolean isRegister() {return isRegister;}
    public void setRegister() {isRegister = true;}

    public String getRoomId() {return roomId;}
    public void setRoomId(String roomId) {
        this.roomId = roomId;

        if(AppInstance.getInstance().getMode() == ProcessMode.CLIENT) {
            RoomInfo roomInfo = SessionManager.getInstance().getRoomInfo(roomId);
            if (roomInfo != null) {
                GuiManager.getInstance().getRoomPanel().setRoomName(roomInfo.getRoomName());
            }
        }
    }

    public void initRoomId() {
        this.roomId = "";

        if(AppInstance.getInstance().getMode() == ProcessMode.CLIENT) {
            GuiManager.getInstance().getRoomPanel().setRoomName(this.roomId);
        }
    }
}
