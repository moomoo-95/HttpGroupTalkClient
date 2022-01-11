package protocol.hgtp.message.response;

import protocol.hgtp.exception.HgtpException;
import protocol.hgtp.message.base.context.HgtpCommonContext;
import protocol.hgtp.message.base.HgtpHeader;
import protocol.hgtp.message.base.HgtpMessage;
import util.module.ByteUtil;

public class HgtpCommonResponse extends HgtpMessage {

    private final HgtpHeader hgtpHeader;                    // 12 bytes
    private final HgtpCommonContext hgtpCommonContext;      // At least 5 bytes

    public HgtpCommonResponse(byte[] data) throws HgtpException {
        if (data.length >= HgtpHeader.HGTP_HEADER_SIZE + 1 + ByteUtil.NUM_BYTES_IN_INT) {
            int index = 0;

            byte[] headerByteData = new byte[HgtpHeader.HGTP_HEADER_SIZE];
            System.arraycopy(data, index, headerByteData, 0, headerByteData.length);
            this.hgtpHeader = new HgtpHeader(headerByteData);
            index += headerByteData.length;

            byte[] contextByteData = new byte[this.hgtpHeader.getBodyLength()];
            System.arraycopy(data, index, contextByteData, 0, contextByteData.length);
            this.hgtpCommonContext = new HgtpCommonContext(contextByteData);
        } else {
            this.hgtpHeader = null;
            this.hgtpCommonContext = null;

        }
    }

    public HgtpCommonResponse(short magicCookie, short messageType, int seqNumber, long timeStamp, Short requestType, String userId) {
        // requestType + userIdLength + userId
        int bodyLength =  1 + ByteUtil.NUM_BYTES_IN_INT + userId.length();

        this.hgtpHeader = new HgtpHeader(magicCookie, messageType, seqNumber, timeStamp, bodyLength);
        this.hgtpCommonContext = new HgtpCommonContext(requestType, userId);

    }

    @Override
    public byte[] getByteData(){
        byte[] data = new byte[HgtpHeader.HGTP_HEADER_SIZE + this.hgtpHeader.getBodyLength()];
        int index = 0;

        byte[] headerByteData = this.hgtpHeader.getByteData();
        System.arraycopy(headerByteData, 0, data, index, headerByteData.length);
        index += headerByteData.length;

        byte[] contextByteData = this.hgtpCommonContext.getByteData();
        System.arraycopy(contextByteData, 0, data, index, contextByteData.length);

        return data;
    }

    public HgtpHeader getHgtpHeader() {return hgtpHeader;}

    public HgtpCommonContext getHgtpCommonContext() {return hgtpCommonContext;}
}
