package moomoo.hgtp.grouptalk.protocol.hgtp.message.request;

import moomoo.hgtp.grouptalk.protocol.hgtp.exception.HgtpException;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.base.HgtpHeader;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.base.HgtpMessage;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.base.HgtpMessageType;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.base.content.HgtpRoomContent;
import moomoo.hgtp.grouptalk.service.AppInstance;


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

    public HgtpJoinRoomRequest(String userId, int seqNumber, String roomId) {
        this.hgtpContent = new HgtpRoomContent(roomId);
        this.hgtpHeader = new HgtpHeader(AppInstance.MAGIC_COOKIE, HgtpMessageType.JOIN_ROOM, HgtpMessageType.JOIN_ROOM, userId, seqNumber, AppInstance.getInstance().getTimeStamp(), hgtpContent.getBodyLength());
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
