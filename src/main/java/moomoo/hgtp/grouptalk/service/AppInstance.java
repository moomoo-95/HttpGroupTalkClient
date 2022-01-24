package moomoo.hgtp.grouptalk.service;

import moomoo.hgtp.grouptalk.config.ConfigManager;
import moomoo.hgtp.grouptalk.util.CnameGenerator;
import org.apache.commons.net.ntp.TimeStamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;


public class AppInstance {

    private static final Logger log = LoggerFactory.getLogger(AppInstance.class);

    public static final String ALGORITHM = "MD5";
    public static final String MD5_REALM = "HGTP_SERVICE";
    public static final String MD5_HASH_KEY = "950817";

    public static final short MAGIC_COOKIE = 0x4853; // HS
    public static final int SERVER_MODE = 0;
    public static final int CLIENT_MODE = 1;
    public static final int PROXY_MODE = 2;
    public static final int USER_ID_SIZE = 8;
    public static final int ROOM_ID_SIZE = 12;
    public static final int SEQ_INCREMENT = 1;
    public static final long SERVER_SESSION_ID = 84;

    private static AppInstance appInstance = null;

    // 프로그램 모드 -1 : init / 0 : server / 1 : client / 2 : proxy
    private int mode = -1;

    private final String userId;
    private String roomId = "";

    private String configPath = "";
    private ConfigManager configManager = null;

    private String serverNonce = "";

    public AppInstance() {
        userId = CnameGenerator.generateCnameUserId();

        try {
            // Decoding nonce -> realm
            MessageDigest messageDigestNonce = MessageDigest.getInstance(ALGORITHM);
            messageDigestNonce.update(AppInstance.MD5_REALM.getBytes(StandardCharsets.UTF_8));
            messageDigestNonce.update(AppInstance.MD5_HASH_KEY.getBytes(StandardCharsets.UTF_8));
            byte[] digestNonce = messageDigestNonce.digest();
            messageDigestNonce.reset();
            messageDigestNonce.update(digestNonce);

            serverNonce = new String(messageDigestNonce.digest());
        } catch (Exception e) {
            log.error("AppInstance ", e);
            System.exit(1);
        }
    }

    public static AppInstance getInstance() {
        if (appInstance == null) {
            appInstance = new AppInstance();
        }
        return appInstance;
    }

    public int getMode() {return mode;}

    public boolean setMode(String mode) {
        if (this.mode != -1) { return false; }
        switch (mode){
            case "server":
                this.mode = SERVER_MODE;
                break;
            case "client":
                this.mode = CLIENT_MODE;
                break;
            case "proxy":
                this.mode = PROXY_MODE;
                break;
            default:
                return false;
        }
        return true;
    }

    public String getUserId() {return userId;}

    public String getRoomId() {return roomId;}
    public void setRoomId(String roomId) {this.roomId = roomId;}
    public void initRoomId() {this.roomId = "";}

    public String getConfigPath() {return configPath;}

    public ConfigManager getConfigManager() {return configManager;}

    public void setConfigManager(String configPath) {
        this.configPath = configPath;
        this.configManager = new ConfigManager(configPath);
    }

    public String getServerNonce() {return serverNonce;}

    public long getTimeStamp() { return TimeStamp.getCurrentTime().getSeconds();}
}
