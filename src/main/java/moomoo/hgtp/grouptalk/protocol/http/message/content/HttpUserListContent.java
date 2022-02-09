package moomoo.hgtp.grouptalk.protocol.http.message.content;

import moomoo.hgtp.grouptalk.protocol.http.base.HttpMessage;

import java.util.HashSet;

/**
 * @class HttpUserListContent
 * @brief 서버가 클라이언트에게 유저 목록 정보를 보내기 위한 http 요청 메시지
 */
public class HttpUserListContent extends HttpMessage {

    private final HashSet<String> userListSet = new HashSet<>();

    public HttpUserListContent() {
        // nothing
    }

    public void addAllUserList(HashSet<String> roomKeySet) {
        userListSet.addAll(roomKeySet);
    }

    public boolean isEmpty() { return userListSet.isEmpty(); }
    public HashSet<String> getUserListSet() { return userListSet; }

}
