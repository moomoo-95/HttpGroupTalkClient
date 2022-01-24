package moomoo.hgtp.grouptalk.protocol.hgtp.message.request;

import moomoo.hgtp.grouptalk.protocol.hgtp.exception.HgtpException;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.base.HgtpHeader;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.base.HgtpMessage;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.base.content.HgtpRoomManagerContent;
import moomoo.hgtp.grouptalk.service.AppInstance;


public class HgtpInviteUserFromRoomRequest extends HgtpMessage {

    private final HgtpHeader hgtpHeader;
    private final HgtpRoomManagerContent hgtpContent;

    public HgtpInviteUserFromRoomRequest(byte[] data) throws HgtpException {
        if (data.length >= HgtpHeader.HGTP_HEADER_SIZE + AppInstance.ROOM_ID_SIZE + AppInstance.USER_ID_SIZE) {
            int index = 0;

            byte[] headerByteData = new byte[HgtpHeader.HGTP_HEADER_SIZE];
            System.arraycopy(data, index, headerByteData, 0, headerByteData.length);
            this.hgtpHeader = new HgtpHeader(headerByteData);
            index += headerByteData.length;

            byte[] contextByteData = new byte[hgtpHeader.getBodyLength()];
            System.arraycopy(data, index, contextByteData, 0, contextByteData.length);
            this.hgtpContent = new HgtpRoomManagerContent(contextByteData);
        } else {
            this.hgtpHeader = null;
            this.hgtpContent = null;
        }
    }

    public HgtpInviteUserFromRoomRequest(short magicCookie, short messageType, String userId, int seqNumber, long timeStamp, String roomId, String peerUserId) {
        this.hgtpContent = new HgtpRoomManagerContent(roomId, peerUserId);
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

    public HgtpRoomManagerContent getHgtpContent() {return hgtpContent;}
}
