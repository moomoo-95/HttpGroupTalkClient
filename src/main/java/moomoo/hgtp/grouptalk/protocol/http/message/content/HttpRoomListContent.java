package moomoo.hgtp.grouptalk.protocol.http.message.content;

import moomoo.hgtp.grouptalk.protocol.http.base.HttpMessage;

import java.util.HashSet;

public class HttpRoomListContent extends HttpMessage {

    private HashSet<String> roomListSet = new HashSet<>();

    public HttpRoomListContent() {
        // nothing
    }

    public void addAllRoomList(HashSet<String> roomKeySet) {
        roomListSet.addAll(roomKeySet);
    }

    public boolean isEmpty() { return roomListSet.isEmpty(); }
    public HashSet<String> getRoomListSet() { return roomListSet; }

}
