package moomoo.hgtp.grouptalk.protocol.http.message;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import moomoo.hgtp.grouptalk.protocol.http.message.content.HttpRoomListContent;
import moomoo.hgtp.grouptalk.protocol.http.message.content.HttpRoomUserListContent;
import moomoo.hgtp.grouptalk.protocol.http.message.content.HttpUserListContent;

public class HttpMessageFactory {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * @fn createHttpRoomListContent
     * @brief string 형태의 json 데이터를 HttpRoomListContent 클래스로 변환하는 메서드
     * @param jsonString
     * @return
     */
    public static HttpRoomListContent createHttpRoomListContent(String jsonString) {
        HttpRoomListContent roomListContent = gson.fromJson(jsonString, HttpRoomListContent.class);

        return roomListContent;
    }

    /**
     * @fn createHttpUserListContent
     * @brief string 형태의 json 데이터를 HttpUserListContent 클래스로 변환하는 메서드
     * @param jsonString
     * @return
     */
    public static HttpUserListContent createHttpUserListContent(String jsonString) {
        HttpUserListContent userListContent = gson.fromJson(jsonString, HttpUserListContent.class);

        return userListContent;
    }

    /**
     * @fn createHttpUserListContent
     * @brief string 형태의 json 데이터를 HttpUserListContent 클래스로 변환하는 메서드
     * @param jsonString
     * @return
     */
    public static HttpRoomUserListContent createHttpRoomUserListContent(String jsonString) {
        HttpRoomUserListContent roomUserListContent = gson.fromJson(jsonString, HttpRoomUserListContent.class);

        return roomUserListContent;
    }
}
