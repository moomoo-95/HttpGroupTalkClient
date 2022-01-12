package com.hgtp.client.protocol.hgtp.message.response.handler;

import com.hgtp.client.protocol.hgtp.message.response.HgtpCommonResponse;
import com.hgtp.client.protocol.hgtp.message.response.HgtpUnauthorizedResponse;

public class HgtpResponseHandler {

    public HgtpResponseHandler() {
        // nothing
    }

    public static boolean okResponseProcessing(HgtpCommonResponse hgtpOkResponse) {
        return true;
    }

    public static boolean badRequestResponseProcessing(HgtpCommonResponse hgtpBadRequestResponse) {
        return true;
    }

    public static boolean unauthorizedResponseProcessing(HgtpUnauthorizedResponse hgtpUnauthorizedResponse) {
        return true;
    }

    public static boolean forbiddenResponseProcessing(HgtpCommonResponse hgtpForbiddenResponse) {
        return true;
    }

    public static boolean serverUnavailableResponseProcessing(HgtpCommonResponse hgtpServerUnavailableResponse) {
        return true;
    }

    public static boolean declineResponseProcessing(HgtpCommonResponse hgtpDeclineResponse) {
        return true;
    }
}
