package moomoo.hgtp.grouptalk.protocol.http.message;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import moomoo.hgtp.grouptalk.protocol.http.message.content.*;

/**
 * @class HttpMessageFactory
 * @brief Http message 를 생성하는 factory class
 */
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
     * @fn createHttpRoomUserListContent
     * @brief string 형태의 json 데이터를 HttpRoomUserListContent 클래스로 변환하는 메서드
     * @param jsonString
     * @return
     */
    public static HttpRoomUserListContent createHttpRoomUserListContent(String jsonString) {
        HttpRoomUserListContent roomUserListContent = gson.fromJson(jsonString, HttpRoomUserListContent.class);

        return roomUserListContent;
    }

    /**
     * @fn createHttpMessageContent
     * @brief string 형태의 json 데이터를 HttpMessageContent 클래스로 변환하는 메서드
     * @param jsonString
     * @return
     */
    public static HttpMessageContent createHttpMessageContent(String jsonString) {
        HttpMessageContent messageContent = gson.fromJson(jsonString, HttpMessageContent.class);

        return messageContent;
    }

    /**
     * @fn createHttpNoticeContent
     * @brief string 형태의 json 데이터를 createHttpNoticeContent 클래스로 변환하는 메서드
     * @param jsonString
     * @return
     */
    public static HttpNoticeContent createHttpNoticeContent(String jsonString) {
        HttpNoticeContent messageContent = gson.fromJson(jsonString, HttpNoticeContent.class);

        return messageContent;
    }

    /**
     * @fn createHttpRefreshContent
     * @brief string 형태의 json 데이터를 createHttpRefreshContent 클래스로 변환하는 메서드
     * @param jsonString
     * @return
     */
    public static HttpRefreshContent createHttpRefreshContent(String jsonString) {
        HttpRefreshContent messageContent = gson.fromJson(jsonString, HttpRefreshContent.class);

        return messageContent;
    }
}
