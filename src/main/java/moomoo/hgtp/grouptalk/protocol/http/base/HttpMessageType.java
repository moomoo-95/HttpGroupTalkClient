package moomoo.hgtp.grouptalk.protocol.http.base;

public class HttpMessageType {
    // http message parsing number
    public static final int PARSE_MESSAGE = 0;
    public static final int PARSE_CONTENT = 1;
    public static final int PARSE_SIZE = 2;

    // header key
    public static final String MESSAGE_TYPE = "message-type";
    // header value
    public static final String ROOM_LIST = "room_list";
    public static final String USER_LIST = "user_list";
    public static final String ROOM_USER_LIST = "room_user_list";
    public static final String MESSAGE = "message";
    public static final String NOTICE = "notice";

    private HttpMessageType() {
        // nothing
    }
}
