package moomoo.hgtp.grouptalk.protocol.http.base;

public class HttpMessageType {
    // header key
    public static final String MESSAGE_TYPE = "message-type";
    // header value
    public static final String ROOM_LIST = "room_list";
    public static final String USER_LIST = "user_list";
    public static final String ROOM_USER_LIST = "room_user_list";
    public static final String MESSAGE = "message";
    public static final String NOTICE = "notice";
    public static final String REFRESH = "refresh";

    public static final String APPLICATION_JSON = "application/json";

    private HttpMessageType() {
        // nothing
    }
}
