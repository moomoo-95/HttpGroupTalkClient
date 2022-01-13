package moomoo.hgtp.client.protocol.hgtp.message.response.handler;

import moomoo.hgtp.client.protocol.hgtp.message.response.HgtpCommonResponse;
import moomoo.hgtp.client.protocol.hgtp.message.response.HgtpUnauthorizedResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HgtpResponseHandler {

    private static final Logger log = LoggerFactory.getLogger(HgtpResponseHandler.class);

    public HgtpResponseHandler() {
        // nothing
    }

    public static boolean okResponseProcessing(HgtpCommonResponse hgtpOkResponse) {
        log.debug("({}) () () RECV HGTP MSG [{}]", hgtpOkResponse.getHgtpHeader().getUserId(), hgtpOkResponse);
        return true;
    }

    public static boolean badRequestResponseProcessing(HgtpCommonResponse hgtpBadRequestResponse) {
        log.debug("({}) () () RECV HGTP MSG [{}]", hgtpBadRequestResponse.getHgtpHeader().getUserId(), hgtpBadRequestResponse);
        return true;
    }

    public static boolean unauthorizedResponseProcessing(HgtpUnauthorizedResponse hgtpUnauthorizedResponse) {
        log.debug("({}) () () RECV HGTP MSG [{}]", hgtpUnauthorizedResponse.getHgtpHeader().getUserId(), hgtpUnauthorizedResponse);
        return true;
    }

    public static boolean forbiddenResponseProcessing(HgtpCommonResponse hgtpForbiddenResponse) {
        log.debug("({}) () () RECV HGTP MSG [{}]", hgtpForbiddenResponse.getHgtpHeader().getUserId(), hgtpForbiddenResponse);
        return true;
    }

    public static boolean serverUnavailableResponseProcessing(HgtpCommonResponse hgtpServerUnavailableResponse) {
        log.debug("({}) () () RECV HGTP MSG [{}]", hgtpServerUnavailableResponse.getHgtpHeader().getUserId(), hgtpServerUnavailableResponse);
        return true;
    }

    public static boolean declineResponseProcessing(HgtpCommonResponse hgtpDeclineResponse) {
        log.debug("({}) () () RECV HGTP MSG [{}]", hgtpDeclineResponse.getHgtpHeader().getUserId(), hgtpDeclineResponse);
        return true;
    }
}
