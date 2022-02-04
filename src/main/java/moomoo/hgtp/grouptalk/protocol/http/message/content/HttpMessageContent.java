package moomoo.hgtp.grouptalk.protocol.http.message.content;

import moomoo.hgtp.grouptalk.protocol.http.base.HttpMessage;

import java.text.SimpleDateFormat;

public class HttpMessageContent extends HttpMessage {

    private final String userId;
    private final String message;
    private final long messageTime;

    public HttpMessageContent(String userId, String message, long messageTime) {
        this.userId = userId;
        this.message = message;
        this.messageTime = messageTime;
    }

    public String getUserId() {
        return userId;
    }

    public String getMessage() {
        return message;
    }

    public long getMessageTime() {
        return messageTime;
    }
    public String getMessageTimeFormat() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-DD HH:mm:ss.SSS");
        return simpleDateFormat.format(messageTime);
    }
}
