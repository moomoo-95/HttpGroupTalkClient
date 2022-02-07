package moomoo.hgtp.grouptalk.fsm;


import com.fsm.StateManager;
import com.fsm.module.StateHandler;

public class HgtpFsmManager {

    private final StateManager stateManager = new StateManager(8);
    private final StateHandler stateHandler;

    public HgtpFsmManager() {
        stateManager.addStateHandler(HgtpState.NAME);
        stateHandler = stateManager.getStateHandler(HgtpState.NAME);
    }

    public void initState(){

        // REGISTER
        stateHandler.addState(HgtpEvent.REGISTER, HgtpState.IDLE, HgtpState.REGISTER, null, null, null, 0, 0 );
        // REGISTER_FAIL
        stateHandler.addState(HgtpEvent.REGISTER_SUC, HgtpState.REGISTER, HgtpState.REGISTER_OK, null, null, null, 0, 0 );
        // REGISTER_SUC
        stateHandler.addState(HgtpEvent.REGISTER_FAIL, HgtpState.REGISTER, HgtpState.IDLE, null, null, null, 0, 0 );

        // UNREGISTER
        stateHandler.addState(HgtpEvent.UNREGISTER, HgtpState.REGISTER_OK, HgtpState.IDLE, null, null, null, 0, 0 );

        // CREATE_ROOM
        stateHandler.addState(HgtpEvent.CREATE_ROOM, HgtpState.REGISTER_OK, HgtpState.CREATE, null, null, null, 0, 0 );
        // CREATE_ROOM_SUC
        stateHandler.addState(HgtpEvent.CREATE_ROOM_SUC, HgtpState.CREATE, HgtpState.CREATE_OK, null, null, null, 0, 0 );
        // CREATE_ROOM_FAIL
        stateHandler.addState(HgtpEvent.CREATE_ROOM_FAIL, HgtpState.CREATE, HgtpState.REGISTER_OK, null, null, null, 0, 0 );

        // DELETE_ROOM
        stateHandler.addState(HgtpEvent.DELETE_ROOM, HgtpState.TALK, HgtpState.DELETE, null, null, null, 0, 0 );
        // DELETE_ROOM_FAIL
        stateHandler.addState(HgtpEvent.DELETE_ROOM_FAIL, HgtpState.DELETE, HgtpState.TALK, null, null, null, 0, 0 );
        // DELETE_ROOM_SUC
        stateHandler.addState(HgtpEvent.DELETE_ROOM_SUC, HgtpState.DELETE, HgtpState.REGISTER_OK, null, null, null, 0, 0 );

        // JOIN_ROOM
        stateHandler.addState(HgtpEvent.JOIN_ROOM, HgtpState.REGISTER_OK, HgtpState.JOIN, null, null, null, 0, 0 );
        // JOIN_ROOM_SUC
        stateHandler.addState(HgtpEvent.JOIN_ROOM_SUC, HgtpState.JOIN, HgtpState.JOIN_OK, null, null, null, 0, 0 );
        // JOIN_ROOM_FAIL
        stateHandler.addState(HgtpEvent.JOIN_ROOM_FAIL, HgtpState.JOIN, HgtpState.REGISTER_OK, null, null, null, 0, 0 );

        // INVITE_USER_ROOM
        stateHandler.addState(HgtpEvent.INVITE_USER_ROOM, HgtpState.REGISTER_OK, HgtpState.INVITE, null, null, null, 0, 0 );
        // INVITE_USER_ROOM_SUC
        stateHandler.addState(HgtpEvent.INVITE_USER_ROOM_SUC, HgtpState.INVITE, HgtpState.JOIN_OK, null, null, null, 0, 0 );
        // INVITE_USER_ROOM_FAIL
        stateHandler.addState(HgtpEvent.INVITE_USER_ROOM_FAIL, HgtpState.INVITE, HgtpState.REGISTER_OK, null, null, null, 0, 0 );

        // REMOVE_USER_ROOM
        stateHandler.addState(HgtpEvent.REMOVE_USER_ROOM, HgtpState.REGISTER_OK, HgtpState.REMOVE, null, null, null, 0, 0 );
        // REMOVE_USER_ROOM_SUC
        stateHandler.addState(HgtpEvent.REMOVE_USER_ROOM_SUC, HgtpState.REMOVE, HgtpState.TALK, null, null, null, 0, 0 );
        // REMOVE_USER_ROOM_FAIL
        stateHandler.addState(HgtpEvent.REMOVE_USER_ROOM_FAIL, HgtpState.REMOVE, HgtpState.REGISTER_OK, null, null, null, 0, 0 );

        // EXIT_ROOM
        stateHandler.addState(HgtpEvent.EXIT_ROOM, HgtpState.TALK, HgtpState.EXIT, null, null, null, 0, 0 );
        // EXIT_ROOM_SUC
        stateHandler.addState(HgtpEvent.EXIT_ROOM_SUC, HgtpState.EXIT, HgtpState.REGISTER_OK, null, null, null, 0, 0 );
        // EXIT_ROOM_FAIL
        stateHandler.addState(HgtpEvent.EXIT_ROOM_FAIL, HgtpState.EXIT, HgtpState.TALK, null, null, null, 0, 0 );

        // TO_TALK
        stateHandler.addState(HgtpEvent.TO_TALK, HgtpState.TO_TALK, HgtpState.TALK, null, null, null, 0, 0 );

        stateHandler.addEventCondition(new ToTalkEventCondition(stateManager, stateHandler.getEvent(HgtpEvent.TO_TALK)), 1000);
    }

    public StateManager getStateManager() {
        return stateManager;
    }

    public StateHandler getStateHandler() {
        return stateHandler;
    }
}
