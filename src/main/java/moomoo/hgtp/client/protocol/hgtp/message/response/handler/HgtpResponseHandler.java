package moomoo.hgtp.client.protocol.hgtp.message.response.handler;

import moomoo.hgtp.client.config.ConfigManager;
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

    private final HgtpRequestHandler hgtpRequestHandler = new HgtpRequestHandler();

    public HgtpResponseHandler() {
        // nothing
    }

    public boolean okResponseProcessing(HgtpCommonResponse hgtpOkResponse) {
        HgtpHeader hgtpHeader = hgtpOkResponse.getHgtpHeader();
        log.debug("({}) () () RECV HGTP MSG [{}]", hgtpHeader.getUserId(), hgtpOkResponse);

        if (hgtpHeader.getRequestType() == HgtpMessageType.REGISTER) {
            // nothing
        }
        return true;
    }

    public boolean badRequestResponseProcessing(HgtpCommonResponse hgtpBadRequestResponse) {
        log.debug("({}) () () RECV HGTP MSG [{}]", hgtpBadRequestResponse.getHgtpHeader().getUserId(), hgtpBadRequestResponse);
        return true;
    }

    public boolean unauthorizedResponseProcessing(HgtpUnauthorizedResponse hgtpUnauthorizedResponse) {
        AppInstance appInstance = AppInstance.getInstance();
        ConfigManager configManager = appInstance.getConfigManager();

        HgtpHeader hgtpHeader = hgtpUnauthorizedResponse.getHgtpHeader();
        HgtpUnauthorizedContent hgtpRegisterContent = hgtpUnauthorizedResponse.getHgtpContent();
        log.debug("({}) () () RECV HGTP MSG [{}]", hgtpHeader.getUserId(), hgtpUnauthorizedResponse);

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
        log.debug("({}) () () RECV HGTP MSG [{}]", hgtpForbiddenResponse.getHgtpHeader().getUserId(), hgtpForbiddenResponse);
        return true;
    }

    public boolean serverUnavailableResponseProcessing(HgtpCommonResponse hgtpServerUnavailableResponse) {
        log.debug("({}) () () RECV HGTP MSG [{}]", hgtpServerUnavailableResponse.getHgtpHeader().getUserId(), hgtpServerUnavailableResponse);
        return true;
    }

    public boolean declineResponseProcessing(HgtpCommonResponse hgtpDeclineResponse) {
        log.debug("({}) () () RECV HGTP MSG [{}]", hgtpDeclineResponse.getHgtpHeader().getUserId(), hgtpDeclineResponse);
        return true;
    }
}
