package moomoo.hgtp.grouptalk.protocol.hgtp.message.base.content;

import moomoo.hgtp.grouptalk.service.AppInstance;
import moomoo.hgtp.grouptalk.util.NetworkUtil;
import org.slf4j.LoggerFactory;
import util.module.ByteUtil;

import java.nio.charset.StandardCharsets;

/**
 * @class HgtpRoomControlContent
 * @brief room에 대한 생성 제거 하기 위해 사용되는 hgtp 메시지의 content 타입
 */
public class HgtpRoomControlContent implements HgtpContent {

    private final String roomId;            // 12 bytes
    private final int roomNameLength;     // 4 bytes
    private final String roomName;            // roomNameLength bytes

    public HgtpRoomControlContent(byte[] data) {
        if (data.length >= getBodyLength()) {
            int index = 0;
            byte[] roomIdByteData = new byte[AppInstance.ROOM_ID_SIZE];
            System.arraycopy(data, index, roomIdByteData, 0, roomIdByteData.length);
            this.roomId = new String(roomIdByteData, StandardCharsets.UTF_8);
            index += roomIdByteData.length;

            byte[] roomNameLengthByteData = new byte[ByteUtil.NUM_BYTES_IN_INT];
            System.arraycopy(data, index, roomNameLengthByteData, 0, roomNameLengthByteData.length);
            this.roomNameLength = ByteUtil.bytesToInt(roomNameLengthByteData, true);
            index += roomNameLengthByteData.length;

            byte[] roomNameByteData = new byte[roomNameLength];
            System.arraycopy(data, index, roomNameByteData, 0, roomNameByteData.length);
            this.roomName = new String(roomNameByteData, StandardCharsets.UTF_8);
        } else {
            this.roomId = "";
            this.roomNameLength = 0;
            this.roomName = "";
        }
    }

    public HgtpRoomControlContent(String roomId, String roomName) {
        this.roomId = roomId;
        String encodeRoomName = NetworkUtil.messageEncoding(roomName);
        this.roomNameLength = encodeRoomName.getBytes(StandardCharsets.UTF_8).length;
        this.roomName = encodeRoomName;
    }

    @Override
    public byte[] getByteData() {
        byte[] data = new byte[getBodyLength()];
        int index = 0;

        byte[] roomIdByteData = roomId.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(roomIdByteData, 0, data, index, roomIdByteData.length);
        index += roomIdByteData.length;

        byte[] roomNameLengthByteData = ByteUtil.intToBytes(roomNameLength, true);
        System.arraycopy(roomNameLengthByteData, 0, data, index, roomNameLengthByteData.length);
        index += roomNameLengthByteData.length;

        byte[] roomNameByteData = roomName.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(roomNameByteData, 0, data, index, roomNameByteData.length);

        return data;
    }

    public int getBodyLength() {
        return AppInstance.ROOM_ID_SIZE + ByteUtil.NUM_BYTES_IN_INT + roomNameLength;
    }

    public String getRoomId() {
        return roomId;
    }

    public String getRoomName() {return NetworkUtil.messageDecoding(roomName);}
}
