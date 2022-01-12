package protocol.hgtp.message.request.handler;

import protocol.hgtp.message.request.*;

public class HgtpRequestHandler {

    public HgtpRequestHandler() {
        // nothing
    }

    public static boolean registerRequestProcessing(HgtpRegisterRequest hgtpRegisterRequest) {
        return true;
    }

    public static boolean unregisterRequestProcessing(HgtpUnregisterRequest hgtpUnregisterRequest) {
        return true;
    }

    public static boolean createRoomRequestProcessing(HgtpCreateRoomRequest hgtpCreateRoomRequest) {
        return true;
    }

    public static boolean deleteRoomRequestProcessing(HgtpDeleteRoomRequest hgtpDeleteRoomRequest) {
        return true;
    }

    public static boolean joinRoomRequestProcessing(HgtpJoinRoomRequest hgtpJoinRoomRequest) {
        return true;
    }

    public static boolean exitRoomRequestProcessing(HgtpExitRoomRequest hgtpExitRoomRequest) {
        return true;
    }

    public static boolean inviteUserFromRoomRequestProcessing(HgtpInviteUserFromRoomRequest hgtpInviteUserFromRoomRequest) {
        return true;
    }

    public static boolean removeUserFromRoomRequestProcessing(HgtpRemoveUserFromRoomRequest hgtpRemoveUserFromRoomRequest) {
        return true;
    }

}
