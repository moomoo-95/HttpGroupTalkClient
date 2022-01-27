package moomoo.hgtp.grouptalk.protocol.http.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import moomoo.hgtp.grouptalk.gui.GuiManager;
import moomoo.hgtp.grouptalk.gui.component.panel.RoomListPanel;
import moomoo.hgtp.grouptalk.gui.component.panel.UserListPanel;
import moomoo.hgtp.grouptalk.network.NetworkManager;
import moomoo.hgtp.grouptalk.protocol.http.base.HttpMessageType;
import moomoo.hgtp.grouptalk.protocol.http.message.content.HttpRoomListContent;
import moomoo.hgtp.grouptalk.protocol.http.message.content.HttpUserListContent;
import moomoo.hgtp.grouptalk.session.SessionManager;
import moomoo.hgtp.grouptalk.session.base.UserInfo;
import network.socket.GroupSocket;
import network.socket.netty.tcp.NettyTcpClientChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;

public class HttpRequestMessageHandler {

    private static final Logger log = LoggerFactory.getLogger(HttpRequestMessageHandler.class);

    /**
     * @fn sendRoomListRequest
     * @brief room list 정보를 client 에게 전송하는 메서드, 방 생성 삭제 시 마다 모든 클라이언트에게 전송
     * @param userInfo
     */
    public void sendRoomListRequest(UserInfo userInfo) {
        DefaultFullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, HttpMessageType.ROOM_LIST);
        HttpRoomListContent httpRoomListContent = new HttpRoomListContent();

        setRequestHeader(request, userInfo, HttpMessageType.ROOM_LIST);
        SessionManager sessionManager = SessionManager.getInstance();

        if (sessionManager.getRoomInfoSize() > 0) {
            HashSet<String> roomInfoSet = new HashSet<>(sessionManager.getRoomInfoHashMap().keySet()) ;
            httpRoomListContent.addAllRoomList(roomInfoSet);
        }

        String content = httpRoomListContent.toString();

        ByteBuf byteBuf = Unpooled.copiedBuffer(content, StandardCharsets.UTF_8);
        request.headers().set(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
        request.content().writeBytes(byteBuf);

        GroupSocket groupSocket = NetworkManager.getInstance().getHttpGroupSocket(userInfo.getUserId(), false);
        if (groupSocket == null) {
            return;
        }
        NettyTcpClientChannel clientChannel = (NettyTcpClientChannel) groupSocket.getDestination(userInfo.getSessionId()).getNettyChannel();
        if (request != null) {
            clientChannel.sendHttpRequest(request);
            log.debug("[{}] -> [{}] -> [{}\n\n{}]",
                    groupSocket.getListenSocket().getNetAddress().getPort(),
                    groupSocket.getDestination(userInfo.getSessionId()).getGroupEndpointId().getGroupAddress().getPort(),
                    request.headers().toString(), request.content().toString(StandardCharsets.UTF_8));
            }

        clientChannel.closeConnectChannel();
        clientChannel.openConnectChannel(userInfo.getHttpTargetNetAddress().getInet4Address().getHostAddress(), userInfo.getHttpTargetNetAddress().getPort());
    }

    /**
     * @fn sendUserListRequest
     * @brief user list 정보를 client 에게 전송하는 메서드, user 등록 해제 시 마다 모든 클라이언트에게 전송
     * @param userInfo
     */
    public void sendUserListRequest(UserInfo userInfo) {
        DefaultFullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, HttpMessageType.ROOM_LIST);
        HttpUserListContent httpUserListContent = new HttpUserListContent();

        setRequestHeader(request, userInfo, HttpMessageType.USER_LIST);
        SessionManager sessionManager = SessionManager.getInstance();

        if (sessionManager.getUserInfoSize() > 0) {
            HashSet<String> userInfoSet = new HashSet<>(sessionManager.getUserInfoHashMap().keySet()) ;
            httpUserListContent.addAllUserList(userInfoSet);
        }

        String content = httpUserListContent.toString();

        ByteBuf byteBuf = Unpooled.copiedBuffer(content, StandardCharsets.UTF_8);
        request.headers().set(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
        request.content().writeBytes(byteBuf);

        GroupSocket groupSocket = NetworkManager.getInstance().getHttpGroupSocket(userInfo.getUserId(), false);
        if (groupSocket == null) {
            return;
        }
        NettyTcpClientChannel clientChannel = (NettyTcpClientChannel) groupSocket.getDestination(userInfo.getSessionId()).getNettyChannel();
        if (request != null) {
            clientChannel.sendHttpRequest(request);
            log.debug("[{}] -> [{}] -> [{}\n\n{}]",
                    groupSocket.getListenSocket().getNetAddress().getPort(),
                    groupSocket.getDestination(userInfo.getSessionId()).getGroupEndpointId().getGroupAddress().getPort(),
                    request.headers().toString(), request.content().toString(StandardCharsets.UTF_8));
        }

        clientChannel.closeConnectChannel();
        clientChannel.openConnectChannel(userInfo.getHttpTargetNetAddress().getInet4Address().getHostAddress(), userInfo.getHttpTargetNetAddress().getPort());
    }

    /**
     * @fn receiveRoomListRequest
     * @brief server로 부터 받은 room list 정보를 통해 room list를 갱신하는 메서드
     * @param roomListContent
     */
    public void receiveRoomListRequest(HttpRoomListContent roomListContent) {
        RoomListPanel roomListPanel = GuiManager.getInstance().getRoomListPanel();

        if (roomListContent.isEmpty()) {
            roomListPanel.setRoomList(null);
        } else {
            roomListPanel.setRoomList(roomListContent.getRoomListSet());
        }
    }

    /**
     * @fn receiveUserListRequest
     * @brief server로 부터 받은 user list 정보를 통해 user list를 갱신하는 메서드
     * @param userListContent
     */
    public void receiveUserListRequest(HttpUserListContent userListContent) {
        UserListPanel userListPanel = GuiManager.getInstance().getUserListPanel();

        if (userListContent.isEmpty()) {
            userListPanel.setUserList(null);
        } else {
            userListPanel.setUserList(userListContent.getUserListSet());
        }
    }

    private void setRequestHeader(DefaultFullHttpRequest request, UserInfo userInfo, String messageType) {
        request.headers().set(HttpHeaderNames.HOST, userInfo.getHttpClientNetAddress().getInet4Address().getHostAddress());
        request.headers().set(HttpHeaderNames.USER_AGENT, userInfo.getUserId());
        request.headers().set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);
        request.headers().set(HttpMessageType.MESSAGE_TYPE, messageType);
    }
}
