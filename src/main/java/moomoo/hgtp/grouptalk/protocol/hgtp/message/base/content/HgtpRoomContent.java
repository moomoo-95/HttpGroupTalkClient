package moomoo.hgtp.grouptalk.protocol.hgtp.message.base.content;

import moomoo.hgtp.grouptalk.service.AppInstance;

import java.nio.charset.StandardCharsets;

public class HgtpRoomContent implements HgtpContent {

    private final String roomId;            // 12 bytes

    public HgtpRoomContent(byte[] data) {
        if (data.length >= getBodyLength()) {
            int index = 0;
            byte[] roomIdByteData = new byte[getBodyLength()];
            System.arraycopy(data, index, roomIdByteData, 0, roomIdByteData.length);
            this.roomId = new String(roomIdByteData, StandardCharsets.UTF_8);

        } else {
            this.roomId = "";
        }
    }

    public HgtpRoomContent(String roomId) {
        this.roomId = roomId;
    }

    @Override
    public byte[] getByteData() {
        byte[] data = new byte[getBodyLength()];
        int index = 0;

        byte[] roomIdByteData = roomId.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(roomIdByteData, 0, data, index, roomIdByteData.length);

        return data;
    }

    public int getBodyLength() {
        return AppInstance.ROOM_ID_SIZE;
    }

    public String getRoomId() {
        return roomId;
    }
}
