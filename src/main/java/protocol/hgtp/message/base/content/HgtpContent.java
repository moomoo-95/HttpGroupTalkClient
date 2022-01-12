package protocol.hgtp.message.base.content;


public abstract class HgtpContent {



    public HgtpContent(byte[] data) {
    }

    public HgtpContent() {
    }

    public byte[] getByteData(){
        return new byte[0];
    }

    public int getBodyLength() {
        return 0;
    }
}
