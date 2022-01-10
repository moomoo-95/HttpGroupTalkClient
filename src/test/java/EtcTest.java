import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import protocol.hgtp.base.ByteUtil;
import protocol.hgtp.base.HgtpHeader;
import protocol.hgtp.base.HgtpMessageType;
import protocol.hgtp.exception.HgtpException;

import java.text.SimpleDateFormat;

public class EtcTest {

    private static final Logger log = LoggerFactory.getLogger(EtcTest.class);
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-DD HH:mm:ss.SSS");

    @Test
    public void etcTest() {


        byte[] testHeader = setTestHeader();

        log.debug("INPUT DATA : {}", testHeader);
        try {
            HgtpHeader hgtpHeader = new HgtpHeader(testHeader);
            log.debug("PARSE DATA : {}", hgtpHeader);
            log.debug("OUT   DATA : {}", hgtpHeader.getByteData());
        } catch (HgtpException e) {
            log.error("parse error : {}", e);
        }



    }

    public byte[] setTestHeader(){
        byte[] testHeader = new byte[12];
        // magic cookie
        testHeader[0] = 0x48;
        // method
        testHeader[1] = ByteUtil.shortToBytes(HgtpMessageType.UNREGISTER, true)[1];
        // seq number
        testHeader[2] = 0x40;
        testHeader[3] = 0x0F;
        // timestamp
        long time = System.currentTimeMillis();
        byte[] timeStamp = ByteUtil.longToBytes(time, true);
        for (int index = 4; index < timeStamp.length; index++) {
            testHeader[index] = timeStamp[index];
        }
        testHeader[8] = 0x00;
        testHeader[9] = 0x00;
        testHeader[10] = 0x00;
        testHeader[11] = 0x03;

        // body length;
        return testHeader;
    }
}
