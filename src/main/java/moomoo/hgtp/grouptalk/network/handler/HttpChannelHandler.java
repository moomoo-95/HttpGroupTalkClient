package moomoo.hgtp.grouptalk.network.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import moomoo.hgtp.grouptalk.protocol.http.HttpManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

import static moomoo.hgtp.grouptalk.protocol.http.base.HttpMessageType.PARSE_CONTENT;
import static moomoo.hgtp.grouptalk.protocol.http.base.HttpMessageType.PARSE_MESSAGE;

public class HttpChannelHandler extends SimpleChannelInboundHandler<HttpObject> {
    private static final Logger log = LoggerFactory.getLogger(HttpChannelHandler.class);

    // HTTP 메시지 수신시 channelRead0가 총 두 번 호출됨
    // DefaultHttpResponse 또는 DefaultHttpRequest (0) -> DefaultHttpContent (1) 와 EmptyLastHttpContent (2) 는 동시에 들어옴
    private boolean isContent = false;
    private Object[] httpObjectArray = new Object[] {null, null};


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, HttpObject httpObject){
        if (httpObject.decoderResult().isFailure()) {
            log.warn("Fail to process the http message. {}", httpObject);
            return;
        }
        // request or response
        if ( (httpObject instanceof HttpRequest || httpObject instanceof HttpResponse) && !isContent) {
            if (httpObjectArray[PARSE_MESSAGE] != null) {
                log.error("({}) ({}) ({}) Fail to req/res message oder. {}", isContent, httpObjectArray[PARSE_MESSAGE], httpObjectArray[PARSE_CONTENT], httpObject);
            }
            httpObjectArray[PARSE_MESSAGE] = httpObject;
            isContent = true;
        }
        // content
        else if (httpObject instanceof HttpContent && isContent){
            if (httpObjectArray[PARSE_CONTENT] != null) {
                log.error("({}) ({}) ({}) Fail to cnt message oder. {}", isContent, httpObjectArray[PARSE_MESSAGE], httpObjectArray[PARSE_CONTENT], httpObject);
            }
            httpObjectArray[PARSE_CONTENT] = ((HttpContent) httpObject).content().toString(StandardCharsets.UTF_8);

            HttpManager.getInstance().putMessage(httpObjectArray);
        }
        else {
            log.error("({}) ({}) ({}) Fail to unexpected message oder. {}", isContent, httpObjectArray[PARSE_MESSAGE], httpObjectArray[PARSE_CONTENT], httpObject);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.warn("HttpChannelHandler.Exception ", cause);
    }
}
