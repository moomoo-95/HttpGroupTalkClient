package moomoo.hgtp.client.network.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpChannelHandler extends ChannelInboundHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(HttpChannelHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg){
//        if (msg instanceof DefaultHttpRequest) {
//            DefaultHttpRequest req = (DefaultHttpRequest) msg;
//            DefaultFullHttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,  HttpResponseStatus.OK);
//            if (req.decoderResult().isFailure()) {
//                log.warn("Fail to process the request. Bad request is detected.", req);
//                sendFailResponse(ctx, req, res, HttpResponseStatus.BAD_REQUEST);
//                return;
//            }
//            log.debug("HTTP REQ MSG : {}", ((DefaultHttpRequest) msg).method().name());
//        } else if (msg instanceof DefaultHttpResponse) {
//            log.debug("HTTP RES MSG : {}", ((DefaultHttpResponse) msg).status().code());
//
//        } else {
//            log.debug("Undefine message : {}", msg.toString());
//        }
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
