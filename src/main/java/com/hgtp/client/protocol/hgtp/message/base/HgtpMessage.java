package com.hgtp.client.protocol.hgtp.message.base;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public abstract class HgtpMessage {
    public byte[] getByteData() {
        return new byte[0];
    }

    @Override
    public String toString() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this);
    }
}
