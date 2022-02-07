package moomoo.hgtp.grouptalk.fsm;


import com.fsm.StateManager;
import com.fsm.module.StateHandler;

public class HgtpFsmManager {

    private final StateManager stateManager = new StateManager(8);

    public HgtpFsmManager() {
        // nothing
    }

    public void initState(){
        stateManager.addStateHandler(HgtpState.NAME);
        StateHandler hgtpStateHandler = stateManager.getStateHandler(HgtpState.NAME);

        // REGISTER
        hgtpStateHandler.addState(HgtpEvent.REGISTER, HgtpState.IDLE, HgtpState.REGISTER, null, null, null, 0, 0 );
        // REGISTER_FAIL
        hgtpStateHandler.addState(HgtpEvent.REGISTER_FAIL, HgtpState.REGISTER, HgtpState.IDLE, null, null, null, 0, 0 );
        // UNREGISTER
        hgtpStateHandler.addState(HgtpEvent.UNREGISTER, HgtpState.REGISTER, HgtpState.IDLE, null, null, null, 0, 0 );

        // CREATE_ROOM
        hgtpStateHandler.addState(HgtpEvent.CREATE_ROOM, HgtpState.REGISTER, HgtpState.CREATE, null, null, null, 0, 0 );
        // CREATE_ROOM_SUC
        hgtpStateHandler.addState(HgtpEvent.CREATE_ROOM_SUC, HgtpState.CREATE, HgtpState.CREATE_OK, null, null, null, 0, 0 );
        // CREATE_ROOM_FAIL
        hgtpStateHandler.addState(HgtpEvent.CREATE_ROOM_FAIL, HgtpState.CREATE, HgtpState.REGISTER, null, null, null, 0, 0 );

        // DELETE_ROOM
        hgtpStateHandler.addState(HgtpEvent.DELETE_ROOM, HgtpState.TALK, HgtpState.DELETE, null, null, null, 0, 0 );
        // DELETE_ROOM_FAIL
        hgtpStateHandler.addState(HgtpEvent.DELETE_ROOM_FAIL, HgtpState.DELETE, HgtpState.TALK, null, null, null, 0, 0 );
        // DELETE_ROOM_SUC
        hgtpStateHandler.addState(HgtpEvent.DELETE_ROOM_SUC, HgtpState.DELETE, HgtpState.REGISTER, null, null, null, 0, 0 );

        // JOIN_ROOM
        hgtpStateHandler.addState(HgtpEvent.JOIN_ROOM, HgtpState.REGISTER, HgtpState.JOIN, null, null, null, 0, 0 );
        // JOIN_ROOM_SUC
        hgtpStateHandler.addState(HgtpEvent.JOIN_ROOM_SUC, HgtpState.JOIN, HgtpState.JOIN_OK, null, null, null, 0, 0 );
        // JOIN_ROOM_FAIL
        hgtpStateHandler.addState(HgtpEvent.JOIN_ROOM_FAIL, HgtpState.JOIN, HgtpState.REGISTER, null, null, null, 0, 0 );

        // INVITE_USER_ROOM
        hgtpStateHandler.addState(HgtpEvent.INVITE_USER_ROOM, HgtpState.REGISTER, HgtpState.INVITE, null, null, null, 0, 0 );
        // INVITE_USER_ROOM_SUC
        hgtpStateHandler.addState(HgtpEvent.INVITE_USER_ROOM_SUC, HgtpState.INVITE, HgtpState.JOIN_OK, null, null, null, 0, 0 );
        // INVITE_USER_ROOM_FAIL
        hgtpStateHandler.addState(HgtpEvent.INVITE_USER_ROOM_FAIL, HgtpState.INVITE, HgtpState.REGISTER, null, null, null, 0, 0 );

        // REMOVE_USER_ROOM
        hgtpStateHandler.addState(HgtpEvent.REMOVE_USER_ROOM, HgtpState.REGISTER, HgtpState.REMOVE, null, null, null, 0, 0 );
        // REMOVE_USER_ROOM_SUC
        hgtpStateHandler.addState(HgtpEvent.REMOVE_USER_ROOM_SUC, HgtpState.REMOVE, HgtpState.TALK, null, null, null, 0, 0 );
        // REMOVE_USER_ROOM_FAIL
        hgtpStateHandler.addState(HgtpEvent.REMOVE_USER_ROOM_FAIL, HgtpState.REMOVE, HgtpState.REGISTER, null, null, null, 0, 0 );

        // EXIT_ROOM
        hgtpStateHandler.addState(HgtpEvent.EXIT_ROOM, HgtpState.TALK, HgtpState.EXIT, null, null, null, 0, 0 );
        // EXIT_ROOM_SUC
        hgtpStateHandler.addState(HgtpEvent.EXIT_ROOM_SUC, HgtpState.EXIT, HgtpState.REGISTER, null, null, null, 0, 0 );
        // EXIT_ROOM_FAIL
        hgtpStateHandler.addState(HgtpEvent.EXIT_ROOM_FAIL, HgtpState.EXIT, HgtpState.TALK, null, null, null, 0, 0 );

        // TO_TALK
        hgtpStateHandler.addState(HgtpEvent.TO_TALK, HgtpState.TO_TALK, HgtpState.TALK, null, null, null, 0, 0 );
    }

    public StateManager getStateManager() {
        return stateManager;
    }
}
