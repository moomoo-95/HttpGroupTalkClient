package moomoo.hgtp.grouptalk.network.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpChannelHandler extends SimpleChannelInboundHandler<HttpObject> {
    private static final Logger log = LoggerFactory.getLogger(HttpChannelHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, HttpObject httpObject){
        if (httpObject instanceof HttpRequest) {
//            DefaultHttpRequest req = (DefaultHttpRequest) msg;
//            DefaultFullHttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,  HttpResponseStatus.OK);
//            if (req.decoderResult().isFailure()) {
//                log.warn("Fail to process the request. Bad request is detected.", req);
//                sendFailResponse(ctx, req, res, HttpResponseStatus.BAD_REQUEST);
//                return;
//            }
            log.debug("HTTP REQ MSG : {}", httpObject);
        } else if (httpObject instanceof HttpResponse) {
            log.debug("HTTP RES MSG : {}", httpObject);

        } else {
            log.debug("Undefine message : {}", httpObject.toString());
        }
    }

//    public static void sendResponse(ChannelHandlerContext ctx, DefaultHttpRequest req, FullHttpResponse res) {
//        ctx.write(res);
//        log.debug("() () () Response: {}", res);
//    }
//
//    public void sendFailResponse(ChannelHandlerContext ctx, DefaultHttpRequest req, FullHttpResponse res, HttpResponseStatus httpResponseStatus) {
//        res.setStatus(httpResponseStatus);
//        sendResponse(ctx, req, res);
//    }
}
