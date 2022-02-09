package moomoo.hgtp.grouptalk.protocol.hgtp.message.base.content;

import moomoo.hgtp.grouptalk.service.AppInstance;
import moomoo.hgtp.grouptalk.util.NetworkUtil;
import util.module.ByteUtil;

import java.nio.charset.StandardCharsets;

/**
 * @class HgtpManagerControlContent
 * @brief room에 user를 초대/강제퇴장 하기 위해 사용되는 hgtp 메시지의 content 타입
 */
public class HgtpManagerControlContent implements HgtpContent {

    private final String roomId;            // 12 bytes
    private final int peerHostNameLength;     // 4 bytes
    private final String peerHostName;            // peerHostNameLength bytes

    public HgtpManagerControlContent(byte[] data) {
        if (data.length >= getBodyLength()) {
            int index = 0;
            byte[] roomIdByteData = new byte[AppInstance.ROOM_ID_SIZE];
            System.arraycopy(data, index, roomIdByteData, 0, roomIdByteData.length);
            this.roomId = new String(roomIdByteData, StandardCharsets.UTF_8);
            index += roomIdByteData.length;

            byte[] peerHostNameLengthByteData = new byte[ByteUtil.NUM_BYTES_IN_INT];
            System.arraycopy(data, index, peerHostNameLengthByteData, 0, peerHostNameLengthByteData.length);
            this.peerHostNameLength = ByteUtil.bytesToInt(peerHostNameLengthByteData, true);
            index += peerHostNameLengthByteData.length;

            byte[] peerHostNameByteData = new byte[peerHostNameLength];
            System.arraycopy(data, index, peerHostNameByteData, 0, peerHostNameByteData.length);
            this.peerHostName = new String(peerHostNameByteData, StandardCharsets.UTF_8);

        } else {
            this.roomId = "";
            this.peerHostNameLength = 0;
            this.peerHostName = "";
        }
    }

    public HgtpManagerControlContent(String roomId, String peerHostName) {
        this.roomId = roomId;
        String encodePeerHostName = NetworkUtil.messageEncoding(peerHostName);
        this.peerHostNameLength = encodePeerHostName.getBytes(StandardCharsets.UTF_8).length;
        this.peerHostName = encodePeerHostName;
    }

    @Override
    public byte[] getByteData() {
        byte[] data = new byte[getBodyLength()];
        int index = 0;

        byte[] roomIdByteData = roomId.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(roomIdByteData, 0, data, index, roomIdByteData.length);
        index += roomIdByteData.length;

        byte[] peerHostNameLengthByteData = ByteUtil.intToBytes(peerHostNameLength, true);
        System.arraycopy(peerHostNameLengthByteData, 0, data, index, peerHostNameLengthByteData.length);
        index += peerHostNameLengthByteData.length;

        byte[] peerHostNameByteData = peerHostName.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(peerHostNameByteData, 0, data, index, peerHostNameByteData.length);

        return data;
    }

    public int getBodyLength() {
        return AppInstance.ROOM_ID_SIZE + ByteUtil.NUM_BYTES_IN_INT + peerHostNameLength;
    }

    public String getRoomId() {
        return roomId;
    }

    public String getPeerHostName() {
        return NetworkUtil.messageDecoding(peerHostName);
    }
}
