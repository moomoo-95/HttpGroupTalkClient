package moomoo.hgtp.grouptalk.protocol.http.message.content;

import moomoo.hgtp.grouptalk.protocol.http.base.HttpMessage;

import java.util.HashSet;

public class HttpUserListContent extends HttpMessage {

    private HashSet<String> userListSet = new HashSet<>();

    public HttpUserListContent() {
        // nothing
    }

    public void addAllUserList(HashSet<String> roomKeySet) {
        userListSet.addAll(roomKeySet);
    }

    public boolean isEmpty() { return userListSet.isEmpty(); }
    public HashSet<String> getUserListSet() { return userListSet; }

}
