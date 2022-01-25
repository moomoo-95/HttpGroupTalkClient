package moomoo.hgtp.grouptalk.protocol.hgtp.message.response.handler;

import moomoo.hgtp.grouptalk.config.ConfigManager;
import moomoo.hgtp.grouptalk.gui.GuiManager;
import moomoo.hgtp.grouptalk.gui.component.panel.ControlPanel;
import moomoo.hgtp.grouptalk.network.NetworkManager;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.base.HgtpHeader;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.base.HgtpMessageType;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.base.content.HgtpUnauthorizedContent;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.request.HgtpRegisterRequest;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.request.handler.HgtpRequestHandler;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.response.HgtpCommonResponse;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.response.HgtpUnauthorizedResponse;
import moomoo.hgtp.grouptalk.service.AppInstance;
import moomoo.hgtp.grouptalk.session.SessionManager;
import moomoo.hgtp.grouptalk.session.base.UserInfo;
import network.definition.DestinationRecord;
import org.apache.commons.net.ntp.TimeStamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class HgtpResponseHandler {

    private static final Logger log = LoggerFactory.getLogger(HgtpResponseHandler.class);
    private static final String RECV_LOG = "({}) () () RECV HGTP MSG [{}]";

    private static AppInstance appInstance = AppInstance.getInstance();
    private static SessionManager sessionManager = SessionManager.getInstance();
    private static NetworkManager networkManager = NetworkManager.getInstance();

    public HgtpResponseHandler() {
        // nothing
    }

    public void okResponseProcessing(HgtpCommonResponse hgtpOkResponse) {
        HgtpHeader hgtpHeader = hgtpOkResponse.getHgtpHeader();
        log.debug(RECV_LOG, hgtpHeader.getUserId(), hgtpOkResponse);

        if (hgtpHeader == null) {
            log.debug("() () () header is null [{}]", hgtpOkResponse);
            return;
        }

        if (appInstance.getMode() == AppInstance.CLIENT_MODE) {
            ControlPanel controlPanel = GuiManager.getInstance().getControlPanel();

            switch (hgtpHeader.getRequestType()) {
                case HgtpMessageType.REGISTER:
                    // todo register 등록시 저장되도록 설정
//                    httpTargetAddress = new NetAddress(appInstance.getConfigManager().getTargetListenIp(), configManager.getHttpListenPort(), true, SocketProtocol.TCP);
//                    networkManager.getHttpGroupSocket().getDestination()
//                    hgtpGroupSocket.addDestination(hgtpTargetAddress, null, AppInstance.SERVER_SESSION_ID, hgtpChannelInitializer);
                    controlPanel.setRegisterButtonStatus();
                    break;
                case HgtpMessageType.UNREGISTER:
                    controlPanel.setInitButtonStatus();
                    break;
                case HgtpMessageType.CREATE_ROOM:
                    controlPanel.setCreateRoomButtonStatus();
                    break;
                case HgtpMessageType.DELETE_ROOM:
                    controlPanel.setDeleteRoomButtonStatus();
                    sessionManager.getUserInfo(appInstance.getUserId()).initRoomId();
                    break;
                default:
            }
        }
    }

    public void badRequestResponseProcessing(HgtpCommonResponse hgtpBadRequestResponse) {
        HgtpHeader hgtpHeader = hgtpBadRequestResponse.getHgtpHeader();
        log.debug(RECV_LOG, hgtpHeader.getUserId(), hgtpBadRequestResponse);

        if (hgtpHeader == null) {
            log.debug("() () () header is null [{}]", hgtpBadRequestResponse);
            return;
        }

        if (appInstance.getMode() == AppInstance.CLIENT_MODE) {
            switch (hgtpHeader.getRequestType()) {
                case HgtpMessageType.REGISTER:
                    // todo register 등록 실패 상태
                    break;
                case HgtpMessageType.UNREGISTER:
                    // todo register 등록 해제 실패 상태
                    break;
                case HgtpMessageType.CREATE_ROOM:
                    sessionManager.getUserInfo(appInstance.getUserId()).initRoomId();
                    break;
                case HgtpMessageType.DELETE_ROOM:
                    // todo delete room 실패 상태
                    break;
                default:
            }
        }

    }

    public void unauthorizedResponseProcessing(HgtpUnauthorizedResponse hgtpUnauthorizedResponse) {
        ConfigManager configManager = appInstance.getConfigManager();

        HgtpHeader hgtpHeader = hgtpUnauthorizedResponse.getHgtpHeader();
        HgtpUnauthorizedContent hgtpRegisterContent = hgtpUnauthorizedResponse.getHgtpContent();

        if (hgtpHeader == null || hgtpRegisterContent == null) {
            log.debug("() () () header or content is null [{}]", hgtpUnauthorizedResponse);
            return;
        }
        log.debug(RECV_LOG, hgtpHeader.getUserId(), hgtpUnauthorizedResponse);

        // server 일 경우 bad request 전송
        if (appInstance.getMode() == AppInstance.SERVER_MODE) {
            HgtpCommonResponse hgtpCommonResponse = new HgtpCommonResponse(
                    AppInstance.MAGIC_COOKIE, HgtpMessageType.BAD_REQUEST, hgtpHeader.getRequestType(),
                    hgtpHeader.getUserId(), hgtpHeader.getSeqNumber() + AppInstance.SEQ_INCREMENT, appInstance.getTimeStamp());

            sendCommonResponse(hgtpCommonResponse);
            return;
        }

        // http socket 설정 및 target address 설정
        UserInfo userInfo = sessionManager.getUserInfo(appInstance.getUserId());
        userInfo.setHttpTargetNetAddress(configManager.getTargetListenIp(), hgtpRegisterContent.getListenPort());

        try {
            // Encoding realm -> nonce
            MessageDigest messageDigestRealm = MessageDigest.getInstance(AppInstance.ALGORITHM);
            messageDigestRealm.update(hgtpRegisterContent.getRealm().getBytes(StandardCharsets.UTF_8));
            messageDigestRealm.update(AppInstance.MD5_HASH_KEY.getBytes(StandardCharsets.UTF_8));
            byte[] digestRealm = messageDigestRealm.digest();
            messageDigestRealm.reset();
            messageDigestRealm.update(digestRealm);
            String nonce = new String(messageDigestRealm.digest());

            // Send Register
            HgtpRegisterRequest hgtpRegisterRequest = new HgtpRegisterRequest(
                    AppInstance.MAGIC_COOKIE, HgtpMessageType.REGISTER, appInstance.getUserId(),
                    hgtpHeader.getSeqNumber() + AppInstance.SEQ_INCREMENT, TimeStamp.getCurrentTime().getSeconds(),
                    configManager.getHgtpExpireTime(), configManager.getLocalListenIp(), (short) userInfo.getHttpServerNetAddress().getPort()
            );
            HgtpRequestHandler hgtpRequestHandler = new HgtpRequestHandler();
            hgtpRequestHandler.sendRegisterRequest(hgtpRegisterRequest, nonce);
        } catch (Exception e) {
            log.error("HgtpResponseHandler.unauthorizedResponseProcessing ", e);
        }
    }

    public boolean forbiddenResponseProcessing(HgtpCommonResponse hgtpForbiddenResponse) {
        log.debug(RECV_LOG, hgtpForbiddenResponse.getHgtpHeader().getUserId(), hgtpForbiddenResponse);
        return true;
    }

    public void serverUnavailableResponseProcessing(HgtpCommonResponse hgtpServerUnavailableResponse) {
        HgtpHeader hgtpHeader = hgtpServerUnavailableResponse.getHgtpHeader();
        log.debug(RECV_LOG, hgtpHeader.getUserId(), hgtpServerUnavailableResponse);

        if (hgtpHeader == null) {
            log.debug("() () () header is null [{}]", hgtpServerUnavailableResponse);
            return;
        }

        if (appInstance.getMode() == AppInstance.CLIENT_MODE) {
            switch (hgtpHeader.getRequestType()) {
                case HgtpMessageType.REGISTER:
                    // todo register 등록 실패 상태
                    break;
                case HgtpMessageType.UNREGISTER:
                    // todo register 등록 해제 실패 상태
                    break;
                case HgtpMessageType.CREATE_ROOM:
                    sessionManager.getUserInfo(appInstance.getUserId()).initRoomId();
                    break;
                case HgtpMessageType.DELETE_ROOM:
                    // todo delete room 실패 상태
                    break;
                default:
            }
        }
    }

    public boolean declineResponseProcessing(HgtpCommonResponse hgtpDeclineResponse) {
        log.debug(RECV_LOG, hgtpDeclineResponse.getHgtpHeader().getUserId(), hgtpDeclineResponse);
        return true;
    }

    /**
     * @fn sendCommonResponse
     * @brief UnauthorizedResponse 를 제외한 나머지 응답을 전송하는 메서드
     * @param hgtpCommonResponse
     */
    public void sendCommonResponse(HgtpCommonResponse hgtpCommonResponse) {
        UserInfo userInfo = sessionManager.getUserInfo(hgtpCommonResponse.getHgtpHeader().getUserId());

        if (userInfo == null) {
            log.warn("({}) () () UserInfo is null.", userInfo.getUserId());
        }

        DestinationRecord destinationRecord = networkManager.getHgtpGroupSocket().getDestination(userInfo.getSessionId());
        if (destinationRecord == null) {
            log.warn("({}) () () DestinationRecord Channel is null.", userInfo.getUserId());
        }

        byte[] data = hgtpCommonResponse.getByteData();
        destinationRecord.getNettyChannel().sendData(data, data.length);
        log.debug("({}) () () [{}] SEND DATA {}", userInfo.getUserId(), HgtpMessageType.RESPONSE_HASHMAP.get(hgtpCommonResponse.getHgtpHeader().getMessageType()), hgtpCommonResponse);

    }

    /**
     * @fn sendCommonResponse
     * @brief UnauthorizedResponse 를 전송하는 메서드
     * @param hgtpUnauthorizedResponse
     */
    public void sendUnauthorizedResponse(HgtpUnauthorizedResponse hgtpUnauthorizedResponse) {
        UserInfo userInfo = sessionManager.getUserInfo(hgtpUnauthorizedResponse.getHgtpHeader().getUserId());

        if (userInfo == null) {
            log.warn("({}) () () UserInfo is null.", userInfo.getUserId());
        }

        DestinationRecord destinationRecord = networkManager.getHgtpGroupSocket().getDestination(userInfo.getSessionId());
        if (destinationRecord == null) {
            log.warn("({}) () () DestinationRecord Channel is null.", userInfo.getUserId());
        }

        byte[] data = hgtpUnauthorizedResponse.getByteData();
        destinationRecord.getNettyChannel().sendData(data, data.length);
        log.debug("({}) () () [{}] SEND DATA {}", userInfo.getUserId(), HgtpMessageType.RESPONSE_HASHMAP.get(hgtpUnauthorizedResponse.getHgtpHeader().getMessageType()), hgtpUnauthorizedResponse);
    }
}
