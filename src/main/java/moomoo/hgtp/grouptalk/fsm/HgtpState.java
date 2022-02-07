package moomoo.hgtp.grouptalk.fsm;

import java.util.HashSet;

public class HgtpState {
    public static final String NAME = "HgtpState";

    public static final String IDLE = "IDLE";
    public static final String REGISTER = "REGISTER";
    public static final String CREATE = "CREATE";
    public static final String CREATE_OK = "CREATE_OK";
    public static final String JOIN = "JOIN";
    public static final String JOIN_OK = "JOIN_OK";
    public static final String INVITE = "INVITE";
    public static final String TALK = "TALK";
    public static final String REMOVE = "REMOVE";
    public static final String EXIT = "EXIT";
    public static final String DELETE = "DELETE";

    protected static final HashSet<String> TO_TALK = new HashSet<>();

    static {
        TO_TALK.add(CREATE_OK);
        TO_TALK.add(JOIN_OK);
    }

    private HgtpState() {
        // nothing
    }
}
