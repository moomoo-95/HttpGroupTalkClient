package moomoo.hgtp.grouptalk.protocol.hgtp.message.response;

import moomoo.hgtp.grouptalk.protocol.hgtp.exception.HgtpException;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.base.HgtpHeader;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.base.HgtpMessage;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.base.content.HgtpUnauthorizedContent;
import util.module.ByteUtil;


public class HgtpUnauthorizedResponse extends HgtpMessage {

    private final HgtpHeader hgtpHeader;
    private final HgtpUnauthorizedContent hgtpContent;

    public HgtpUnauthorizedResponse(byte[] data) throws HgtpException {
        if (data.length >= HgtpHeader.HGTP_HEADER_SIZE + ByteUtil.NUM_BYTES_IN_INT) {
            int index = 0;

            byte[] headerByteData = new byte[HgtpHeader.HGTP_HEADER_SIZE];
            System.arraycopy(data, index, headerByteData, 0, headerByteData.length);
            this.hgtpHeader = new HgtpHeader(headerByteData);
            index += headerByteData.length;

            byte[] contextByteData = new byte[this.hgtpHeader.getBodyLength()];
            System.arraycopy(data, index, contextByteData, 0, contextByteData.length);
            this.hgtpContent = new HgtpUnauthorizedContent(contextByteData);

        } else {
            this.hgtpHeader = null;
            this.hgtpContent = null;
        }
    }

    public HgtpUnauthorizedResponse(short magicCookie, short messageType, Short requestType, String userId, int seqNumber, long timeStamp, String realm) {
        this.hgtpContent = new HgtpUnauthorizedContent(realm);
        this.hgtpHeader = new HgtpHeader(magicCookie, messageType, requestType, userId, seqNumber, timeStamp, hgtpContent.getBodyLength());
    }

    @Override
    public byte[] getByteData(){
        byte[] data = new byte[HgtpHeader.HGTP_HEADER_SIZE + this.hgtpHeader.getBodyLength()];
        int index = 0;

        byte[] headerByteData = this.hgtpHeader.getByteData();
        System.arraycopy(headerByteData, 0, data, index, headerByteData.length);
        index += headerByteData.length;

        byte[] contextByteData = this.hgtpContent.getByteData();
        System.arraycopy(contextByteData, 0, data, index, contextByteData.length);

        return data;
    }

    public HgtpHeader getHgtpHeader() {return hgtpHeader;}

    public HgtpUnauthorizedContent getHgtpContent() {return hgtpContent;}
}
