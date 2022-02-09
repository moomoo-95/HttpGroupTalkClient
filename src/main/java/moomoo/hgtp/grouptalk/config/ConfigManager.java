package moomoo.hgtp.grouptalk.config;

import org.ini4j.Ini;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class ConfigManager {

    private static final Logger log = LoggerFactory.getLogger(ConfigManager.class);

    private static final String CONFIG_LOG = "Load [{}] config...(OK)";
    private static final String PORT_RANGE_LOG = "[{}] config [{}] : [{}] Error (1024 - 32767)";

    private Ini ini = null;

    // SECTION
    private static final String SECTION_COMMON = "COMMON";
    private static final String SECTION_NETWORK = "NETWORK";
    private static final String SECTION_HGTP = "HGTP";
    private static final String SECTION_HTTP = "HTTP";

    // Field
    // COMMON
    private static final String FIELD_USER_MAX_SIZE = "USER_MAX_SIZE";
    private static final String FIELD_ROOM_MAX_SIZE = "ROOM_MAX_SIZE";
    // NETWORK
    private static final String FIELD_LOCAL_LISTEN_IP = "LOCAL_LISTEN_IP";
    private static final String FIELD_TARGET_LISTEN_IP = "TARGET_LISTEN_IP";
    private static final String FIELD_SEND_BUF_SIZE = "SEND_BUF_SIZE";
    private static final String FIELD_RECV_BUF_SIZE = "RECV_BUF_SIZE";
    private static final String FIELD_THREAD_COUNT = "THREAD_COUNT";
    // HGTP
    private static final String FIELD_HGTP_LISTEN_PORT = "HGTP_LISTEN_PORT";
    private static final String FIELD_HGTP_TARGET_PORT = "HGTP_TARGET_PORT";
    private static final String FIELD_HGTP_THREAD_COUNT = "HGTP_THREAD_COUNT";
    private static final String FIELD_HGTP_EXPIRE_TIME = "HGTP_EXPIRE_TIME";
    // HTTP
    private static final String FIELD_HTTP_MIN_PORT = "HTTP_MIN_PORT";
    private static final String FIELD_HTTP_MAX_PORT = "HTTP_MAX_PORT";

    // COMMON
    private int userMaxSize = 0;
    private int roomMaxSize = 0;
    // NETWORK
    private String localListenIp = "";
    private String targetListenIp = "";
    private int  sendBufSize = 0;
    private int  recvBufSize = 0;
    private int threadCount = 0;
    // HGTP
    private short hgtpListenPort = 0;
    private short hgtpTargetPort = 0;
    private int hgtpThreadCount = 0;
    private long hgtpExpireTime = 0;
    // HTTP
    private int  httpMinPort = 0;
    private int  httpMaxPort = 0;

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
        this.userMaxSize = Integer.parseInt(getIniValue(SECTION_COMMON, FIELD_USER_MAX_SIZE));
        if (userMaxSize <= 0 || userMaxSize > 1000000) {
            log.warn("[{}] config [{}] : [{} -> 100] Warn", SECTION_COMMON, FIELD_USER_MAX_SIZE, userMaxSize);
            userMaxSize = 100;
            setIniValue(SECTION_COMMON, FIELD_USER_MAX_SIZE, "100");
        }

        this.roomMaxSize = Integer.parseInt(getIniValue(SECTION_COMMON, FIELD_ROOM_MAX_SIZE));
        if (roomMaxSize < 0 || roomMaxSize > 1000000) {
            log.warn("[{}] config [{}] : [{} -> 100] Warn", SECTION_COMMON, FIELD_ROOM_MAX_SIZE, roomMaxSize);
            roomMaxSize = 100;
            setIniValue(SECTION_COMMON, FIELD_ROOM_MAX_SIZE, "100");
        }

        log.debug(CONFIG_LOG, SECTION_COMMON);
    }

    private void loadNetworkConfig() {
        this.localListenIp = getIniValue(SECTION_NETWORK, FIELD_LOCAL_LISTEN_IP);

        this.targetListenIp = getIniValue(SECTION_NETWORK, FIELD_TARGET_LISTEN_IP);

        this.sendBufSize = Integer.parseInt(getIniValue(SECTION_NETWORK, FIELD_SEND_BUF_SIZE));
        if (sendBufSize < 1024) {
            log.warn("[{}] config [{}] : [{} -> 1048576] Warn", SECTION_NETWORK, FIELD_SEND_BUF_SIZE, sendBufSize);
            sendBufSize = 1048576;
            setIniValue(SECTION_NETWORK, FIELD_SEND_BUF_SIZE, "1048576");
        }

        this.recvBufSize = Integer.parseInt(getIniValue(SECTION_NETWORK, FIELD_RECV_BUF_SIZE));
        if (recvBufSize < 1024) {
            log.warn("[{}] config [{}] : [{} -> 1048576] Warn", SECTION_NETWORK, FIELD_RECV_BUF_SIZE, recvBufSize);
            recvBufSize = 1048576;
            setIniValue(SECTION_NETWORK, FIELD_RECV_BUF_SIZE, "1048576");
        }

        this.threadCount = Integer.parseInt(getIniValue(SECTION_NETWORK, FIELD_THREAD_COUNT));
        if (threadCount <= 0) {
            log.warn("[{}] config [{}] : [{} -> 4] Warn", SECTION_NETWORK, FIELD_THREAD_COUNT, threadCount);
            threadCount = 4;
            setIniValue(SECTION_NETWORK, FIELD_THREAD_COUNT, "4");
        }

        log.debug(CONFIG_LOG, SECTION_NETWORK);
    }

    private void loadHgtpConfig() {
        this.hgtpListenPort = Short.parseShort(getIniValue(SECTION_HGTP, FIELD_HGTP_LISTEN_PORT));
        if (hgtpListenPort < 1024 || hgtpListenPort > 32767) {
            log.error(PORT_RANGE_LOG, SECTION_HGTP, FIELD_HGTP_LISTEN_PORT, hgtpListenPort);
            System.exit(1);
        }

        this.hgtpTargetPort = Short.parseShort(getIniValue(SECTION_HGTP, FIELD_HGTP_TARGET_PORT));
        if (hgtpTargetPort < 1024 || hgtpTargetPort > 32767) {
            log.error(PORT_RANGE_LOG, SECTION_HGTP, FIELD_HGTP_TARGET_PORT, hgtpTargetPort);
            System.exit(1);
        }

        this.hgtpThreadCount = Integer.parseInt(getIniValue(SECTION_HGTP, FIELD_HGTP_THREAD_COUNT));
        if (hgtpThreadCount <= 0) {
            log.warn("[{}] config [{}] : [{} -> 4] Warn", SECTION_HGTP, FIELD_HGTP_THREAD_COUNT, hgtpThreadCount);
            hgtpThreadCount = 4;
            setIniValue(SECTION_HGTP, FIELD_HGTP_THREAD_COUNT, "4");
        }

        this.hgtpExpireTime = Long.parseLong(getIniValue(SECTION_HGTP, FIELD_HGTP_EXPIRE_TIME));
        if (hgtpExpireTime <= 0) {
            log.warn("[{}] config [{}] : [{} -> 3600] Warn", SECTION_HGTP, FIELD_HGTP_THREAD_COUNT, hgtpExpireTime);
            hgtpExpireTime = 3600;
            setIniValue(SECTION_HGTP, FIELD_HGTP_EXPIRE_TIME, "3600");
        }
        log.debug(CONFIG_LOG, SECTION_HGTP);
    }

    private void loadHttpConfig() {
        this.httpMinPort = Short.parseShort(getIniValue(SECTION_HTTP, FIELD_HTTP_MIN_PORT));
        if (httpMinPort < 1024 || httpMinPort > 32767) {
            log.error(PORT_RANGE_LOG, SECTION_HTTP, FIELD_HTTP_MIN_PORT, httpMinPort);
            System.exit(1);
        }

        this.httpMaxPort = Short.parseShort(getIniValue(SECTION_HTTP, FIELD_HTTP_MAX_PORT));
        if (httpMaxPort < 1024 || httpMaxPort > 32767) {
            log.error(PORT_RANGE_LOG, SECTION_HTTP, FIELD_HTTP_MAX_PORT, httpMaxPort);
            System.exit(1);
        }

        if (httpMinPort > httpMaxPort) {
            log.error("[{}] config [{} > {}] Error. {} > {}", SECTION_HTTP, FIELD_HTTP_MIN_PORT, FIELD_HTTP_MAX_PORT, httpMinPort, httpMaxPort);
            System.exit(1);
        }

        log.debug(CONFIG_LOG, SECTION_HGTP);
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

    // common
    public int getUserMaxSize() {return userMaxSize;}
    public int getRoomMaxSize() {return roomMaxSize;}

    // network
    public String getLocalListenIp() {return localListenIp;}
    public String getTargetListenIp() {return targetListenIp;}
    public int getSendBufSize() {return sendBufSize;}
    public int getRecvBufSize() {return recvBufSize;}
    public int getThreadCount() {return threadCount;}

    // hgtp
    public short getHgtpListenPort() {return hgtpListenPort;}
    public short getHgtpTargetPort() {return hgtpTargetPort;}
    public int getHgtpThreadCount() {return hgtpThreadCount;}
    public long getHgtpExpireTime() {return hgtpExpireTime;}

    // http
    public int getHttpMinPort() {return httpMinPort;}
    public int getHttpMaxPort() {return httpMaxPort;}
}
