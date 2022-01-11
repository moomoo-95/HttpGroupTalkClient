package protocol.hgtp.message.response;

import protocol.hgtp.message.base.HgtpHeader;
import protocol.hgtp.message.base.HgtpMessage;
import protocol.hgtp.exception.HgtpException;
import util.module.ByteUtil;

import java.nio.charset.StandardCharsets;

public class HgtpResponseUnauthorized extends HgtpMessage {

    private final HgtpHeader hgtpHeader;

    private final int userIdLength;         // 4 bytes
    private final String userId;            // userIdLength bytes
    private final int realmLength;          // 4 bytes
    private final String realm;             // realmLength bytes

    public HgtpResponseUnauthorized(byte[] data) throws HgtpException {
        if (data.length >= HgtpHeader.HGTP_HEADER_SIZE + ByteUtil.NUM_BYTES_IN_INT * 2) {
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

            byte[] realmLengthByteData = new byte[ByteUtil.NUM_BYTES_IN_INT];
            System.arraycopy(data, index, realmLengthByteData, 0, realmLengthByteData.length);
            realmLength = ByteUtil.bytesToInt(realmLengthByteData, true);
            index += realmLengthByteData.length;

            byte[] realmByteData = new byte[realmLength];
            System.arraycopy(data, index, realmByteData, 0, realmByteData.length);
            realm = new String(realmByteData, StandardCharsets.UTF_8);

        } else {
            this.hgtpHeader = null;
            this.userIdLength = 0;
            this.userId = null;
            this.realmLength = 0;
            this.realm = null;
        }
    }

    public HgtpResponseUnauthorized(short magicCookie, short messageType, int seqNumber, long timeStamp, String userId, String realm) {
        // userIdLength + userId + realmLength + realm
        int bodyLength = ByteUtil.NUM_BYTES_IN_INT + userId.length() + ByteUtil.NUM_BYTES_IN_INT +  realm.length();

        this.hgtpHeader = new HgtpHeader(magicCookie, messageType, seqNumber, timeStamp, bodyLength);
        this.userIdLength = userId.getBytes(StandardCharsets.UTF_8).length;
        this.userId = userId;
        this.realmLength = realm.getBytes(StandardCharsets.UTF_8).length;
        this.realm = realm;
    }

    @Override
    public byte[] getByteData(){
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

        byte[] realmLengthByteData = ByteUtil.intToBytes(realmLength, true);
        System.arraycopy(realmLengthByteData, 0, data, index, realmLengthByteData.length);
        index += realmLengthByteData.length;

        byte[] realmByteData = realm.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(realmByteData, 0, data, index, realmByteData.length);

        return data;
    }

    public HgtpHeader getHgtpHeader() {return hgtpHeader;}

    public String getUserId() {return userId;}

    public String getRealm() {return realm;}
}
