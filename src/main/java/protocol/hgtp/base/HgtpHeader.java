package protocol.hgtp.base;

import protocol.hgtp.exception.HgtpException;
import util.module.ByteUtil;

/**
 *    The HGTP header has the following format:
 *
 *     0                   1                   2                   3
 *     0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 *    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 *    | magic cookie  | message type  |        sequence number        |
 *    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 *    |                           timestamp                           |
 *    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 *    |                          body length                          |
 *    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 */
public class HgtpHeader {

    public static final int HGTP_HEADER_SIZE = 12;
    public static final short MAGIC_COOKIE = 0x48; // H

    private final short magicCookie;                // 1 bytes
    private final short messageType;                // 1 bytes
    private final int seqNumber;                    // 2 bytes
    private final long timeStamp;                   // 4 bytes
    private int bodyLength = 0;                     // 4 bytes

    public HgtpHeader(byte[] data) throws HgtpException {
        if (data.length == HGTP_HEADER_SIZE && data[0] == MAGIC_COOKIE) {
            int index = 0;

            byte[] mcToMtByteData = new byte[2];
            System.arraycopy(data, index, mcToMtByteData, 0, mcToMtByteData.length);

            this.magicCookie = ByteUtil.bytesToShort(new byte[]{0x0, mcToMtByteData[0]}, true);
            this.messageType = ByteUtil.bytesToShort(new byte[]{0x0, mcToMtByteData[1]}, true);
            index += mcToMtByteData.length;

            byte[] seqNumberByteData = new byte[2];
            System.arraycopy(data, index, seqNumberByteData, 0, seqNumberByteData.length);
            this.seqNumber = ByteUtil.bytesToShort(seqNumberByteData, true);
            index += seqNumberByteData.length;

            byte[] timeStampByteData = new byte[4];
            System.arraycopy(data, index, timeStampByteData, 0, timeStampByteData.length);
            this.timeStamp = ByteUtil.bytesToInt(timeStampByteData, true);
            index += timeStampByteData.length;

            byte[] bodyLengthByteData = new byte[4];
            System.arraycopy(data, index, bodyLengthByteData, 0, bodyLengthByteData.length);
            this.bodyLength = ByteUtil.bytesToInt(bodyLengthByteData, true);

        } else {
            magicCookie = 0x00;
            messageType = HgtpMessageType.UNKNOWN;
            seqNumber = 0;
            timeStamp = 0;
            bodyLength = 0;

            throw new HgtpException("[HGTP Header] Fail to create the header. Data length: (" + data.length + ") / MG: ("+data[0]+")");
        }
    }

    public HgtpHeader(short magicCookie, short messageType, int seqNumber, long timeStamp, int bodyLength) {
        this.magicCookie = magicCookie;
        this.messageType = messageType;
        this.seqNumber = seqNumber;
        this.timeStamp = timeStamp % 4294967296L;
        this.bodyLength = bodyLength;
    }

    public byte[] getByteData(){
        byte[] data = new byte[HGTP_HEADER_SIZE];
        int index = 0;

        byte[] magicCookieByteData = ByteUtil.shortToBytes(magicCookie, true);
        System.arraycopy(magicCookieByteData, 1, data, index, 1);
        index += 1;

        byte[] messageTypeByteData = ByteUtil.shortToBytes(messageType, true);
        System.arraycopy(messageTypeByteData, 1, data, index, 1);
        index += 1;

        byte[] seqNumberByteData = ByteUtil.intToBytes(seqNumber, true);
        System.arraycopy(seqNumberByteData, seqNumberByteData.length/2, data, index, seqNumberByteData.length/2);
        index += seqNumberByteData.length/2;

        byte[] timeStampByteData = ByteUtil.longToBytes(timeStamp, true);
        System.arraycopy(timeStampByteData, timeStampByteData.length/2, data, index, timeStampByteData.length/2);
        index += timeStampByteData.length/2;

        byte[] bodyLengthByteData = ByteUtil.intToBytes(bodyLength, true);
        System.arraycopy(bodyLengthByteData, 0, data, index, bodyLengthByteData.length);

        return data;
    }

    public short getMagicCookie() {return magicCookie;}

    public short getMessageType() {return messageType;}

    public int getSeqNumber() {return seqNumber;}

    public long getTimeStamp() {return timeStamp;}

    public int getBodyLength() {return bodyLength;}
    public void setBodyLength(int bodyLength) {this.bodyLength = bodyLength;}

    @Override
    public String toString() {
        return "HgtpHeader{" +
                "magicCookie=" + magicCookie +
                ", messageType=" + messageType +
                ", seqNumber=" + seqNumber +
                ", timeStamp=" + timeStamp +
                ", bodyLength=" + bodyLength +
                '}';
    }
}
