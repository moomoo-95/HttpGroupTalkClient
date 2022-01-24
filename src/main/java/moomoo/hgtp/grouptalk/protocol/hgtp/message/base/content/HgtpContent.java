package moomoo.hgtp.grouptalk.protocol.hgtp.message.base.content;


public interface HgtpContent {
    byte[] getByteData();

    int getBodyLength();
}
