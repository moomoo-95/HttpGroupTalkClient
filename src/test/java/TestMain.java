import moomoo.hgtp.grouptalk.service.AppInstance;
import moomoo.hgtp.grouptalk.util.CnameGenerator;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import protocol.hgtp.HgtpTest;

import java.nio.charset.StandardCharsets;

public class TestMain {
    private static final Logger log = LoggerFactory.getLogger(TestMain.class);
    @Test
    public void testMain() {
        AppInstance appInstance = AppInstance.getInstance();
        appInstance.setConfigManager("src/main/resources/config/user_config.ini");
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
    }

}
