package moomoo.hgtp.grouptalk.protocol.http.base;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public abstract class HttpMessage {
    public byte[] getByteData() {
        return new byte[0];
    }

    @Override
    public String toString() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this);
    }
}