package com.hgtp.client.protocol.hgtp.message.request;

import com.hgtp.client.protocol.hgtp.exception.HgtpException;
import com.hgtp.client.protocol.hgtp.message.base.HgtpHeader;
import com.hgtp.client.protocol.hgtp.message.base.HgtpMessage;
import com.hgtp.client.protocol.hgtp.message.base.content.HgtpRegisterContent;
import util.module.ByteUtil;


public class HgtpRegisterRequest extends HgtpMessage {

    private final HgtpHeader hgtpHeader;
    private final HgtpRegisterContent hgtpContext;

    public HgtpRegisterRequest(byte[] data) throws HgtpException {
        if (data.length >= HgtpHeader.HGTP_HEADER_SIZE + ByteUtil.NUM_BYTES_IN_LONG + ByteUtil.NUM_BYTES_IN_SHORT + ByteUtil.NUM_BYTES_IN_INT) {
            int index = 0;

            byte[] headerByteData = new byte[HgtpHeader.HGTP_HEADER_SIZE];
            System.arraycopy(data, index, headerByteData, 0, headerByteData.length);
            this.hgtpHeader = new HgtpHeader(headerByteData);
            index += headerByteData.length;

            byte[] contextByteData = new byte[hgtpHeader.getBodyLength()];
            System.arraycopy(data, index, contextByteData, 0, contextByteData.length);
            this.hgtpContext = new HgtpRegisterContent(contextByteData);
        } else {
            this.hgtpHeader = null;
            this.hgtpContext = null;
        }
    }

    public HgtpRegisterRequest(short magicCookie, short messageType, String userId, int seqNumber, long timeStamp,  long expires, short listenPort) {
        //  + expires + listenPort + nonceLength (nonce 미포함)
        int bodyLength = ByteUtil.NUM_BYTES_IN_LONG
                + ByteUtil.NUM_BYTES_IN_SHORT + ByteUtil.NUM_BYTES_IN_INT;

        this.hgtpHeader = new HgtpHeader(magicCookie, messageType, messageType, userId, seqNumber, timeStamp, bodyLength);
        this.hgtpContext = new HgtpRegisterContent(expires, listenPort);
    }

    @Override
    public byte[] getByteData() {
        byte[] data = new byte[HgtpHeader.HGTP_HEADER_SIZE + this.hgtpHeader.getBodyLength()];
        int index = 0;

        byte[] headerByteData = this.hgtpHeader.getByteData();
        System.arraycopy(headerByteData, 0, data, index, headerByteData.length);
        index += headerByteData.length;

        byte[] contextByteData = this.hgtpContext.getByteData();
        System.arraycopy(contextByteData, 0, data, index, contextByteData.length);

        return data;
    }

    public HgtpHeader getHgtpHeader() {return hgtpHeader;}

    public HgtpRegisterContent getHgtpContext() {return hgtpContext;}
}
