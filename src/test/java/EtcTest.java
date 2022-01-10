import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import protocol.hgtp.exception.HgtpException;
import protocol.hgtp.request.HgtpRequestRegister;
import protocol.hgtp.base.HgtpHeader;
import protocol.hgtp.base.HgtpMessageType;
import protocol.hgtp.response.HgtpResponseUnauthorized;

import java.text.SimpleDateFormat;

public class EtcTest {

    private static final Logger log = LoggerFactory.getLogger(EtcTest.class);
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-DD HH:mm:ss.SSS");

    @Test
    public void hgtpTest() {
        try {
            // create, send Register
            HgtpRequestRegister sendHgtpRequestRegister = new HgtpRequestRegister(HgtpHeader.MAGIC_COOKIE, HgtpMessageType.REGISTER, 4, System.currentTimeMillis(),
                    "regSessionId", 3600L, (short) 5060);
            log.debug("SEND DATA : {}", sendHgtpRequestRegister);
            // recv Register
            byte[] recvRegister = sendHgtpRequestRegister.getByteData();
            HgtpRequestRegister recvHgtpRequestRegister = new HgtpRequestRegister(recvRegister);
            log.debug("RECV DATA  : {}", recvHgtpRequestRegister);

            // create, send unauthorized
            HgtpHeader hgtpHeader = recvHgtpRequestRegister.getHgtpHeader();
            HgtpResponseUnauthorized sendHgtpResponseUnauthorized = new HgtpResponseUnauthorized(
                    hgtpHeader.getMagicCookie(), HgtpMessageType.UNAUTHORIZED_401,
                    hgtpHeader.getSeqNumber() + 1, System.currentTimeMillis(), recvHgtpRequestRegister.getUserId(),
                    "RR");
            log.debug("SEND DATA : {}", sendHgtpResponseUnauthorized);
            // recv unauthorized
            byte[] recvUnauthorized = sendHgtpResponseUnauthorized.getByteData();
            HgtpResponseUnauthorized recvHgtpResponseUnauthorized = new HgtpResponseUnauthorized(recvUnauthorized);
            log.debug("RECV DATA : {}", recvHgtpResponseUnauthorized);

        } catch (HgtpException e) {
            log.error("hgtpTest ", e);
        }
    }
}
