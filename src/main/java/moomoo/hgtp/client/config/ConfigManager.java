package moomoo.hgtp.client.config;

import org.ini4j.Ini;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class ConfigManager {

    private static final Logger log = LoggerFactory.getLogger(ConfigManager.class);

    private Ini ini = null;

    // SECTION
    private static final String SECTION_COMMON = "COMMON";
    private static final String SECTION_NETWORK = "NETWORK";
    private static final String SECTION_HGTP = "HGTP";
    private static final String SECTION_HTTP = "HTTP";

    // Field
    // NETWORK
    private static final String FIELD_LOCAL_LISTEN_IP = "LOCAL_LISTEN_IP";
    private static final String FIELD_TARGET_LISTEN_IP = "TARGET_LISTEN_IP";
    private static final String FIELD_SEND_BUF_SIZE = "SEND_BUF_SIZE";
    private static final String FIELD_RECV_BUF_SIZE = "RECV_BUF_SIZE";
    // HGTP
    private static final String FIELD_HGTP_LISTEN_PORT = "HGTP_LISTEN_PORT";
    private static final String FIELD_HGTP_TARGET_PORT = "HGTP_TARGET_PORT";
    private static final String FIELD_HGTP_THREAD_SIZE = "HGTP_THREAD_SIZE";
    private static final String FIELD_HGTP_EXPIRE_TIME = "HGTP_EXPIRE_TIME";
    // HTTP
    private static final String FIELD_HTTP_LISTEN_PORT = "HTTP_LISTEN_PORT";

    // COMMON
    // NETWORK
    private String localListenIp = "";
    private String targetListenIp = "";
    private int  sendBufSize = 0;
    private int  recvBufSize = 0;
    // HGTP
    private short hgtpListenPort = 0;
    private short hgtpTargetPort = 0;
    private int  hgtpThreadSize = 0;
    private long hgtpExpireTime = 0;
    // HTTP
    private short httpListenPort = 0;

    public ConfigManager(String configPath) {
        File iniFile = new File(configPath);
        if (!iniFile.isFile() || !iniFile.exists()) {
            log.warn("Not found the config path. (path={})", configPath);
            return;
        }

        try {
            this.ini = new Ini(iniFile);

            loadCommonConfig();
            loadNetworkConfig();
            loadHgtpConfig();
            loadHttpConfig();
        } catch (Exception e) {
            log.error("ConfigManager ", e);
        }
    }

    private void loadCommonConfig() {
        // nothing
        log.debug("Load [{}] config...(OK)", SECTION_COMMON);
    }

    private void loadNetworkConfig() {
        this.localListenIp = getIniValue(SECTION_NETWORK, FIELD_LOCAL_LISTEN_IP);

        this.targetListenIp = getIniValue(SECTION_NETWORK, FIELD_TARGET_LISTEN_IP);

        this.sendBufSize = Integer.parseInt(getIniValue(SECTION_NETWORK, FIELD_SEND_BUF_SIZE));
        if (sendBufSize < 1024) {
            log.warn("[{}] config [{}] : [{} -> 1048576] Warn", SECTION_NETWORK, FIELD_SEND_BUF_SIZE, sendBufSize);
            sendBufSize = 1048576;
        }

        this.recvBufSize = Integer.parseInt(getIniValue(SECTION_NETWORK, FIELD_SEND_BUF_SIZE));
        if (recvBufSize < 1024) {
            log.warn("[{}] config [{}] : [{} -> 1048576] Warn", SECTION_NETWORK, FIELD_RECV_BUF_SIZE, recvBufSize);
            recvBufSize = 1048576;
        }

        log.debug("Load [{}] config...(OK)", SECTION_NETWORK);
    }

    private void loadHgtpConfig() {
        this.hgtpListenPort = Short.parseShort(getIniValue(SECTION_HGTP, FIELD_HGTP_LISTEN_PORT));
        if (hgtpListenPort < 1024 || hgtpListenPort > 32767) {
            log.error("[{}] config [{}] : [{}] Error (1024 - 32767)", SECTION_HGTP, FIELD_HGTP_LISTEN_PORT, hgtpListenPort);
            System.exit(1);
        }

        this.hgtpTargetPort = Short.parseShort(getIniValue(SECTION_HGTP, FIELD_HGTP_TARGET_PORT));
        if (hgtpTargetPort < 1024 || hgtpTargetPort > 32767) {
            log.error("[{}] config [{}] : [{}] Error (1024 - 32767)", SECTION_HGTP, FIELD_HGTP_TARGET_PORT, hgtpTargetPort);
            System.exit(1);
        }

        this.hgtpThreadSize = Integer.parseInt(getIniValue(SECTION_HGTP, FIELD_HGTP_THREAD_SIZE));
        if (hgtpThreadSize <= 0) {
            log.warn("[{}] config [{}] : [{} -> 3] Warn", SECTION_HGTP, FIELD_HGTP_THREAD_SIZE, hgtpThreadSize);
            hgtpThreadSize = 3;
        }

        this.hgtpExpireTime = Long.parseLong(getIniValue(SECTION_HGTP, FIELD_HGTP_EXPIRE_TIME));
        if (hgtpExpireTime <= 0) {
            log.warn("[{}] config [{}] : [{} -> 3600] Warn", SECTION_HGTP, FIELD_HGTP_THREAD_SIZE, hgtpExpireTime);
            hgtpExpireTime = 3600;
        }
        log.debug("Load [{}] config...(OK)", SECTION_HGTP);
    }

    private void loadHttpConfig() {
        this.httpListenPort = Short.parseShort(getIniValue(SECTION_HTTP, FIELD_HTTP_LISTEN_PORT));
        if (httpListenPort < 1024 || httpListenPort > 32767) {
            log.error("[{}] config [{}] : [{}] Error (1024 - 32767)", SECTION_HTTP, FIELD_HTTP_LISTEN_PORT, httpListenPort);
            System.exit(1);
        }

        log.debug("Load [{}] config...(OK)", SECTION_HGTP);
    }

    private String getIniValue(String section, String key){
        String value = ini.get(section, key);
        if (value == null) {
            log.error("[{}] \"{}\" is null.", section, key);
            System.exit(1);
            return null;
        }

        value = value.trim();
        log.debug("Get [{}] config [{}] : [{}]", section, key, value);
        return  value;
    }

    private void setIniValue(String section, String key, String value) {
        try {
            ini.put(section, key, value);
            ini.store();

            log.debug("Set [{}] config [{}] : [{}]", section, key, value);
        } catch (Exception e) {
            log.warn("Fail to set [{}] config [{}] : [{}] ", section, key, value);
        }
    }

    public String getLocalListenIp() {return localListenIp;}
    public String getTargetListenIp() {return targetListenIp;}
    public int getSendBufSize() {return sendBufSize;}
    public int getRecvBufSize() {return recvBufSize;}


    public short getHgtpListenPort() {return hgtpListenPort;}
    public short getHgtpTargetPort() {return hgtpTargetPort;}
    public int getHgtpThreadSize() {return hgtpThreadSize;}
    public long getHgtpExpireTime() {return hgtpExpireTime;}

    public short getHttpListenPort() {return httpListenPort;}


}
