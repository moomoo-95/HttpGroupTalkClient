package protocol.hgtp.message.response;

import protocol.hgtp.exception.HgtpException;
import protocol.hgtp.message.base.HgtpHeader;
import protocol.hgtp.message.base.HgtpMessage;
import protocol.hgtp.message.base.HgtpMessageType;
import util.module.ByteUtil;

import java.nio.charset.StandardCharsets;

public class HgtpResponseOk extends HgtpMessage {

    private final HgtpHeader hgtpHeader;

    private final short requestType;        // 1 bytes
    private final int userIdLength;         // 4 bytes
    private final String userId;            // userIdLength bytes

    public HgtpResponseOk(byte[] data) throws HgtpException {
        if (data.length >= HgtpHeader.HGTP_HEADER_SIZE + 1 + ByteUtil.NUM_BYTES_IN_INT) {
            int index = 0;

            byte[] headerByteData = new byte[HgtpHeader.HGTP_HEADER_SIZE];
            System.arraycopy(data, index, headerByteData, 0, headerByteData.length);
            this.hgtpHeader = new HgtpHeader(headerByteData);
            index += headerByteData.length;

            byte[] requestTypeByteData = new byte[1];
            System.arraycopy(data, index, requestTypeByteData, 0, requestTypeByteData.length);
            this.requestType = ByteUtil.bytesToShort(new byte[]{0x0, requestTypeByteData[0]}, true);
            index += requestTypeByteData.length;

            byte[] idLengthByteData = new byte[ByteUtil.NUM_BYTES_IN_INT];
            System.arraycopy(data, index, idLengthByteData, 0, idLengthByteData.length);
            this.userIdLength = ByteUtil.bytesToInt(idLengthByteData, true);
            index += idLengthByteData.length;

            byte[] idByteData = new byte[userIdLength];
            System.arraycopy(data, index, idByteData, 0, idByteData.length);
            this.userId = new String(idByteData);
        } else {
            this.hgtpHeader = null;
            this.requestType = HgtpMessageType.UNKNOWN;
            this.userIdLength = 0;
            this.userId = null;
        }
    }

    public HgtpResponseOk(short magicCookie, short messageType, int seqNumber, long timeStamp, Short requestType, String userId) {
        // requestType + userIdLength + userId
        int bodyLength =  1 + ByteUtil.NUM_BYTES_IN_INT + userId.length();

        this.hgtpHeader = new HgtpHeader(magicCookie, messageType, seqNumber, timeStamp, bodyLength);
        this.requestType = requestType;
        this.userIdLength = userId.getBytes(StandardCharsets.UTF_8).length;
        this.userId = userId;
    }

    @Override
    public byte[] getByteData(){
        byte[] data = new byte[HgtpHeader.HGTP_HEADER_SIZE + this.hgtpHeader.getBodyLength()];
        int index = 0;

        byte[] headerByteData = this.hgtpHeader.getByteData();
        System.arraycopy(headerByteData, 0, data, index, headerByteData.length);
        index += headerByteData.length;

        byte[] requestTypeByteData = ByteUtil.shortToBytes(requestType, true);
        System.arraycopy(requestTypeByteData, requestTypeByteData.length/2, data, index, requestTypeByteData.length/2);
        index += requestTypeByteData.length/2;

        byte[] userIdLengthByteData = ByteUtil.intToBytes(userIdLength, true);
        System.arraycopy(userIdLengthByteData, 0, data, index, userIdLengthByteData.length);
        index += userIdLengthByteData.length;

        byte[] userIdByteData = userId.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(userIdByteData, 0, data, index, userIdByteData.length);


        return data;
    }

    public HgtpHeader getHgtpHeader() {return hgtpHeader;}

    public short getRequestType() {return requestType;}

    public String getUserId() {return userId;}
}
