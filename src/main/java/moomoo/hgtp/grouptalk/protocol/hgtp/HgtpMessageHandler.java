package moomoo.hgtp.grouptalk.protocol.hgtp;

import moomoo.hgtp.grouptalk.protocol.hgtp.exception.HgtpException;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.base.HgtpHeader;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.base.HgtpMessageType;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.request.*;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.request.handler.HgtpRequestHandler;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.response.HgtpCommonResponse;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.response.HgtpUnauthorizedResponse;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.response.handler.HgtpResponseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.scheduler.job.Job;
import service.scheduler.schedule.ScheduleManager;
import util.module.ConcurrentCyclicFIFO;

import java.util.concurrent.TimeUnit;

public class HgtpMessageHandler extends Job {

    private static final Logger log = LoggerFactory.getLogger(HgtpMessageHandler.class);

    private final ConcurrentCyclicFIFO<byte[]> hgtpMessageQueue;
    private final HgtpRequestHandler hgtpRequestHandler = new HgtpRequestHandler();
    private final HgtpResponseHandler hgtpResponseHandler = new HgtpResponseHandler();

    private boolean isQuit = false;

    public HgtpMessageHandler(ScheduleManager scheduleManager, int initialDelay, int interval, TimeUnit timeUnit, int priority, int totalRunCount, boolean isLasted, ConcurrentCyclicFIFO<byte[]> hgtpMessageQueue) {
        super(scheduleManager, HgtpMessageHandler.class.getSimpleName(), initialDelay, interval, timeUnit, priority, totalRunCount, isLasted);
        this.hgtpMessageQueue = hgtpMessageQueue;
    }

    @Override
    public void run() {
        queueProcessing();
    }

    private void queueProcessing() {
        while (!isQuit) {
            try {
                byte[] data = hgtpMessageQueue.take();
                parseHgtpMessage(data);
            } catch (InterruptedException e) {
                log.error("() () () HgtpConsumer.queueProcessing ", e);
                isQuit = true;
            }
        }
    }

    /**
     * @fn parseHgtpMessage
     * @brief byte 형태로 들어온 hgtp 메시지의 타입을 분석하는 메서드
     * @param data
     */
    private void parseHgtpMessage(byte[] data) {
        try {
            HgtpHeader hgtpHeader = new HgtpHeader(data);

            log.debug("({}) () () RECV MSG TYPE : {}", hgtpHeader.getUserId(), HgtpMessageType.HGTP_HASHMAP.get(hgtpHeader.getMessageType()));
            switch (hgtpHeader.getMessageType()){
                case HgtpMessageType.REGISTER:
                    HgtpRegisterRequest hgtpRegisterRequest = new HgtpRegisterRequest(data);
                    hgtpRequestHandler.registerRequestProcessing(hgtpRegisterRequest);
                    break;
                case HgtpMessageType.UNREGISTER:
                    HgtpUnregisterRequest hgtpUnregisterRequest = new HgtpUnregisterRequest(data);
                    hgtpRequestHandler.unregisterRequestProcessing(hgtpUnregisterRequest);
                    break;
                case HgtpMessageType.CREATE_ROOM:
                    HgtpCreateRoomRequest hgtpCreateRoomRequest = new HgtpCreateRoomRequest(data);
                    hgtpRequestHandler.createRoomRequestProcessing(hgtpCreateRoomRequest);
                    break;
                case HgtpMessageType.DELETE_ROOM:
                    HgtpDeleteRoomRequest hgtpDeleteRoomRequest = new HgtpDeleteRoomRequest(data);
                    hgtpRequestHandler.deleteRoomRequestProcessing(hgtpDeleteRoomRequest);
                    break;
                case HgtpMessageType.JOIN_ROOM:
                    HgtpJoinRoomRequest hgtpJoinRoomRequest = new HgtpJoinRoomRequest(data);
                    hgtpRequestHandler.joinRoomRequestProcessing(hgtpJoinRoomRequest);
                    break;
                case HgtpMessageType.EXIT_ROOM:
                    HgtpExitRoomRequest hgtpExitRoomRequest = new HgtpExitRoomRequest(data);
                    hgtpRequestHandler.exitRoomRequestProcessing(hgtpExitRoomRequest);
                    break;
                case HgtpMessageType.INVITE_USER_FROM_ROOM:
                    HgtpInviteUserFromRoomRequest hgtpInviteUserFromRoomRequest = new HgtpInviteUserFromRoomRequest(data);
                    hgtpRequestHandler.inviteUserFromRoomRequestProcessing(hgtpInviteUserFromRoomRequest);
                    break;
                case HgtpMessageType.REMOVE_USER_FROM_ROOM:
                    HgtpRemoveUserFromRoomRequest hgtpRemoveUserFromRoomRequest = new HgtpRemoveUserFromRoomRequest(data);
                    hgtpRequestHandler.removeUserFromRoomRequestProcessing(hgtpRemoveUserFromRoomRequest);
                    break;
                case HgtpMessageType.OK:
                    HgtpCommonResponse hgtpOkResponse = new HgtpCommonResponse(data);
                    hgtpResponseHandler.okResponseProcessing(hgtpOkResponse);
                    break;
                case HgtpMessageType.BAD_REQUEST:
                    HgtpCommonResponse hgtpBadRequestResponse = new HgtpCommonResponse(data);
                    hgtpResponseHandler.badRequestResponseProcessing(hgtpBadRequestResponse);
                    break;
                case HgtpMessageType.UNAUTHORIZED:
                    HgtpUnauthorizedResponse hgtpUnauthorizedResponse = new HgtpUnauthorizedResponse(data);
                    hgtpResponseHandler.unauthorizedResponseProcessing(hgtpUnauthorizedResponse);
                    break;
                case HgtpMessageType.FORBIDDEN:
                    HgtpCommonResponse hgtpForbiddenResponse = new HgtpCommonResponse(data);
                    hgtpResponseHandler.forbiddenResponseProcessing(hgtpForbiddenResponse);
                    break;
                case HgtpMessageType.SERVER_UNAVAILABLE:
                    HgtpCommonResponse hgtpServerUnavailableResponse = new HgtpCommonResponse(data);
                    hgtpResponseHandler.serverUnavailableResponseProcessing(hgtpServerUnavailableResponse);
                    break;
                case HgtpMessageType.DECLINE:
                    HgtpCommonResponse hgtpDeclineResponse = new HgtpCommonResponse(data);
                    hgtpResponseHandler.declineResponseProcessing(hgtpDeclineResponse);
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
