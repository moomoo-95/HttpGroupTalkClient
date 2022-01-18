package moomoo.hgtp.client.protocol.hgtp.message.request.handler;

import moomoo.hgtp.client.network.NetworkManager;
import moomoo.hgtp.client.protocol.hgtp.message.base.HgtpHeader;
import moomoo.hgtp.client.protocol.hgtp.message.base.HgtpMessageType;
import moomoo.hgtp.client.protocol.hgtp.message.request.*;
import moomoo.hgtp.client.service.AppInstance;
import network.definition.DestinationRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HgtpRequestHandler {

    private static final Logger log = LoggerFactory.getLogger(HgtpRequestHandler.class);

    private static AppInstance appInstance = AppInstance.getInstance();

    public HgtpRequestHandler() {
        // nothing
    }

    public boolean unregisterRequestProcessing(HgtpUnregisterRequest hgtpUnregisterRequest) {
        HgtpHeader hgtpHeader = hgtpUnregisterRequest.getHgtpHeader();
        log.debug("({}) () () RECV HGTP MSG [{}]", hgtpHeader.getUserId(), hgtpUnregisterRequest);

        return true;
    }

    public boolean createRoomRequestProcessing(HgtpCreateRoomRequest hgtpCreateRoomRequest) {
        log.debug("({}) () () RECV HGTP MSG [{}]", hgtpCreateRoomRequest.getHgtpHeader().getUserId(), hgtpCreateRoomRequest);
        return true;
    }

    public boolean deleteRoomRequestProcessing(HgtpDeleteRoomRequest hgtpDeleteRoomRequest) {
        log.debug("({}) () () RECV HGTP MSG [{}]", hgtpDeleteRoomRequest.getHgtpHeader().getUserId(), hgtpDeleteRoomRequest);
        return true;
    }

    public boolean joinRoomRequestProcessing(HgtpJoinRoomRequest hgtpJoinRoomRequest) {
        log.debug("({}) () () RECV HGTP MSG [{}]", hgtpJoinRoomRequest.getHgtpHeader().getUserId(), hgtpJoinRoomRequest);
        return true;
    }

    public boolean exitRoomRequestProcessing(HgtpExitRoomRequest hgtpExitRoomRequest) {
        log.debug("({}) () () RECV HGTP MSG [{}]", hgtpExitRoomRequest.getHgtpHeader().getUserId(), hgtpExitRoomRequest);
        return true;
    }

    public boolean inviteUserFromRoomRequestProcessing(HgtpInviteUserFromRoomRequest hgtpInviteUserFromRoomRequest) {
        log.debug("({}) () () RECV HGTP MSG [{}]", hgtpInviteUserFromRoomRequest.getHgtpHeader().getUserId(), hgtpInviteUserFromRoomRequest);
        return true;
    }

    public boolean removeUserFromRoomRequestProcessing(HgtpRemoveUserFromRoomRequest hgtpRemoveUserFromRoomRequest) {
        log.debug("({}) () () RECV HGTP MSG [{}]", hgtpRemoveUserFromRoomRequest.getHgtpHeader().getUserId(), hgtpRemoveUserFromRoomRequest);
        return true;
    }

    public void sendRegisterRequest(HgtpRegisterRequest hgtpRegisterRequest, String nonce) {
        if (nonce != null) {
            hgtpRegisterRequest.getHgtpContent().setNonce(hgtpRegisterRequest.getHgtpHeader(), nonce);
        }

        byte[] data = hgtpRegisterRequest.getByteData();

        DestinationRecord destinationRecord = NetworkManager.getInstance().getHgtpGroupSocket().getDestination(AppInstance.SERVER_SESSION_ID);
        if (destinationRecord == null) {
            log.warn("({}) () () DestinationRecord Channel is null.", appInstance.getUserId());
        }

        destinationRecord.getNettyChannel().sendData(data, data.length);
        log.debug("({}) () () [{}] SEND DATA {}", appInstance.getUserId(), HgtpMessageType.REQUEST_HASHMAP.get(HgtpMessageType.REGISTER), hgtpRegisterRequest);
    }

}
