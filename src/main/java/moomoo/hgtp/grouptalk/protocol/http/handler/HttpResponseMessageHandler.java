package moomoo.hgtp.grouptalk.protocol.http.handler;

import io.netty.handler.codec.http.*;

public class HttpResponseMessageHandler {
    public DefaultFullHttpResponse createResponse(DefaultHttpRequest request, HttpResponseStatus status) {
        DefaultFullHttpResponse httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,  status);

        setResponseHeader(request, httpResponse);

        return httpResponse;
    }

    private void setResponseHeader(DefaultHttpRequest request, DefaultFullHttpResponse response) {
        response.headers().set(HttpHeaderNames.HOST, request.headers().get(HttpHeaderNames.HOST));
        response.headers().set(HttpHeaderNames.USER_AGENT, request.headers().get(HttpHeaderNames.USER_AGENT));
    }
}
