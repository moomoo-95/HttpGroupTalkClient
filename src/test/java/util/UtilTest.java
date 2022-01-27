package util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import moomoo.hgtp.grouptalk.protocol.http.message.content.HttpRoomListContent;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UtilTest {
    private static final Logger log = LoggerFactory.getLogger(UtilTest.class);

    @Test
    public void utilTest() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String testString = "{\n" +
                "  \"roomListSet\": [\n" +
                "    \"opaYpFN4+zpO\"\n" +
                "  ]\n" +
                "}";
        HttpRoomListContent userSet = gson.fromJson(testString, HttpRoomListContent.class);

        log.debug("{}", userSet);


    }
}
