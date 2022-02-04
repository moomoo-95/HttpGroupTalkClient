package moomoo.hgtp.grouptalk.protocol.http.base;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;

import java.nio.charset.StandardCharsets;

public class HttpRequest {

    private final FullHttpRequest request;

    public HttpRequest(final FullHttpRequest request) {
        this.request = request;
    }

    public HttpHeaders headers() {
        return request.headers();
    }
    public String body() {
        return request.content().toString(StandardCharsets.UTF_8);
    }
}
