package protocol.hgtp.message.base;

import protocol.hgtp.exception.HgtpException;
import util.module.ByteUtil;

import java.nio.charset.StandardCharsets;

/**
 *    The HGTP header has the following format:
 *
 *     0                   1                   2                   3
 *     0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 *    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 *    |          magic cookie         | message type  | request type  |
 *    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 *    |                              UID                              |
 *    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 *    |                              UID                              |
 *    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 *    |                        sequence number                        |
 *    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 *    |                           timestamp                           |
 *    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 *    |                          body length                          |
 *    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 */
public class HgtpHeader {

    public static final int HGTP_HEADER_SIZE = 24;
    public static final short MAGIC_COOKIE = 0x4853; // HS

    private final short magicCookie;                // 2 bytes
    private final short messageType;                // 1 bytes
    private final short requestType;                // 1 bytes
    private final String userId;                    // 8 bytes
    private final int seqNumber;                    // 4 bytes
    private final long timeStamp;                   // 4 bytes
    private int bodyLength = 0;                     // 4 bytes

    public HgtpHeader(byte[] data) throws HgtpException {
        if (data.length == HGTP_HEADER_SIZE) {
            int index = 0;

            byte[] magicCookieByteData = new byte[ByteUtil.NUM_BYTES_IN_SHORT];
            System.arraycopy(data, index, magicCookieByteData, 0, magicCookieByteData.length);
            this.magicCookie = ByteUtil.bytesToShort(magicCookieByteData, true);
            index += magicCookieByteData.length;

            if (magicCookie != MAGIC_COOKIE) {
                messageType = HgtpMessageType.UNKNOWN;
                requestType = HgtpMessageType.UNKNOWN;
                userId = "";
                seqNumber = 0;
                timeStamp = 0;
                bodyLength = 0;
                throw new HgtpException("[HGTP Header] Fail to create the header. MG: ("+magicCookie+")");
            }

            byte[] mtToRtByteData = new byte[ByteUtil.NUM_BYTES_IN_SHORT];
            System.arraycopy(data, index, mtToRtByteData, 0, mtToRtByteData.length);

            this.messageType = ByteUtil.bytesToShort(new byte[]{0x0, mtToRtByteData[0]}, true);
            this.requestType = ByteUtil.bytesToShort(new byte[]{0x0, mtToRtByteData[1]}, true);
            index += mtToRtByteData.length;

            byte[] userIdByteData = new byte[ByteUtil.NUM_BYTES_IN_LONG];
            System.arraycopy(data, index, userIdByteData, 0, userIdByteData.length);
            this.userId = new String(userIdByteData, StandardCharsets.UTF_8);
            index += userIdByteData.length;


            byte[] seqNumberByteData = new byte[ByteUtil.NUM_BYTES_IN_INT];
            System.arraycopy(data, index, seqNumberByteData, 0, seqNumberByteData.length);
            this.seqNumber = ByteUtil.bytesToInt(seqNumberByteData, true);
            index += seqNumberByteData.length;

            byte[] timeStampByteData = new byte[ByteUtil.NUM_BYTES_IN_LONG];
            System.arraycopy(data, index, timeStampByteData, timeStampByteData.length/2, timeStampByteData.length/2);
            this.timeStamp = ByteUtil.bytesToLong(timeStampByteData, true);
            index += timeStampByteData.length/2;

            byte[] bodyLengthByteData = new byte[ByteUtil.NUM_BYTES_IN_INT];
            System.arraycopy(data, index, bodyLengthByteData, 0, bodyLengthByteData.length);
            this.bodyLength = ByteUtil.bytesToInt(bodyLengthByteData, true);

        } else {
            magicCookie = 0x0000;
            messageType = HgtpMessageType.UNKNOWN;
            requestType = HgtpMessageType.UNKNOWN;
            userId = "";
            seqNumber = 0;
            timeStamp = 0;
            bodyLength = 0;
            throw new HgtpException("[HGTP Header] Fail to create the header. Data length: (" + data.length + ")");
        }
    }

    public HgtpHeader(short magicCookie, short messageType, short requestType, String userId, int seqNumber, long timeStamp, int bodyLength) {
        this.magicCookie = magicCookie;
        this.messageType = messageType;
        this.requestType = requestType;
        this.userId = userId;
        this.seqNumber = seqNumber;
        this.timeStamp = timeStamp;
        this.bodyLength = bodyLength;
    }

    public byte[] getByteData(){
        byte[] data = new byte[HGTP_HEADER_SIZE];
        int index = 0;

        byte[] magicCookieByteData = ByteUtil.shortToBytes(magicCookie, true);
        System.arraycopy(magicCookieByteData, 0, data, index, magicCookieByteData.length);
        index += magicCookieByteData.length;

        byte[] messageTypeByteData = ByteUtil.shortToBytes(messageType, true);
        System.arraycopy(messageTypeByteData, 1, data, index, 1);
        index += 1;

        byte[] requestTypeByteData = ByteUtil.shortToBytes(requestType, true);
        System.arraycopy(requestTypeByteData, 1, data, index, 1);
        index += 1;

        byte[] userIdByteData = userId.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(userIdByteData, 0, data, index, userIdByteData.length);
        index += userIdByteData.length;

        byte[] seqNumberByteData = ByteUtil.intToBytes(seqNumber, true);
        System.arraycopy(seqNumberByteData, 0, data, index, seqNumberByteData.length);
        index += seqNumberByteData.length;

        byte[] timeStampByteData = ByteUtil.longToBytes(timeStamp, true);
        System.arraycopy(timeStampByteData, timeStampByteData.length/2, data, index, timeStampByteData.length/2);
        index += timeStampByteData.length/2;

        byte[] bodyLengthByteData = ByteUtil.intToBytes(bodyLength, true);
        System.arraycopy(bodyLengthByteData, 0, data, index, bodyLengthByteData.length);

        return data;
    }

    public short getMagicCookie() {return magicCookie;}

    public short getMessageType() {return messageType;}

    public short getRequestType() {return requestType;}

    public String getUserId() {return userId;}

    public int getSeqNumber() {return seqNumber;}

    public long getTimeStamp() {return timeStamp;}

    public int getBodyLength() {return bodyLength;}
    public void setBodyLength(int bodyLength) {this.bodyLength = bodyLength;}
}
