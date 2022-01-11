package protocol.hgtp.message.base.context;

import protocol.hgtp.exception.HgtpException;

public class HgtpCommonContext extends HgtpContext {

    public HgtpCommonContext(byte[] data) throws HgtpException {
        super(data);
    }

    public HgtpCommonContext(Short requestType, String userId) {
        super(requestType, userId);
    }

    @Override
    public byte[] getByteData() {
        return super.getByteData();
    }

    @Override
    public short getRequestType() {
        return super.getRequestType();
    }

    @Override
    public int getUserIdLength() {
        return super.getUserIdLength();
    }

    @Override
    public String getUserId() {
        return super.getUserId();
    }

    @Override
    public int getBodyLength() {
        return super.getBodyLength();
    }
}
