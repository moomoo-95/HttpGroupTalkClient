import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import protocol.hgtp.HgtpTest;
import util.CnameGenerator;

import java.nio.charset.StandardCharsets;

public class TestMain {
    private static final Logger log = LoggerFactory.getLogger(TestMain.class);
    @Test
    public void testMain() {
        String userId = CnameGenerator.generateCnameUserId();
        String roomId = CnameGenerator.generateCnameRoomId();
        log.debug("{} : {} : {}", userId.length(), userId, userId.getBytes(StandardCharsets.UTF_8));
        log.debug("{} : {} : {}", roomId.length(), roomId, roomId.getBytes(StandardCharsets.UTF_8));
        HgtpTest hgtpTest = new HgtpTest();
        log.debug("-------------------- Register --------------------");
        hgtpTest.hgtpRegisterTest(userId);
        log.debug("-------------------- Create room --------------------");
        hgtpTest.hgtpCreateRoomTest(userId, roomId);
        log.debug("-------------------- Delete room --------------------");
        hgtpTest.hgtpDeleteRoomTest(userId, roomId);
        log.debug("-------------------- Unregister --------------------");

        hgtpTest.hgtpUnregisterTest(userId);


    }

}
