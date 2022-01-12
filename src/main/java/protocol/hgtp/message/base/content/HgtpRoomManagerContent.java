package protocol.hgtp.message.base.content;

import java.nio.charset.StandardCharsets;

public class HgtpRoomManagerContent implements HgtpContent {

    private final String roomId;            // 12 bytes
    private final String peerUserId;            // 8 bytes

    public HgtpRoomManagerContent(byte[] data) {
        if (data.length >= getBodyLength()) {
            int index = 0;
            byte[] roomIdByteData = new byte[12];
            System.arraycopy(data, index, roomIdByteData, 0, roomIdByteData.length);
            this.roomId = new String(roomIdByteData, StandardCharsets.UTF_8);
            index += roomIdByteData.length;

            byte[] peerUserIdByteData = new byte[8];
            System.arraycopy(data, index, peerUserIdByteData, 0, peerUserIdByteData.length);
            this.peerUserId = new String(peerUserIdByteData, StandardCharsets.UTF_8);

        } else {
            this.roomId = "";
            this.peerUserId = "";
        }
    }

    public HgtpRoomManagerContent(String roomId, String peerUserId) {
        this.roomId = roomId;
        this.peerUserId = peerUserId;
    }

    @Override
    public byte[] getByteData() {
        byte[] data = new byte[getBodyLength()];
        int index = 0;

        byte[] roomIdByteData = roomId.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(roomIdByteData, 0, data, index, roomIdByteData.length);
        index += roomIdByteData.length;

        byte[] peerUserIdByteData = peerUserId.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(peerUserIdByteData, 0, data, index, peerUserIdByteData.length);

        return data;
    }

    public int getBodyLength() {
        return 12 + 8;
    }

    public String getRoomId() {
        return roomId;
    }

    public String getPeerUserId() {
        return peerUserId;
    }
}
