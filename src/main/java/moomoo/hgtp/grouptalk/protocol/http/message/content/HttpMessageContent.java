package moomoo.hgtp.grouptalk.protocol.http.message.content;

import moomoo.hgtp.grouptalk.protocol.http.base.HttpMessage;
import moomoo.hgtp.grouptalk.service.AppInstance;
import moomoo.hgtp.grouptalk.util.NetworkUtil;

import java.text.SimpleDateFormat;

/**
 * @class HttpMessageContent
 * @brief 동일한 방에 있는 모든 유저에게 메시지를 전달하기 위한 http 요청 메시지
 */
public class HttpMessageContent extends HttpMessage {

    private final String hostName;
    private final String message;
    private final long messageTime;

    public HttpMessageContent(String hostName, String message) {
        String encodeHostName = NetworkUtil.messageEncoding(hostName);
        this.hostName = encodeHostName;
        String encodeMessage = NetworkUtil.messageEncoding(message);
        this.message = encodeMessage;
        this.messageTime = AppInstance.getInstance().getTimeStamp();
    }

    public String getHostName() {
        return NetworkUtil.messageDecoding(hostName);
    }

    public String getMessage() {
        return NetworkUtil.messageDecoding(message);
    }

    public long getMessageTime() {
        return messageTime;
    }
    public String getMessageTimeFormat() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return simpleDateFormat.format(messageTime);
    }
}
