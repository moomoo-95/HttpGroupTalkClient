package moomoo.hgtp.grouptalk.protocol.http.handler;

import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import moomoo.hgtp.grouptalk.gui.GuiManager;
import moomoo.hgtp.grouptalk.gui.component.panel.RoomListPanel;
import moomoo.hgtp.grouptalk.network.NetworkManager;
import moomoo.hgtp.grouptalk.protocol.http.base.HttpMessageType;
import moomoo.hgtp.grouptalk.session.SessionManager;
import moomoo.hgtp.grouptalk.session.base.UserInfo;
import network.socket.GroupSocket;
import network.socket.netty.tcp.NettyTcpClientChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class HttpRequestMessageHandler {

    private static final Logger log = LoggerFactory.getLogger(HttpRequestMessageHandler.class);
    private static final String NULL = "NULL";

    /**
     * @fn sendRoomListRequest
     * @brief room list 정보를 client 에게 전송하는 메서드, 방 생성시 마다 모든 클라이언트에게 전송
     * @param userInfo
     */
    public void sendRoomListRequest(UserInfo userInfo) {
        DefaultHttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, HttpMessageType.ROOM_LIST);

        SessionManager sessionManager = SessionManager.getInstance();
        if (sessionManager.getRoomInfoSize() > 0) {
            Map roomInfoMap = sessionManager.getRoomInfoHashMap();
            roomInfoMap.keySet().toString();
            setRequestHeader(request, userInfo, HttpMessageType.ROOM_LIST, roomInfoMap.keySet());
        } else {
            setRequestHeader(request, userInfo, HttpMessageType.ROOM_LIST, NULL);
        }

        GroupSocket groupSocket = NetworkManager.getInstance().getHttpGroupSocket(userInfo.getUserId(), false);
        if (groupSocket == null) {
            return;
        }
        NettyTcpClientChannel clientChannel = (NettyTcpClientChannel) groupSocket.getDestination(userInfo.getSessionId()).getNettyChannel();
        if (request != null) {
            clientChannel.sendHttpRequest(request);
            log.debug("[{}] -> [{}] -> [{}]", groupSocket.getListenSocket().getNetAddress().getPort(), groupSocket.getDestination(userInfo.getSessionId()).getGroupEndpointId().getGroupAddress().getPort(), request);
        }

        clientChannel.closeConnectChannel();
        clientChannel.openConnectChannel(userInfo.getHttpTargetNetAddress().getInet4Address().getHostAddress(), userInfo.getHttpTargetNetAddress().getPort());
    }

    /**
     * @fn sendRoomListRequest
     * @brief server로 부터 받은 room list 정보를 통해 room list를 갱신하는 메서드
     * @param roomList
     */
    public void receiveRoomListRequest(String roomList) {
        RoomListPanel roomListPanel = GuiManager.getInstance().getRoomListPanel();

        if (roomList.equals(NULL)) {
            roomListPanel.setRoomList(null);
        } else {
            String room = roomList.substring(1, roomList.length()-1);
            String[] roomArray = room.split(", ");

            roomListPanel.setRoomList(roomArray);
        }

    }

    private void setRequestHeader(DefaultHttpRequest request, UserInfo userInfo, String contentType, Object content) {
        request.headers().set(HttpHeaderNames.HOST, userInfo.getHttpClientNetAddress().getInet4Address().getHostAddress());
        request.headers().set(HttpHeaderNames.USER_AGENT, userInfo.getUserId());
        request.headers().set(HttpHeaderNames.CONTENT_TYPE, contentType);
        request.headers().set(contentType, content);
    }
}
