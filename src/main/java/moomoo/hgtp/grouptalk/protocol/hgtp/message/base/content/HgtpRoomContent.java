package moomoo.hgtp.grouptalk.protocol.hgtp.message.base.content;

import moomoo.hgtp.grouptalk.util.NetworkUtil;
import util.module.ByteUtil;

import java.nio.charset.StandardCharsets;

/**
 * @class HgtpRoomContent
 * @brief room에 대한 입장/퇴장하기 위해 사용되는 hgtp 메시지의 content 타입
 */
public class HgtpRoomContent implements HgtpContent {

    private final int roomNameLength;     // 4 bytes
    private final String roomName;            // peerHostNameLength bytes

    public HgtpRoomContent(byte[] data) {
        if (data.length >= getBodyLength()) {
            int index = 0;
            byte[] roomNameLengthByteData = new byte[ByteUtil.NUM_BYTES_IN_INT];
            System.arraycopy(data, index, roomNameLengthByteData, 0, roomNameLengthByteData.length);
            this.roomNameLength = ByteUtil.bytesToInt(roomNameLengthByteData, true);
            index += roomNameLengthByteData.length;

            byte[] roomNameByteData = new byte[roomNameLength];
            System.arraycopy(data, index, roomNameByteData, 0, roomNameByteData.length);
            this.roomName = new String(roomNameByteData, StandardCharsets.UTF_8);

        } else {
            this.roomNameLength = 0;
            this.roomName = "";
        }
    }

    public HgtpRoomContent(String peerHostName) {
        String encodePeerHostName = NetworkUtil.messageEncoding(peerHostName);
        this.roomNameLength = encodePeerHostName.getBytes(StandardCharsets.UTF_8).length;
        this.roomName = encodePeerHostName;
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

        return data;
    }

    public int getBodyLength() {
        return ByteUtil.NUM_BYTES_IN_INT + roomNameLength;
    }

    public String getRoomName() {
        return NetworkUtil.messageDecoding(roomName);
    }
}
