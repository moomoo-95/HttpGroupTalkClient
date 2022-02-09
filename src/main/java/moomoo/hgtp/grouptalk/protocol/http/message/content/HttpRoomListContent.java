package moomoo.hgtp.grouptalk.protocol.http.message.content;

import moomoo.hgtp.grouptalk.protocol.http.base.HttpMessage;

import java.util.HashSet;

/**
 * @class HttpRoomListContent
 * @brief 서버가 클라이언트에게 room 목록 정보를 보내기 위한 http 요청 메시지
 */
public class HttpRoomListContent extends HttpMessage {

    private final HashSet<String> roomListSet = new HashSet<>();

    public HttpRoomListContent() {
        // nothing
    }

    public void addAllRoomList(HashSet<String> roomKeySet) {
        roomListSet.addAll(roomKeySet);
    }

    public boolean isEmpty() { return roomListSet.isEmpty(); }
    public HashSet<String> getRoomListSet() { return roomListSet; }

}
