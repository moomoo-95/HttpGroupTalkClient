package moomoo.hgtp.grouptalk.fsm;

public class HgtpEvent {

    public static final String REGISTER = "REGISTER";
    public static final String REGISTER_SUC = "REGISTER_SUC";
    public static final String REGISTER_FAIL = "REGISTER_FAIL";

    public static final String UNREGISTER = "UNREGISTER";

    public static final String CREATE_ROOM = "CREATE_ROOM";
    public static final String CREATE_ROOM_SUC = "CREATE_ROOM_SUC";
    public static final String CREATE_ROOM_FAIL = "CREATE_ROOM_FAIL";

    public static final String DELETE_ROOM = "DELETE_ROOM";
    public static final String DELETE_ROOM_FAIL = "DELETE_ROOM_FAIL";
    public static final String DELETE_ROOM_SUC = "DELETE_ROOM_SUC";

    public static final String JOIN_ROOM = "JOIN_ROOM";
    public static final String JOIN_ROOM_SUC = "JOIN_ROOM_SUC";
    public static final String JOIN_ROOM_FAIL = "JOIN_ROOM_FAIL";

    public static final String INVITE_USER_ROOM = "INVITE_USER_ROOM";
    public static final String INVITE_USER_ROOM_SUC = "INVITE_USER_ROOM_SUC";
    public static final String INVITE_USER_ROOM_FAIL = "INVITE_USER_ROOM_FAIL";

    public static final String REMOVE_USER_ROOM = "REMOVE_USER_ROOM";
    public static final String REMOVE_USER_ROOM_SUC = "REMOVE_USER_ROOM_SUC";
    public static final String REMOVE_USER_ROOM_FAIL = "REMOVE_USER_ROOM_FAIL";

    public static final String EXIT_ROOM = "EXIT_ROOM";
    public static final String EXIT_ROOM_SUC = "EXIT_ROOM_SUC";
    public static final String EXIT_ROOM_FAIL = "EXIT_ROOM_FAIL";

    public static final String TO_TALK = "TO_TALK";

    private HgtpEvent() {
        // nothing
    }
}
