package moomoo.hgtp.grouptalk.protocol.http.message.content;

import moomoo.hgtp.grouptalk.protocol.http.base.HttpMessage;

public class HttpNoticeContent extends HttpMessage {

    private final String notice;

    public HttpNoticeContent(String notice) {
        this.notice = notice;
    }

    public String getNotice() {
        return notice;
    }
}
