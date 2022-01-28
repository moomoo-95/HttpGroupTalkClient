package moomoo.hgtp.grouptalk.protocol.http;

import io.netty.handler.codec.http.*;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.response.handler.HgtpResponseHandler;
import moomoo.hgtp.grouptalk.protocol.http.handler.HttpRequestMessageHandler;
import moomoo.hgtp.grouptalk.protocol.http.message.HttpMessageFactory;
import moomoo.hgtp.grouptalk.protocol.http.message.content.HttpRoomListContent;
import moomoo.hgtp.grouptalk.protocol.http.message.content.HttpRoomUserListContent;
import moomoo.hgtp.grouptalk.protocol.http.message.content.HttpUserListContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.module.ConcurrentCyclicFIFO;

import static moomoo.hgtp.grouptalk.protocol.http.base.HttpMessageType.*;

public class HttpConsumer implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(HgtpResponseHandler.class);

    private final ConcurrentCyclicFIFO<Object[]> httpQueue;
    private final HttpRequestMessageHandler httpRequestMessageHandler = new HttpRequestMessageHandler();

    private boolean isQuit = false;

    public HttpConsumer(ConcurrentCyclicFIFO<Object[]> httpQueue) {
        this.httpQueue = httpQueue;
    }

    @Override
    public void run() {
        queueProcessing();
    }

    private void queueProcessing() {
        while (!isQuit) {
            try {
                Object[] data = httpQueue.take();
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
    private void parseHttpMessage(Object[] httpObject) {
        String httpContent = (String) httpObject[PARSE_CONTENT];

        if (httpObject[PARSE_MESSAGE] instanceof HttpRequest) {
            HttpRequest httpRequest = (HttpRequest) httpObject[PARSE_MESSAGE];

            HttpHeaders httpHeaders = httpRequest.headers();

            String userId = httpHeaders.get(HttpHeaderNames.HOST);
            String messageType = httpHeaders.get(MESSAGE_TYPE);
            log.debug("({}) () () RECV MSG TYPE : {}", userId, messageType);
            log.debug("({}) () () RECV MSG : {}\n\n{}", userId, httpRequest, httpContent);

            switch (messageType){
                case ROOM_LIST:
                    HttpRoomListContent roomListContent = HttpMessageFactory.createHttpRoomListContent(httpContent);
                    httpRequestMessageHandler.receiveRoomListRequest(roomListContent);
                    break;
                case USER_LIST:
                    HttpUserListContent userListContent = HttpMessageFactory.createHttpUserListContent(httpContent);
                    httpRequestMessageHandler.receiveUserListRequest(userListContent);
                    break;
                case ROOM_USER_LIST:
                    HttpRoomUserListContent roomUserListContent = HttpMessageFactory.createHttpRoomUserListContent(httpContent);
                    httpRequestMessageHandler.receiveRoomUserListRequest(roomUserListContent);
                    break;
                case NOTICE:
                    break;
                case MESSAGE:
                    break;
                default:
                    log.warn("({}) () () Undefined message cannot be processed. {}", userId, httpRequest);
                    break;
            }
        }
        else if (httpObject[PARSE_MESSAGE] instanceof HttpResponse) {
            DefaultFullHttpResponse httpResponse = (DefaultFullHttpResponse) httpObject[PARSE_MESSAGE];

            HttpHeaders httpHeaders = httpResponse.headers();

            String userId = httpHeaders.get(HttpHeaderNames.HOST);
            HttpResponseStatus responseStatus = httpResponse.status();

            log.debug("({}) () () RECV MSG TYPE : {}", userId, responseStatus.reasonPhrase());
            log.debug("({}) () () RECV MSG : {}\n\n", userId, httpResponse, httpContent);
            if (HttpResponseStatus.OK.code() == responseStatus.code()) {
            } else if (HttpResponseStatus.BAD_REQUEST.code() == responseStatus.code()) {
            } else {
                log.warn("({}) () () Undefined message cannot be processed. {}", userId, httpResponse);
            }
        }
    }
}
