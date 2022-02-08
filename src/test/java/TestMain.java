import moomoo.hgtp.grouptalk.service.AppInstance;
import moomoo.hgtp.grouptalk.util.CnameGenerator;
import moomoo.hgtp.grouptalk.util.NetworkUtil;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import protocol.hgtp.HgtpTest;
import sun.nio.ch.Net;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Base64.Decoder;

public class TestMain {
    private static final Logger log = LoggerFactory.getLogger(TestMain.class);
    @Test
    public void testMain() {
//        AppInstance appInstance = AppInstance.getInstance();
//        appInstance.setConfigManager("src/main/resources/config/user_config.ini");
//        HgtpManager hgtpManager = HgtpManager.getInstance();
//        hgtpManager.startHgtp();
//
//        String userId = CnameGenerator.generateCnameUserId();
//        String roomId = CnameGenerator.generateCnameRoomId();
//        String userId2 = CnameGenerator.generateCnameUserId();
//        String roomId2 = CnameGenerator.generateCnameRoomId();
//        log.debug("{} : {} : {}", userId.length(), userId, userId.getBytes(StandardCharsets.UTF_8));
//        log.debug("{} : {} : {}", roomId.length(), roomId, roomId.getBytes(StandardCharsets.UTF_8));
//        HgtpTest hgtpTest = new HgtpTest();
//        log.debug("-------------------- Register --------------------");
//        hgtpTest.hgtpRegisterTest(userId);
//        hgtpTest.hgtpRegisterTest(userId2);
//        log.debug("-------------------- Create room --------------------");
//        hgtpTest.hgtpCreateRoomTest(userId, roomId);
//        log.debug("-------------------- Join room --------------------");
//        hgtpTest.hgtpJoinRoomTest(userId, roomId);
//        log.debug("-------------------- Invite user from room --------------------");
//        hgtpTest.hgtpInviteUserFromRoomTest(userId, roomId, userId2);
//        log.debug("-------------------- Remove user from room --------------------");
//        hgtpTest.hgtpRemoveUserFromRoomTest(userId, roomId, userId2);
//        log.debug("-------------------- Exit room --------------------");
//        hgtpTest.hgtpExitRoomTest(userId, roomId);
//        log.debug("-------------------- Delete room --------------------");
//        hgtpTest.hgtpDeleteRoomTest(userId, roomId);
//        log.debug("-------------------- Unregister --------------------");
//
//        hgtpTest.hgtpUnregisterTest(userId);
//
//        hgtpManager.stopHgtp();

        try {
            String ALGORITHM = "MD5";
            String MD5_REALM = "HGTP_SERVICE";
            String MD5_HASH_KEY = "950817";
            String hostName = "asdasdd";
            MessageDigest messageDigest = MessageDigest.getInstance(ALGORITHM);
            messageDigest.update(MD5_REALM.getBytes(StandardCharsets.UTF_8));
            messageDigest.update(MD5_HASH_KEY.getBytes(StandardCharsets.UTF_8));
            byte[] digestRealm = messageDigest.digest();
            messageDigest.reset();
            messageDigest.update(digestRealm);
            String nonce = new String(messageDigest.digest());

            MessageDigest messageDigest2 = MessageDigest.getInstance(ALGORITHM);
            messageDigest2.update(MD5_REALM.getBytes(StandardCharsets.UTF_8));
            messageDigest2.update(hostName.getBytes(StandardCharsets.UTF_8));
            messageDigest2.update(MD5_HASH_KEY.getBytes(StandardCharsets.UTF_8));
            byte[] digestRealm2 = messageDigest2.digest();
            messageDigest2.reset();
            messageDigest2.update(digestRealm2);
            String nonce2 = new String(messageDigest2.digest());

            MessageDigest messageDigest3 = MessageDigest.getInstance(ALGORITHM);
            messageDigest3.update(hostName.getBytes(StandardCharsets.UTF_8));
            messageDigest3.update(MD5_HASH_KEY.getBytes(StandardCharsets.UTF_8));
            byte[] digestRealm3 = messageDigest3.digest();
            messageDigest3.reset();
            messageDigest3.update(digestRealm3);
            String nonce3 = new String(messageDigest3.digest());

            log.debug("{} ", nonce);
            log.debug("{} ", nonce2);
            log.debug("{} ", nonce3);
        } catch (Exception e) {
            // ignore
        }

    }

}
