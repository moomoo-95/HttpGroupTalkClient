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
        HttpPostRequestEncoder postRequestEncoder = null;
        HttpRequest request = null;
        try {
            uri = new URI("http://"+userInfo.getHttpTargetNetAddress().getAddressString()+"/data");
            request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, uri.getRawPath());
            request.headers().set(HttpHeaderNames.HOST, uri.getHost());
            request.headers().set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);
            request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
            request.headers().set(HttpHeaderNames.USER_AGENT, userInfo.getUserId());

            postRequestEncoder = new HttpPostRequestEncoder(request, true);
            postRequestEncoder.addBodyAttribute("UserList", "aaa bbb ccc ddd");
            request = postRequestEncoder.finalizeRequest();

        } catch (Exception e) {
            log.debug("HttpMessageHandler.sendRoomListRequest ", e);
        } finally {
            try {postRequestEncoder.close();} catch (Exception e) {}
        }


        GroupSocket groupSocket = NetworkManager.getInstance().getHttpGroupSocket(userInfo.getUserId(), false);
        if (groupSocket == null) { return; }
        log.debug("groupSocket : {}", groupSocket.getDestination(userInfo.getSessionId()).toString());
        NettyTcpClientChannel clientChannel = (NettyTcpClientChannel) groupSocket.getDestination(userInfo.getSessionId()).getNettyChannel();

        if (request != null) {
            clientChannel.sendHttpRequest(request);
        }

    }
}
