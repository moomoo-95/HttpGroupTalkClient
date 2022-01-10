package protocol.hgtp.request;

import protocol.hgtp.base.HgtpHeader;
import protocol.hgtp.base.HgtpMessage;
import protocol.hgtp.exception.HgtpException;
import util.module.ByteUtil;

import java.nio.charset.StandardCharsets;

public class HgtpRequestRegister extends HgtpMessage {

    private final HgtpHeader hgtpHeader;

    private final int userIdLength;         // 4 bytes
    private final String userId;            // userIdLength bytes
    private final long expires;         // 8 bytes
    private final short listenPort;     // 2 bytes
    private int nonceLength = 0;        // 4 bytes
    private String nonce = "";          // nonceLength bytes

    public HgtpRequestRegister(byte[] data) throws HgtpException {
        if (data.length >= HgtpHeader.HGTP_HEADER_SIZE + ByteUtil.NUM_BYTES_IN_LONG + ByteUtil.NUM_BYTES_IN_SHORT) {
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
            index += idByteData.length;

            byte[] expiresByteData = new byte[ByteUtil.NUM_BYTES_IN_LONG];
            System.arraycopy(data, index, expiresByteData, 0, expiresByteData.length);
            expires = ByteUtil.bytesToLong(expiresByteData, true);
            index += expiresByteData.length;

            byte[] listenPortByteData = new byte[ByteUtil.NUM_BYTES_IN_SHORT];
            System.arraycopy(data, index, listenPortByteData, 0, listenPortByteData.length);
            listenPort = ByteUtil.bytesToShort(listenPortByteData, true);
            index += listenPortByteData.length;

            byte[] nonceLengthByteData = new byte[ByteUtil.NUM_BYTES_IN_INT];
            System.arraycopy(data, index, nonceLengthByteData, 0, nonceLengthByteData.length);
            nonceLength = ByteUtil.bytesToInt(nonceLengthByteData, true);
            if (nonceLength > 0) {
                index += nonceLengthByteData.length;

                byte[] nonceByteData = new byte[nonceLength];
                System.arraycopy(data, index, nonceByteData, 0, nonceByteData.length);
                nonce = new String(nonceByteData);
            }
        } else {
            this.hgtpHeader = null;
            this.userIdLength = 0;
            this.userId = null;
            this.expires = 0;
            this.listenPort = 0;
        }
    }

    public HgtpRequestRegister(short magicCookie, short messageType, int seqNumber, long timeStamp, String userId, long expires, short listenPort) {
        // userIdLength + userId + expires + listenPort + nonceLength (nonce 미포함)
        int bodyLength = ByteUtil.NUM_BYTES_IN_INT + userId.length() + ByteUtil.NUM_BYTES_IN_LONG
                + ByteUtil.NUM_BYTES_IN_SHORT + ByteUtil.NUM_BYTES_IN_INT;

        this.hgtpHeader = new HgtpHeader(magicCookie, messageType, seqNumber, timeStamp, bodyLength);
        this.userIdLength = userId.getBytes(StandardCharsets.UTF_8).length;
        this.userId = userId;
        this.expires = expires;
        this.listenPort = listenPort;
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
        index += userIdByteData.length;

        byte[] expiresByteData = ByteUtil.longToBytes(expires, true);
        System.arraycopy(expiresByteData, 0, data, index, expiresByteData.length);
        index += expiresByteData.length;

        byte[] listenPortByteData = ByteUtil.shortToBytes(listenPort, true);
        System.arraycopy(listenPortByteData, 0, data, index, listenPortByteData.length);
        index += listenPortByteData.length;

        byte[] nonceLengthByteData = ByteUtil.intToBytes(nonceLength, true);
        System.arraycopy(nonceLengthByteData, 0, data, index, nonceLengthByteData.length);

        if (nonceLength > 0 && nonce.length() > 0) {
            byte[] nonceByteData = nonce.getBytes(StandardCharsets.UTF_8);
            byte[] newData = new byte[data.length];
            System.arraycopy(data, 0, newData, 0, data.length);
            index += nonceLengthByteData.length;
            System.arraycopy(nonceByteData, 0, newData, index, nonceByteData.length);
            data = newData;
        }

        return data;
    }

    public HgtpHeader getHgtpHeader() {return hgtpHeader;}

    public String getUserId() {return userId;}

    public long getExpires() {return expires;}

    public short getListenPort() {return listenPort;}

    public String getNonce() {return nonce;}

    public void setNonce(String nonce) {
        this.nonceLength = nonce.getBytes(StandardCharsets.UTF_8).length;
        this.nonce = nonce;

        hgtpHeader.setBodyLength(hgtpHeader.getBodyLength() + nonceLength);
    }
}
