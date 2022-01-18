package moomoo.hgtp.client.protocol.hgtp.message.response.handler;

import moomoo.hgtp.client.config.ConfigManager;
import moomoo.hgtp.client.gui.GuiManager;
import moomoo.hgtp.client.gui.component.panel.ControlPanel;
import moomoo.hgtp.client.protocol.hgtp.message.base.HgtpHeader;
import moomoo.hgtp.client.protocol.hgtp.message.base.HgtpMessageType;
import moomoo.hgtp.client.protocol.hgtp.message.base.content.HgtpUnauthorizedContent;
import moomoo.hgtp.client.protocol.hgtp.message.request.HgtpRegisterRequest;
import moomoo.hgtp.client.protocol.hgtp.message.request.handler.HgtpRequestHandler;
import moomoo.hgtp.client.protocol.hgtp.message.response.HgtpCommonResponse;
import moomoo.hgtp.client.protocol.hgtp.message.response.HgtpUnauthorizedResponse;
import moomoo.hgtp.client.service.AppInstance;
import org.apache.commons.net.ntp.TimeStamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class HgtpResponseHandler {

    private static final Logger log = LoggerFactory.getLogger(HgtpResponseHandler.class);
    private static final String LOG_FORMAT = "({}) () () RECV HGTP MSG [{}]";

    private final HgtpRequestHandler hgtpRequestHandler = new HgtpRequestHandler();

    private AppInstance appInstance = AppInstance.getInstance();

    public HgtpResponseHandler() {
        // nothing
    }

    public void okResponseProcessing(HgtpCommonResponse hgtpOkResponse) {
        HgtpHeader hgtpHeader = hgtpOkResponse.getHgtpHeader();
        log.debug(LOG_FORMAT, hgtpHeader.getUserId(), hgtpOkResponse);
        ControlPanel controlPanel = GuiManager.getInstance().getControlPanel();

        switch (hgtpHeader.getRequestType()){
            case HgtpMessageType.REGISTER:
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
                appInstance.initRoomId();
                break;
            default:
        }
    }

    public void badRequestResponseProcessing(HgtpCommonResponse hgtpBadRequestResponse) {
        HgtpHeader hgtpHeader = hgtpBadRequestResponse.getHgtpHeader();
        log.debug(LOG_FORMAT, hgtpHeader.getUserId(), hgtpBadRequestResponse);

        switch (hgtpHeader.getRequestType()) {
            case HgtpMessageType.REGISTER:
                // todo register 등록 실패 상태
                break;
            case HgtpMessageType.UNREGISTER:
                // todo register 등록 해제 실패 상태
                break;
            case HgtpMessageType.CREATE_ROOM:
                appInstance.initRoomId();
                break;
            case HgtpMessageType.DELETE_ROOM:
                // todo delete room 실패 상태
                break;
            default:
        }
    }

    public boolean unauthorizedResponseProcessing(HgtpUnauthorizedResponse hgtpUnauthorizedResponse) {
        ConfigManager configManager = appInstance.getConfigManager();

        HgtpHeader hgtpHeader = hgtpUnauthorizedResponse.getHgtpHeader();
        HgtpUnauthorizedContent hgtpRegisterContent = hgtpUnauthorizedResponse.getHgtpContent();
        log.debug(LOG_FORMAT, hgtpHeader.getUserId(), hgtpUnauthorizedResponse);

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
                    configManager.getHgtpExpireTime(), configManager.getLocalListenIp(), configManager.getHgtpListenPort()
            );
            hgtpRequestHandler.sendRegisterRequest(hgtpRegisterRequest, nonce);
            return true;
        } catch (Exception e) {
            log.error("HgtpResponseHandler.unauthorizedResponseProcessing ", e);
            return false;
        }
    }

    public boolean forbiddenResponseProcessing(HgtpCommonResponse hgtpForbiddenResponse) {
        log.debug(LOG_FORMAT, hgtpForbiddenResponse.getHgtpHeader().getUserId(), hgtpForbiddenResponse);
        return true;
    }

    public boolean serverUnavailableResponseProcessing(HgtpCommonResponse hgtpServerUnavailableResponse) {
        HgtpHeader hgtpHeader = hgtpServerUnavailableResponse.getHgtpHeader();
        log.debug(LOG_FORMAT, hgtpHeader.getUserId(), hgtpServerUnavailableResponse);

        switch (hgtpHeader.getRequestType()) {
            case HgtpMessageType.REGISTER:
                // todo register 등록 실패 상태
                break;
            case HgtpMessageType.UNREGISTER:
                // todo register 등록 해제 실패 상태
                break;
            case HgtpMessageType.CREATE_ROOM:
                appInstance.initRoomId();
                break;
            case HgtpMessageType.DELETE_ROOM:
                // todo delete room 실패 상태
                break;
            default:
        }
        return true;
    }

    public boolean declineResponseProcessing(HgtpCommonResponse hgtpDeclineResponse) {
        log.debug(LOG_FORMAT, hgtpDeclineResponse.getHgtpHeader().getUserId(), hgtpDeclineResponse);
        return true;
    }
}
