package moomoo.hgtp.grouptalk.protocol.http.message.content;

import moomoo.hgtp.grouptalk.protocol.http.base.HttpMessage;

/**
 * @class HttpNoticeContent
 * @brief 서버가 클라이언트에게 공지할 내용을 전달하기 위한 http 요청 메시지
 */
public class HttpNoticeContent extends HttpMessage {

    private final String notice;

    public HttpNoticeContent(String notice) {
        this.notice = notice;
    }

    public String getNotice() {
        return notice;
    }
}
