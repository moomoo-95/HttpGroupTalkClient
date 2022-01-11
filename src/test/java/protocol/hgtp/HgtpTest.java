package protocol.hgtp;

import org.apache.commons.net.ntp.TimeStamp;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import protocol.hgtp.exception.HgtpException;
import protocol.hgtp.message.request.HgtpRequestRegister;
import protocol.hgtp.message.base.HgtpHeader;
import protocol.hgtp.message.base.HgtpMessageType;
import protocol.hgtp.message.response.HgtpResponseOk;
import protocol.hgtp.message.response.HgtpResponseUnauthorized;
import util.module.ByteUtil;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;

public class HgtpTest {

    private static final Logger log = LoggerFactory.getLogger(HgtpTest.class);
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-DD HH:mm:ss.SSS");

    private static final String TEST_REALM = "HGTP_SERVICE";
    private static final String TEST_HASH_KEY = "950817";

    @Test
    public void hgtpRegisterSuccessTest() {
        try {

            // send first Register
            HgtpRequestRegister sendFirstHgtpRequestRegister = new HgtpRequestRegister(
                    HgtpHeader.MAGIC_COOKIE, HgtpMessageType.REGISTER, 4, TimeStamp.getCurrentTime().getSeconds(),
                    "regSessionId", 3600L, (short) 5060);
            log.debug("SEND DATA : {}", sendFirstHgtpRequestRegister);
            // recv first Register
            byte[] recvFirstRegister = sendFirstHgtpRequestRegister.getByteData();
            HgtpRequestRegister recvFirstHgtpRequestRegister = new HgtpRequestRegister(recvFirstRegister);
            log.debug("RECV DATA  : {}", recvFirstHgtpRequestRegister);

            // send unauthorized
            HgtpResponseUnauthorized sendHgtpResponseUnauthorized = new HgtpResponseUnauthorized(
                    recvFirstHgtpRequestRegister.getHgtpHeader().getMagicCookie(), HgtpMessageType.UNAUTHORIZED,
                    recvFirstHgtpRequestRegister.getHgtpHeader().getSeqNumber() + 1, TimeStamp.getCurrentTime().getSeconds(),
                    recvFirstHgtpRequestRegister.getUserId(), TEST_REALM);
            log.debug("SEND DATA : {}", sendHgtpResponseUnauthorized);
            // recv unauthorized
            byte[] recvUnauthorized = sendHgtpResponseUnauthorized.getByteData();
            HgtpResponseUnauthorized recvHgtpResponseUnauthorized = new HgtpResponseUnauthorized(recvUnauthorized);
            log.debug("RECV DATA : {}", recvHgtpResponseUnauthorized);

            // Encoding realm -> nonce
            MessageDigest messageDigestRealm = MessageDigest.getInstance("MD5");
            messageDigestRealm.update(recvHgtpResponseUnauthorized.getRealm().getBytes(StandardCharsets.UTF_8));
            messageDigestRealm.update(TEST_HASH_KEY.getBytes(StandardCharsets.UTF_8));
            byte[] digestRealm = messageDigestRealm.digest();
            messageDigestRealm.reset();
            messageDigestRealm.update(digestRealm);
            String nonce = new String(messageDigestRealm.digest());

            // send second Register
            HgtpRequestRegister sendSecondHgtpRequestRegister = new HgtpRequestRegister(
                    HgtpHeader.MAGIC_COOKIE, HgtpMessageType.REGISTER, recvHgtpResponseUnauthorized.getHgtpHeader().getSeqNumber() + 1,
                    TimeStamp.getCurrentTime().getSeconds(),
                    recvHgtpResponseUnauthorized.getUserId(), 3600L, (short) 5060);
            sendSecondHgtpRequestRegister.setNonce(nonce);
            log.debug("SEND DATA : {}", sendSecondHgtpRequestRegister);
            // recv second Register
            byte[] recvSecondRegister = sendSecondHgtpRequestRegister.getByteData();
            HgtpRequestRegister recvSecondHgtpRequestRegister = new HgtpRequestRegister(recvSecondRegister);
            log.debug("RECV DATA  : {}", recvSecondHgtpRequestRegister);

            // Decoding nonce -> realm
            MessageDigest messageDigestNonce = MessageDigest.getInstance("MD5");
            messageDigestNonce.update(TEST_REALM.getBytes(StandardCharsets.UTF_8));
            messageDigestNonce.update(TEST_HASH_KEY.getBytes(StandardCharsets.UTF_8));
            byte[] digestNonce = messageDigestNonce.digest();
            messageDigestNonce.reset();
            messageDigestNonce.update(digestNonce);

            String curNonce = new String(messageDigestNonce.digest());
            if (curNonce.equals(recvSecondHgtpRequestRegister.getNonce())) {
                // send 200 Ok
                HgtpResponseOk sendHgtpResponseOk = new HgtpResponseOk(recvSecondHgtpRequestRegister.getHgtpHeader().getMagicCookie(), HgtpMessageType.OK,
                        recvSecondHgtpRequestRegister.getHgtpHeader().getSeqNumber() + 1, TimeStamp.getCurrentTime().getSeconds(),
                        recvSecondHgtpRequestRegister.getHgtpHeader().getMessageType(), recvFirstHgtpRequestRegister.getUserId());
                log.debug("SEND DATA : {}", sendHgtpResponseOk);
                // recv 200 Ok
                byte[] recvResponseOk = sendHgtpResponseOk.getByteData();
                HgtpResponseOk recvHgtpResponseOk = new HgtpResponseOk(recvResponseOk);
                log.debug("RECV DATA  : {}", recvHgtpResponseOk);
            } else {
                log.debug("nonce not matching");

            }


        } catch (HgtpException | NoSuchAlgorithmException e) {
            log.error("HgtpTest.hgtpRegisterSuccessTest ", e);
        }
    }
}
