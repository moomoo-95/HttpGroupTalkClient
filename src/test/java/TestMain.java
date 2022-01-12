import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import protocol.hgtp.HgtpTest;

public class TestMain {
    private static final Logger log = LoggerFactory.getLogger(TestMain.class);
    @Test
    public void testMain() {
        HgtpTest hgtpTest = new HgtpTest();
        hgtpTest.hgtpRegisterTest();
        hgtpTest.hgtpUnregisterTest();

    }

}
