package moomoo.hgtp.grouptalk.protocol.http;

import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.HttpPostRequestEncoder;
import moomoo.hgtp.grouptalk.network.NetworkManager;
import moomoo.hgtp.grouptalk.session.base.UserInfo;
import network.socket.GroupSocket;
import network.socket.netty.tcp.NettyTcpClientChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

public class HttpMessageHandler {
    private static final Logger log = LoggerFactory.getLogger(HttpMessageHandler.class);

    public void sendRoomListRequest(UserInfo userInfo) {
        URI uri = null;
//        HttpPostRequestEncoder postRequestEncoder = null;
        HttpRequest request = null;
        try {
            uri = new URI("http://"+userInfo.getHttpTargetNetAddress().getAddressString());
            request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, uri.toString());
            request.headers().set(HttpHeaderNames.HOST, uri.getHost());
            request.headers().set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.MULTIPART_MIXED);
            request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            request.headers().set(HttpHeaderNames.USER_AGENT, userInfo.getUserId());

//            postRequestEncoder = new HttpPostRequestEncoder(request, true);
//            postRequestEncoder.addBodyAttribute("UserList", "aaa bbb ccc ddd");
//            request = postRequestEncoder.finalizeRequest();

        } catch (Exception e) {
            log.debug("HttpMessageHandler.sendRoomListRequest ", e);
        } finally {
//            try {postRequestEncoder.close();} catch (Exception e) {}
        }


        GroupSocket groupSocket = NetworkManager.getInstance().getHttpGroupSocket(userInfo.getUserId(), false);
        if (groupSocket == null) { return; }
        NettyTcpClientChannel clientChannel = (NettyTcpClientChannel) groupSocket.getDestination(userInfo.getSessionId()).getNettyChannel();

        if (request != null) {
            clientChannel.sendHttpRequest(request);
            log.debug("[{}] -> [{}] -> [{}]", groupSocket.getListenSocket().getNetAddress().getPort(), groupSocket.getDestination(userInfo.getSessionId()).getGroupEndpointId().getGroupAddress().getPort(), request);
        }

    }
}
