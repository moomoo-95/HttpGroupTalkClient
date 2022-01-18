package moomoo.hgtp.client.service;

import moomoo.hgtp.client.config.ConfigManager;
import moomoo.hgtp.client.util.CnameGenerator;
import org.apache.commons.net.ntp.TimeStamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;


public class AppInstance {

    private static final Logger log = LoggerFactory.getLogger(AppInstance.class);

    public static final long SERVER_SESSION_ID = 84;
    public static final int USER_ID_SIZE = 8;
    public static final int ROOM_ID_SIZE = 12;
    public static final int SEQ_INCREMENT = 1;
    public static final short MAGIC_COOKIE = 0x4853; // HS
    public static final String ALGORITHM = "MD5";
    public static final String MD5_REALM = "HGTP_SERVICE";
    public static final String MD5_HASH_KEY = "950817";

    private static AppInstance appInstance = null;

    private final String userId;

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

    public String getUserId() {return userId;}

    public String getConfigPath() {return configPath;}

    public ConfigManager getConfigManager() {return configManager;}

    public void setConfigManager(String configPath) {
        this.configPath = configPath;
        this.configManager = new ConfigManager(configPath);
    }

    public String getServerNonce() {return serverNonce;}

    public long getTimeStamp() { return TimeStamp.getCurrentTime().getSeconds();}
}
