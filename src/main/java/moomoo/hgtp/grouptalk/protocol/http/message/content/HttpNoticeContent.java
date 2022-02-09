package moomoo.hgtp.grouptalk.protocol.http.message.content;

import moomoo.hgtp.grouptalk.protocol.http.base.HttpMessage;
import moomoo.hgtp.grouptalk.util.NetworkUtil;

/**
 * @class HttpNoticeContent
 * @brief 서버가 클라이언트에게 공지할 내용을 전달하기 위한 http 요청 메시지
 */
public class HttpNoticeContent extends HttpMessage {

    private final String notice;

    public HttpNoticeContent(String notice) {
        String encodeNotice = NetworkUtil.messageEncoding(notice);
        this.notice = encodeNotice;
    }

    public String getNotice() {
        return NetworkUtil.messageDecoding(notice);
    }
}
