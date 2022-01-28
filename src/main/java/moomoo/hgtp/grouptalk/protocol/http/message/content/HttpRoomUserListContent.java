package moomoo.hgtp.grouptalk.protocol.http.message.content;

import moomoo.hgtp.grouptalk.protocol.http.base.HttpMessage;

import java.util.HashSet;

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
