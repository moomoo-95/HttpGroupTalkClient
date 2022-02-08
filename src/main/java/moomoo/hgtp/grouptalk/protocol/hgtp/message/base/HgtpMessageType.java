package moomoo.hgtp.grouptalk.protocol.hgtp.message.base;

import java.util.HashMap;
import java.util.Map;

public class HgtpMessageType {
    public static final short UNKNOWN = 0x00;

    public static final short REGISTER = 0x80;
    public static final short UNREGISTER = 0x81;
    public static final short CREATE_ROOM = 0x90;
    public static final short DELETE_ROOM = 0x91;
    public static final short JOIN_ROOM = 0x92;
    public static final short EXIT_ROOM = 0x93;
    public static final short INVITE_USER_FROM_ROOM = 0xA0;
    public static final short REMOVE_USER_FROM_ROOM = 0xA1;
    public static final short REFRESH = 0xB0;

    public static final short OK = 0x20;
    public static final short BAD_REQUEST = 0x40;
    public static final short UNAUTHORIZED = 0x41;
    public static final short FORBIDDEN = 0x43;
    public static final short SERVER_UNAVAILABLE = 0x53;
    public static final short DECLINE = 0x63;

    public static final Map<Short, String> REQUEST_HASHMAP = new HashMap<>();
    public static final Map<Short, String> RESPONSE_HASHMAP = new HashMap<>();
    public static final Map<Short, String> HGTP_HASHMAP = new HashMap<>();

    static {
        REQUEST_HASHMAP.put(REGISTER, "REGISTER");
        REQUEST_HASHMAP.put(UNREGISTER, "UNREGISTER");
        REQUEST_HASHMAP.put(CREATE_ROOM, "CREATE_ROOM");
        REQUEST_HASHMAP.put(DELETE_ROOM, "DELETE_ROOM");
        REQUEST_HASHMAP.put(JOIN_ROOM, "JOIN_ROOM");
        REQUEST_HASHMAP.put(EXIT_ROOM, "EXIT_ROOM");
        REQUEST_HASHMAP.put(INVITE_USER_FROM_ROOM, "INVITE_USER_FROM_ROOM");
        REQUEST_HASHMAP.put(REMOVE_USER_FROM_ROOM, "REMOVE_USER_FROM_ROOM");
        REQUEST_HASHMAP.put(REFRESH, "REFRESH");

        RESPONSE_HASHMAP.put(OK, "OK");
        RESPONSE_HASHMAP.put(BAD_REQUEST, "BAD_REQUEST");
        RESPONSE_HASHMAP.put(UNAUTHORIZED, "UNAUTHORIZED");
        RESPONSE_HASHMAP.put(FORBIDDEN, "FORBIDDEN");
        RESPONSE_HASHMAP.put(SERVER_UNAVAILABLE, "SERVER_UNAVAILABLE");
        RESPONSE_HASHMAP.put(DECLINE, "DECLINE");

        HGTP_HASHMAP.putAll(REQUEST_HASHMAP);
        HGTP_HASHMAP.putAll(RESPONSE_HASHMAP);
    }

    private HgtpMessageType() {
        // nothing
    }
}
