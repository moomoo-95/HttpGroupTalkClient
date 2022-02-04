package moomoo.hgtp.grouptalk.protocol.http.message.content;

import moomoo.hgtp.grouptalk.protocol.http.base.HttpMessage;

/**
 * @class HttpRefreshContent
 * @brief 클라이언트가 서버에게 room, user에 대한 정보를 재 요청하기 위한 http 요청 메시지
 */
public class HttpRefreshContent extends HttpMessage {

    private final String userId;

    public HttpRefreshContent(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }
}
