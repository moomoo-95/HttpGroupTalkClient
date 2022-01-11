package protocol.hgtp.message.base.context;

import protocol.hgtp.exception.HgtpException;
import protocol.hgtp.message.base.HgtpMessageType;
import util.module.ByteUtil;

import java.nio.charset.StandardCharsets;

public abstract class HgtpContext {

    private final short requestType;        // 1 bytes
    private final int userIdLength;         // 4 bytes
    private final String userId;            // userIdLength bytes


    public HgtpContext(byte[] data) {
        if (data.length >= + 1 + ByteUtil.NUM_BYTES_IN_INT) {
            int index = 0;

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
            this.requestType = HgtpMessageType.UNKNOWN;
            this.userIdLength = 0;
            this.userId = null;
        }
    }

    public HgtpContext(Short requestType, String userId) {
        this.requestType = requestType;
        this.userIdLength = userId.getBytes(StandardCharsets.UTF_8).length;
        this.userId = userId;
    }

    public byte[] getByteData(){
        byte[] data = new byte[this.getBodyLength()];
        int index = 0;

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

    public short getRequestType() {return requestType;}

    public int getUserIdLength() {return userIdLength;}

    public String getUserId() {return userId;}

    public int getBodyLength() {
        // requestType + userIdLength + userId
        return 1 + ByteUtil.NUM_BYTES_IN_INT + userId.length();
    }
}
