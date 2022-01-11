package protocol.hgtp.message.request;

import protocol.hgtp.exception.HgtpException;
import protocol.hgtp.message.base.HgtpHeader;
import protocol.hgtp.message.base.HgtpMessage;
import util.module.ByteUtil;

import java.nio.charset.StandardCharsets;

public class HgtpUnregisterRequest extends HgtpMessage {

    private final HgtpHeader hgtpHeader;

    private final int userIdLength;         // 4 bytes
    private final String userId;            // userIdLength bytes

    public HgtpUnregisterRequest(byte[] data) throws HgtpException {
        if (data.length >= HgtpHeader.HGTP_HEADER_SIZE + ByteUtil.NUM_BYTES_IN_INT) {
            int index = 0;

            byte[] headerByteData = new byte[HgtpHeader.HGTP_HEADER_SIZE];
            System.arraycopy(data, index, headerByteData, 0, headerByteData.length);
            this.hgtpHeader = new HgtpHeader(headerByteData);
            index += headerByteData.length;

            byte[] idLengthByteData = new byte[ByteUtil.NUM_BYTES_IN_INT];
            System.arraycopy(data, index, idLengthByteData, 0, idLengthByteData.length);
            userIdLength = ByteUtil.bytesToInt(idLengthByteData, true);
            index += idLengthByteData.length;

            byte[] idByteData = new byte[userIdLength];
            System.arraycopy(data, index, idByteData, 0, idByteData.length);
            userId = new String(idByteData);


        } else {
            this.hgtpHeader = null;
            this.userIdLength = 0;
            this.userId = null;
        }
    }

    public HgtpUnregisterRequest(short magicCookie, short messageType, int seqNumber, long timeStamp, String userId) {
        // userIdLength + userId
        int bodyLength = ByteUtil.NUM_BYTES_IN_INT + userId.length();

        this.hgtpHeader = new HgtpHeader(magicCookie, messageType, seqNumber, timeStamp, bodyLength);
        this.userIdLength = userId.getBytes(StandardCharsets.UTF_8).length;
        this.userId = userId;
    }

    @Override
    public byte[] getByteData() {
        byte[] data = new byte[HgtpHeader.HGTP_HEADER_SIZE + this.hgtpHeader.getBodyLength()];
        int index = 0;

        byte[] headerByteData = this.hgtpHeader.getByteData();
        System.arraycopy(headerByteData, 0, data, index, headerByteData.length);
        index += headerByteData.length;

        byte[] userIdLengthByteData = ByteUtil.intToBytes(userIdLength, true);
        System.arraycopy(userIdLengthByteData, 0, data, index, userIdLengthByteData.length);
        index += userIdLengthByteData.length;

        byte[] userIdByteData = userId.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(userIdByteData, 0, data, index, userIdByteData.length);

        return data;
    }

    public HgtpHeader getHgtpHeader() {return hgtpHeader;}

    public String getUserId() {return userId;}
}
