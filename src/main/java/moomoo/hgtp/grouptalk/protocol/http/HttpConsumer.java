package moomoo.hgtp.grouptalk.protocol.http;

import io.netty.handler.codec.http.*;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.response.handler.HgtpResponseHandler;
import moomoo.hgtp.grouptalk.protocol.http.base.HttpMessageType;
import moomoo.hgtp.grouptalk.protocol.http.handler.HttpRequestMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.module.ConcurrentCyclicFIFO;

public class HttpConsumer implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(HgtpResponseHandler.class);

    private final ConcurrentCyclicFIFO<HttpObject> httpQueue;
    private final HttpRequestMessageHandler httpRequestMessageHandler = new HttpRequestMessageHandler();

    private boolean isQuit = false;

    public HttpConsumer(ConcurrentCyclicFIFO<HttpObject> httpQueue) {
        this.httpQueue = httpQueue;
    }

    @Override
    public void run() {
        queueProcessing();
    }

    private void queueProcessing() {
        while (!isQuit) {
            try {
                HttpObject data = httpQueue.take();
                parseHttpMessage(data);
            } catch (InterruptedException e) {
                log.error("() () () HttpConsumer.queueProcessing ", e);
                isQuit = true;
            }
        }
    }

    /**
     * @fn parseHttpMessage
     * @brief DefaultHttpRequest 형태로 들어온 http 요청 메시지를 분석하는 메서드
     * @param httpObject
     */
    private void parseHttpMessage(HttpObject httpObject) {
        if (httpObject instanceof HttpRequest) {
            DefaultHttpRequest httpRequest = (DefaultHttpRequest) httpObject;

            HttpHeaders httpHeaders = httpRequest.headers();

            String userId = httpHeaders.get(HttpHeaderNames.HOST);
            String messageType = httpHeaders.get(HttpHeaderNames.CONTENT_TYPE);
            log.debug("({}) () () RECV MSG TYPE : {}", userId, messageType);
            log.debug("({}) () () RECV MSG : {}", userId, httpRequest);

            switch (messageType){
                case HttpMessageType.ROOM_LIST:
                    String data = httpHeaders.get(messageType);
                    httpRequestMessageHandler.receiveRoomListRequest(data);
                    break;
                case HttpMessageType.USER_LIST:
                    break;
                case HttpMessageType.NOTICE:
                    break;
                case HttpMessageType.MESSAGE:
                    break;
                default:
                    log.warn("({}) () () Undefined message cannot be processed. {}", userId, httpRequest);
                    break;
            }
        }
        else if (httpObject instanceof HttpResponse) {
            DefaultHttpResponse httpResponse = (DefaultHttpResponse) httpObject;

            HttpHeaders httpHeaders = httpResponse.headers();

            String userId = httpHeaders.get(HttpHeaderNames.HOST);
            HttpResponseStatus responseStatus = httpResponse.status();

            log.debug("({}) () () RECV MSG TYPE : {}", userId, responseStatus.reasonPhrase());
            log.debug("({}) () () RECV MSG : {}", userId, httpResponse);
            if (HttpResponseStatus.OK.code() == responseStatus.code()) {
            } else if (HttpResponseStatus.BAD_REQUEST.code() == responseStatus.code()) {
            } else {
                log.warn("({}) () () Undefined message cannot be processed. {}", userId, httpResponse);
            }
        }
        else {
            log.warn("() () () Undefined message cannot be processed. {}", httpObject);
        }
    }
}
