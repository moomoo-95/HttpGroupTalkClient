package protocol.hgtp.message.response;

import protocol.hgtp.message.base.context.HgtpUnauthorizedContext;
import protocol.hgtp.message.base.HgtpHeader;
import protocol.hgtp.message.base.HgtpMessage;
import protocol.hgtp.exception.HgtpException;
import util.module.ByteUtil;


public class HgtpUnauthorizedResponse extends HgtpMessage {

    private final HgtpHeader hgtpHeader;                        // 12 bytes
    private final HgtpUnauthorizedContext hgtpUnauthorizedContext;  // At least 9 bytes

    public HgtpUnauthorizedResponse(byte[] data) throws HgtpException {
        if (data.length >= HgtpHeader.HGTP_HEADER_SIZE + 1 + ByteUtil.NUM_BYTES_IN_INT * 2) {
            int index = 0;

            byte[] headerByteData = new byte[HgtpHeader.HGTP_HEADER_SIZE];
            System.arraycopy(data, index, headerByteData, 0, headerByteData.length);
            this.hgtpHeader = new HgtpHeader(headerByteData);
            index += headerByteData.length;

            byte[] contextByteData = new byte[this.hgtpHeader.getBodyLength()];
            System.arraycopy(data, index, contextByteData, 0, contextByteData.length);
            this.hgtpUnauthorizedContext = new HgtpUnauthorizedContext(contextByteData);

        } else {
            this.hgtpHeader = null;
            this.hgtpUnauthorizedContext = null;
        }
    }

    public HgtpUnauthorizedResponse(short magicCookie, short messageType, int seqNumber, long timeStamp, Short requestType, String userId, String realm) {
        // requestType + userIdLength + userId + realmLength + realm
        int bodyLength = 1 + ByteUtil.NUM_BYTES_IN_INT + userId.length() + ByteUtil.NUM_BYTES_IN_INT +  realm.length();

        this.hgtpHeader = new HgtpHeader(magicCookie, messageType, seqNumber, timeStamp, bodyLength);
        this.hgtpUnauthorizedContext = new HgtpUnauthorizedContext(requestType, userId, realm);
    }

    @Override
    public byte[] getByteData(){
        byte[] data = new byte[HgtpHeader.HGTP_HEADER_SIZE + this.hgtpHeader.getBodyLength()];
        int index = 0;

        byte[] headerByteData = this.hgtpHeader.getByteData();
        System.arraycopy(headerByteData, 0, data, index, headerByteData.length);
        index += headerByteData.length;

        byte[] contextByteData = this.hgtpUnauthorizedContext.getByteData();
        System.arraycopy(contextByteData, 0, data, index, contextByteData.length);

        return data;
    }

    public HgtpHeader getHgtpHeader() {return hgtpHeader;}

    public HgtpUnauthorizedContext getHgtpUnauthorizedContext() {
        return hgtpUnauthorizedContext;
    }
}
