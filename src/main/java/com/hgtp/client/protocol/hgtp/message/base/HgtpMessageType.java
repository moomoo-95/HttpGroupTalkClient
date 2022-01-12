package com.hgtp.client.protocol.hgtp.message.base;

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

    public static final short OK = 0x20;
    public static final short BAD_REQUEST = 0x40;
    public static final short UNAUTHORIZED = 0x41;
    public static final short FORBIDDEN = 0x43;
    public static final short SERVER_UNAVAILABLE = 0x53;
    public static final short DECLINE = 0x63;

    private HgtpMessageType() {
        // nothing
    }

    //    public static final Map<String, Short> REQUEST_HASHMAP = new HashMap<String, Short>() {
//        {
//        put("REGISTER", REGISTER);
//        put("UNREGISTER", UNREGISTER);
//        put("CREATE_ROOM", CREATE_ROOM);
//        put("DELETE_ROOM", DELETE_ROOM);
//        put("JOIN_ROOM", JOIN_ROOM);
//        put("EXIT_ROOM", EXIT_ROOM);
//        put("INVITE_USER_FROM_ROOM", INVITE_USER_FROM_ROOM);
//        put("REMOVE_USER_FROM_ROOM", REMOVE_USER_FROM_ROOM);
//    } };
//
//    public static final Map<String, Short> RESPONSE_HASHMAP = new HashMap<String, Short>() { {
//        put("OK_200", OK_200);
//        put("BAD_REQUEST_400", BAD_REQUEST_400);
//        put("UNAUTHORIZED_401", UNAUTHORIZED_401);
//        put("FORBIDDEN_403", FORBIDDEN_403);
//        put("SERVER_UNAVAILABLE_503", SERVER_UNAVAILABLE_503);
//        put("DECLINE_603", DECLINE_603);
//    } };
}
