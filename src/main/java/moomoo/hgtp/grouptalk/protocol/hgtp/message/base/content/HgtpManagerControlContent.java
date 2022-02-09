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

    private final int roomNameLength;     // 4 bytes
    private final String roomName;            // roomNameLength bytes
    private final int peerHostNameLength;     // 4 bytes
    private final String peerHostName;            // peerHostNameLength bytes

    public HgtpManagerControlContent(byte[] data) {
        if (data.length >= getBodyLength()) {
            int index = 0;
            byte[] roomNameLengthByteData = new byte[ByteUtil.NUM_BYTES_IN_INT];
            System.arraycopy(data, index, roomNameLengthByteData, 0, roomNameLengthByteData.length);
            this.roomNameLength = ByteUtil.bytesToInt(roomNameLengthByteData, true);
            index += roomNameLengthByteData.length;

            byte[] roomNameByteData = new byte[roomNameLength];
            System.arraycopy(data, index, roomNameByteData, 0, roomNameByteData.length);
            this.roomName = new String(roomNameByteData, StandardCharsets.UTF_8);
            index += roomNameByteData.length;

            byte[] peerHostNameLengthByteData = new byte[ByteUtil.NUM_BYTES_IN_INT];
            System.arraycopy(data, index, peerHostNameLengthByteData, 0, peerHostNameLengthByteData.length);
            this.peerHostNameLength = ByteUtil.bytesToInt(peerHostNameLengthByteData, true);
            index += peerHostNameLengthByteData.length;

            byte[] peerHostNameByteData = new byte[peerHostNameLength];
            System.arraycopy(data, index, peerHostNameByteData, 0, peerHostNameByteData.length);
            this.peerHostName = new String(peerHostNameByteData, StandardCharsets.UTF_8);

        } else {
            this.roomNameLength = 0;
            this.roomName = "";
            this.peerHostNameLength = 0;
            this.peerHostName = "";
        }
    }

    public HgtpManagerControlContent(String roomName, String peerHostName) {
        String encodeRoomName = NetworkUtil.messageEncoding(roomName);
        this.roomNameLength = encodeRoomName.getBytes(StandardCharsets.UTF_8).length;
        this.roomName = encodeRoomName;

        String encodePeerHostName = NetworkUtil.messageEncoding(peerHostName);
        this.peerHostNameLength = encodePeerHostName.getBytes(StandardCharsets.UTF_8).length;
        this.peerHostName = encodePeerHostName;
    }

    @Override
    public byte[] getByteData() {
        byte[] data = new byte[getBodyLength()];
        int index = 0;

        byte[] roomNameLengthByteData = ByteUtil.intToBytes(roomNameLength, true);
        System.arraycopy(roomNameLengthByteData, 0, data, index, roomNameLengthByteData.length);
        index += roomNameLengthByteData.length;

        byte[] roomNameByteData = roomName.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(roomNameByteData, 0, data, index, roomNameByteData.length);
        index += roomNameByteData.length;

        byte[] peerHostNameLengthByteData = ByteUtil.intToBytes(peerHostNameLength, true);
        System.arraycopy(peerHostNameLengthByteData, 0, data, index, peerHostNameLengthByteData.length);
        index += peerHostNameLengthByteData.length;

        byte[] peerHostNameByteData = peerHostName.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(peerHostNameByteData, 0, data, index, peerHostNameByteData.length);

        return data;
    }

    public int getBodyLength() {
        return ByteUtil.NUM_BYTES_IN_INT + roomNameLength + ByteUtil.NUM_BYTES_IN_INT + peerHostNameLength;
    }

    public String getRoomName() {
        return NetworkUtil.messageDecoding(roomName);
    }

    public String getPeerHostName() {
        return NetworkUtil.messageDecoding(peerHostName);
    }
}
