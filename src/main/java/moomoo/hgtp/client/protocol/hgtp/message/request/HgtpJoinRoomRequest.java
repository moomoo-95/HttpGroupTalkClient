package moomoo.hgtp.client.protocol.hgtp.message.request;

import moomoo.hgtp.client.protocol.hgtp.exception.HgtpException;
import moomoo.hgtp.client.protocol.hgtp.message.base.HgtpHeader;
import moomoo.hgtp.client.protocol.hgtp.message.base.HgtpMessage;
import moomoo.hgtp.client.protocol.hgtp.message.base.content.HgtpRoomContent;
import moomoo.hgtp.client.service.AppInstance;


public class HgtpJoinRoomRequest extends HgtpMessage {

    private final HgtpHeader hgtpHeader;
    private final HgtpRoomContent hgtpContent;

    public HgtpJoinRoomRequest(byte[] data) throws HgtpException {
        if (data.length >= HgtpHeader.HGTP_HEADER_SIZE + AppInstance.ROOM_ID_SIZE) {
            int index = 0;

            byte[] headerByteData = new byte[HgtpHeader.HGTP_HEADER_SIZE];
            System.arraycopy(data, index, headerByteData, 0, headerByteData.length);
            this.hgtpHeader = new HgtpHeader(headerByteData);
            index += headerByteData.length;

            byte[] contextByteData = new byte[hgtpHeader.getBodyLength()];
            System.arraycopy(data, index, contextByteData, 0, contextByteData.length);
            this.hgtpContent = new HgtpRoomContent(contextByteData);
        } else {
            this.hgtpHeader = null;
            this.hgtpContent = null;
        }
    }

    public HgtpJoinRoomRequest(short magicCookie, short messageType, String userId, int seqNumber, long timeStamp, String roomId) {
        this.hgtpContent = new HgtpRoomContent(roomId);
        this.hgtpHeader = new HgtpHeader(magicCookie, messageType, messageType, userId, seqNumber, timeStamp, hgtpContent.getBodyLength());
    }

    @Override
    public byte[] getByteData() {
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

    public HgtpRoomContent getHgtpContent() {return hgtpContent;}
}
