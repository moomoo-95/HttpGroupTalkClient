package protocol.hgtp.message.request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import protocol.hgtp.message.base.HgtpHeader;
import protocol.hgtp.exception.HgtpException;
import protocol.hgtp.message.base.HgtpMessage;
import protocol.hgtp.message.base.context.HgtpRegisterContext;
import util.module.ByteUtil;

import java.nio.charset.StandardCharsets;

public class HgtpRegisterRequest extends HgtpMessage {

    private final HgtpHeader hgtpHeader;
    private final HgtpRegisterContext hgtpRegisterContext;

//    private final int userIdLength;         // 4 bytes
//    private final String userId;            // userIdLength bytes
//    private final long expires;             // 8 bytes
//    private final short listenPort;         // 2 bytes
//    private int nonceLength = 0;            // 4 bytes
//    private String nonce = "";              // nonceLength bytes

    public HgtpRegisterRequest(byte[] data) throws HgtpException {
        if (data.length >= HgtpHeader.HGTP_HEADER_SIZE + ByteUtil.NUM_BYTES_IN_INT + ByteUtil.NUM_BYTES_IN_LONG + ByteUtil.NUM_BYTES_IN_SHORT + ByteUtil.NUM_BYTES_IN_INT) {
            int index = 0;

            byte[] headerByteData = new byte[HgtpHeader.HGTP_HEADER_SIZE];
            System.arraycopy(data, index, headerByteData, 0, headerByteData.length);
            this.hgtpHeader = new HgtpHeader(headerByteData);
            index += headerByteData.length;

            byte[] contextByteData = new byte[hgtpHeader.getBodyLength()];
            System.arraycopy(data, index, contextByteData, 0, contextByteData.length);
            this.hgtpRegisterContext = new HgtpRegisterContext(contextByteData);
        } else {
            this.hgtpHeader = null;
            this.hgtpRegisterContext = null;
        }
    }

    public HgtpRegisterRequest(short magicCookie, short messageType, int seqNumber, long timeStamp, String userId, long expires, short listenPort) {
        // userIdLength + userId + expires + listenPort + nonceLength (nonce 미포함)
        int bodyLength = 1 + ByteUtil.NUM_BYTES_IN_INT + userId.length() + ByteUtil.NUM_BYTES_IN_LONG
                + ByteUtil.NUM_BYTES_IN_SHORT + ByteUtil.NUM_BYTES_IN_INT;

        this.hgtpHeader = new HgtpHeader(magicCookie, messageType, seqNumber, timeStamp, bodyLength);
        this.hgtpRegisterContext = new HgtpRegisterContext(messageType, userId, expires, listenPort);
    }

    @Override
    public byte[] getByteData() {
        byte[] data = new byte[HgtpHeader.HGTP_HEADER_SIZE + this.hgtpHeader.getBodyLength()];
        int index = 0;

        byte[] headerByteData = this.hgtpHeader.getByteData();
        System.arraycopy(headerByteData, 0, data, index, headerByteData.length);
        index += headerByteData.length;

        byte[] contextByteData = this.hgtpRegisterContext.getByteData();

        Logger log = LoggerFactory.getLogger(HgtpRegisterRequest.class);
        log.debug("{} / {} / {}", data.length, headerByteData.length, contextByteData.length);
        System.arraycopy(contextByteData, 0, data, index, contextByteData.length);

        return data;
    }

    public HgtpHeader getHgtpHeader() {return hgtpHeader;}

    public HgtpRegisterContext getHgtpRegisterContext() {
        return hgtpRegisterContext;
    }
}
