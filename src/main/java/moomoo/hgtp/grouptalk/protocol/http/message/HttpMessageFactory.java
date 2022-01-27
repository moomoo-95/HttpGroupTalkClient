package moomoo.hgtp.grouptalk.protocol.http.message;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import moomoo.hgtp.grouptalk.protocol.http.message.content.HttpRoomListContent;

public class HttpMessageFactory {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static HttpRoomListContent createHttpRoomListContent(String jsonString) {
        HttpRoomListContent roomListContent = gson.fromJson(jsonString, HttpRoomListContent.class);

        return roomListContent;
    }
}
