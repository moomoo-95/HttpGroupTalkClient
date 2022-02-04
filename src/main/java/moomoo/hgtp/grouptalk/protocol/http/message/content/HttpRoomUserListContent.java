package moomoo.hgtp.grouptalk.protocol.http.message.content;

import moomoo.hgtp.grouptalk.protocol.http.base.HttpMessage;

import java.util.HashSet;

/**
 * @class HttpRoomUserListContent
 * @brief 서버가 클라이언트에게 room 내 유저 목록 정보를 보내기 위한 http 요청 메시지
 */
public class HttpRoomUserListContent extends HttpMessage {

    private HashSet<String> roomUserListSet = new HashSet<>();

    public HttpRoomUserListContent() {
        // nothing
    }

    public void addAllRoomUserList(HashSet<String> roomUserKeySet) {
        roomUserListSet.addAll(roomUserKeySet);
    }

    public boolean isEmpty() { return roomUserListSet.isEmpty(); }
    public HashSet<String> getRoomUserListSet() { return roomUserListSet; }

}
