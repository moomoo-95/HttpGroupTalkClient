package com.hgtp.client.protocol.hgtp.message.base.content;


public interface HgtpContent {
    byte[] getByteData();

    int getBodyLength();
}
