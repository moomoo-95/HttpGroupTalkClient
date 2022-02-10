package moomoo.hgtp.grouptalk.protocol.hgtp.message.response;

import moomoo.hgtp.grouptalk.protocol.hgtp.exception.HgtpException;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.base.HgtpHeader;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.base.HgtpMessage;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.base.HgtpMessageType;
import moomoo.hgtp.grouptalk.service.AppInstance;

public class HgtpForbiddenResponse extends HgtpMessage {

    private final HgtpHeader hgtpHeader;

    public HgtpForbiddenResponse(byte[] data) throws HgtpException {
        if (data.length >= HgtpHeader.HGTP_HEADER_SIZE) {
            int index = 0;

            byte[] headerByteData = new byte[HgtpHeader.HGTP_HEADER_SIZE];
            System.arraycopy(data, index, headerByteData, 0, headerByteData.length);
            this.hgtpHeader = new HgtpHeader(headerByteData);
        } else {
            this.hgtpHeader = null;
        }
    }

    public HgtpForbiddenResponse(Short requestType, String userId, int seqNumber) {
        this.hgtpHeader = new HgtpHeader(AppInstance.MAGIC_COOKIE, HgtpMessageType.FORBIDDEN, requestType, userId, seqNumber, AppInstance.getInstance().getTimeStamp(), 0);
    }

    @Override
    public byte[] getByteData(){
        byte[] data = new byte[HgtpHeader.HGTP_HEADER_SIZE + this.hgtpHeader.getBodyLength()];
        int index = 0;

        byte[] headerByteData = this.hgtpHeader.getByteData();
        System.arraycopy(headerByteData, 0, data, index, headerByteData.length);

        return data;
    }

    public HgtpHeader getHgtpHeader() {return hgtpHeader;}
}
