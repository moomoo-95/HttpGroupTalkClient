package moomoo.hgtp.grouptalk.protocol.http;

import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import moomoo.hgtp.grouptalk.session.base.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class HttpMessageHandler {
    private static final Logger log = LoggerFactory.getLogger(HttpMessageHandler.class);

    public void sendRoomListRequest(UserInfo userInfo) {
        DefaultHttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.PUT, userInfo.getHttpServerNetAddress().getAddressString());
        request.headers().set(HttpHeaders.Names.USER_AGENT, userInfo.getUserId());
        request.headers().set("UserList", new HashMap<String, String>() {{
            put("111", "aaa");
            put("222", "bbb");
            put("333", "ccc");
            put("444", "ddd");

        }});
        log.debug("reqeust : {}", request);
//        NetworkManager.getInstance().getHttpGroupSocket(userInfo.getUserId()).getDestination()
//        networkManager.getHttpGroupSocket().getDestination(userInfo.getSessionId()).getNettyChannel().sendData(data, data.length);

    }
}
