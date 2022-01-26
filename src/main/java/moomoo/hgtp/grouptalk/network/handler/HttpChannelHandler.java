package moomoo.hgtp.grouptalk.network.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import moomoo.hgtp.grouptalk.protocol.http.HttpManager;
import moomoo.hgtp.grouptalk.protocol.http.handler.HttpResponseMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpChannelHandler extends SimpleChannelInboundHandler<HttpObject> {
    private static final Logger log = LoggerFactory.getLogger(HttpChannelHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, HttpObject httpObject){
        if (httpObject.decoderResult().isFailure()) {
            log.warn("Fail to process the request. Bad request is detected. {}", httpObject);
            return;
        }

        HttpManager.getInstance().putMessage(httpObject);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.warn("HttpChannelHandler.Exception (cause={})", cause.toString());
    }
}
