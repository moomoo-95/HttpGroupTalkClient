package moomoo.hgtp.client.protocol.hgtp;

import moomoo.hgtp.client.protocol.hgtp.exception.HgtpException;
import moomoo.hgtp.client.protocol.hgtp.message.base.HgtpHeader;
import moomoo.hgtp.client.protocol.hgtp.message.base.HgtpMessageType;
import moomoo.hgtp.client.protocol.hgtp.message.request.handler.HgtpRequestHandler;
import moomoo.hgtp.client.protocol.hgtp.message.response.HgtpCommonResponse;
import moomoo.hgtp.client.protocol.hgtp.message.response.HgtpUnauthorizedResponse;
import moomoo.hgtp.client.protocol.hgtp.message.response.handler.HgtpResponseHandler;
import moomoo.hgtp.client.protocol.hgtp.message.request.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.module.ConcurrentCyclicFIFO;

public class HgtpConsumer implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(HgtpResponseHandler.class);

    private final ConcurrentCyclicFIFO<byte[]> hgtpQueue;
    private boolean isQuit = false;

    public HgtpConsumer(ConcurrentCyclicFIFO<byte[]> hgtpQueue) {
        this.hgtpQueue = hgtpQueue;
    }

    @Override
    public void run() {
        queueProcessing();
    }

    private void queueProcessing() {
        while (!isQuit) {
            try {
                byte[] data = hgtpQueue.take();
                parseHgtpMessage(data);
            } catch (InterruptedException e) {
                log.error("() () () HgtpConsumer.queueProcessing ", e);
                isQuit = true;
            }
        }
    }

    public void parseHgtpMessage(byte[] data) {
        try {
            HgtpHeader hgtpHeader = new HgtpHeader(data);
            if (hgtpHeader == null) {
                log.warn("This is not HGTP Message. [{}]", data);
                return;
            }

            log.debug("({}) () () RECV MSG TYPE : {}", hgtpHeader.getUserId(), hgtpHeader.getMessageType());
            switch (hgtpHeader.getMessageType()){
                case HgtpMessageType.REGISTER:
                    HgtpRegisterRequest hgtpRegisterRequest = new HgtpRegisterRequest(data);
                    HgtpRequestHandler.registerRequestProcessing(hgtpRegisterRequest);
                    break;
                case HgtpMessageType.UNREGISTER:
                    HgtpUnregisterRequest hgtpUnregisterRequest = new HgtpUnregisterRequest(data);
                    HgtpRequestHandler.unregisterRequestProcessing(hgtpUnregisterRequest);
                    break;
                case HgtpMessageType.CREATE_ROOM:
                    HgtpCreateRoomRequest hgtpCreateRoomRequest = new HgtpCreateRoomRequest(data);
                    HgtpRequestHandler.createRoomRequestProcessing(hgtpCreateRoomRequest);
                    break;
                case HgtpMessageType.DELETE_ROOM:
                    HgtpDeleteRoomRequest hgtpDeleteRoomRequest = new HgtpDeleteRoomRequest(data);
                    HgtpRequestHandler.deleteRoomRequestProcessing(hgtpDeleteRoomRequest);
                    break;
                case HgtpMessageType.JOIN_ROOM:
                    HgtpJoinRoomRequest hgtpJoinRoomRequest = new HgtpJoinRoomRequest(data);
                    HgtpRequestHandler.joinRoomRequestProcessing(hgtpJoinRoomRequest);
                    break;
                case HgtpMessageType.EXIT_ROOM:
                    HgtpExitRoomRequest hgtpExitRoomRequest = new HgtpExitRoomRequest(data);
                    HgtpRequestHandler.exitRoomRequestProcessing(hgtpExitRoomRequest);
                    break;
                case HgtpMessageType.INVITE_USER_FROM_ROOM:
                    HgtpInviteUserFromRoomRequest hgtpInviteUserFromRoomRequest = new HgtpInviteUserFromRoomRequest(data);
                    HgtpRequestHandler.inviteUserFromRoomRequestProcessing(hgtpInviteUserFromRoomRequest);
                    break;
                case HgtpMessageType.REMOVE_USER_FROM_ROOM:
                    HgtpRemoveUserFromRoomRequest hgtpRemoveUserFromRoomRequest = new HgtpRemoveUserFromRoomRequest(data);
                    HgtpRequestHandler.removeUserFromRoomRequestProcessing(hgtpRemoveUserFromRoomRequest);
                    break;
                case HgtpMessageType.OK:
                    HgtpCommonResponse hgtpOkResponse = new HgtpCommonResponse(data);
                    HgtpResponseHandler.okResponseProcessing(hgtpOkResponse);
                    break;
                case HgtpMessageType.BAD_REQUEST:
                    HgtpCommonResponse hgtpBadRequestResponse = new HgtpCommonResponse(data);
                    HgtpResponseHandler.badRequestResponseProcessing(hgtpBadRequestResponse);
                    break;
                case HgtpMessageType.UNAUTHORIZED:
                    HgtpUnauthorizedResponse hgtpUnauthorizedResponse = new HgtpUnauthorizedResponse(data);
                    HgtpResponseHandler.unauthorizedResponseProcessing(hgtpUnauthorizedResponse);
                    break;
                case HgtpMessageType.FORBIDDEN:
                    HgtpCommonResponse hgtpForbiddenResponse = new HgtpCommonResponse(data);
                    HgtpResponseHandler.forbiddenResponseProcessing(hgtpForbiddenResponse);
                    break;
                case HgtpMessageType.SERVER_UNAVAILABLE:
                    HgtpCommonResponse hgtpServerUnavailableResponse = new HgtpCommonResponse(data);
                    HgtpResponseHandler.serverUnavailableResponseProcessing(hgtpServerUnavailableResponse);
                    break;
                case HgtpMessageType.DECLINE:
                    HgtpCommonResponse hgtpDeclineResponse = new HgtpCommonResponse(data);
                    HgtpResponseHandler.declineResponseProcessing(hgtpDeclineResponse);
                    break;
                case HgtpMessageType.UNKNOWN:
                    log.warn("({}) () () Unknown message cannot be processed.", hgtpHeader.getUserId());
                    break;
                default:
                    log.warn("({}) () () Undefined message cannot be processed.", hgtpHeader.getUserId());
                    break;
            }
        } catch (HgtpException e) {
            log.error("HgtpResponseHandler.HgtpResponseProcessing ", e);
        }
    }

}
