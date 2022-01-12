package protocol.hgtp.message.base.content;


public interface HgtpContent {
    byte[] getByteData();

    int getBodyLength();
}
