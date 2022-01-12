package protocol.hgtp.message.base.content;

import protocol.hgtp.message.base.HgtpHeader;
import util.module.ByteUtil;

import java.nio.charset.StandardCharsets;

public class HgtpRegisterContent extends HgtpContent {

    private final long expires;             // 8 bytes
    private final short listenPort;         // 2 bytes
    private int nonceLength = 0;            // 4 bytes
    private String nonce = "";              // nonceLength bytes

    public HgtpRegisterContent(byte[] data) {
        super(data);
        int index = super.getBodyLength();

        if (data.length >= index + ByteUtil.NUM_BYTES_IN_LONG + ByteUtil.NUM_BYTES_IN_SHORT + ByteUtil.NUM_BYTES_IN_INT) {

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
            this.expires = 0;
            this.listenPort = 0;
        }
    }

    public HgtpRegisterContent(long expires, short listenPort) {
        this.expires = expires;
        this.listenPort = listenPort;
    }

    @Override
    public byte[] getByteData() {
        byte[] data = new byte[getTotalBodyLength()];
        int index = 0;

        byte[] commonContextData = super.getByteData();
        System.arraycopy(commonContextData, 0, data, index, commonContextData.length);
        index += commonContextData.length;

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

    public int getTotalBodyLength() {
        return super.getBodyLength() + ByteUtil.NUM_BYTES_IN_LONG + ByteUtil.NUM_BYTES_IN_SHORT + ByteUtil.NUM_BYTES_IN_INT + nonceLength;
    }

    public String getNonce() {return nonce;}

    public void setNonce(HgtpHeader hgtpHeader, String nonce) {
        this.nonceLength = nonce.getBytes(StandardCharsets.UTF_8).length;
        this.nonce = nonce;

        hgtpHeader.setBodyLength(hgtpHeader.getBodyLength() + nonceLength);
    }
}
