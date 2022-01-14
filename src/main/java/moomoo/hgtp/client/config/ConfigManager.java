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
    // HGTP
    private static final String FIELD_HGTP_LISTEN_PORT = "HGTP_LISTEN_PORT";
    private static final String FIELD_HGTP_THREAD_SIZE = "HGTP_THREAD_SIZE";
    private static final String FIELD_HGTP_EXPIRE_TIME = "HGTP_EXPIRE_TIME";
    // HTTP
    private static final String FIELD_HTTP_LISTEN_PORT = "HTTP_LISTEN_PORT";

    // COMMON
    // NETWORK
    private String localListenIp = "";
    // HGTP
    private short hgtpListenPort = 0;
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

        log.debug("Load [{}] config...(OK)", SECTION_NETWORK);
    }

    private void loadHgtpConfig() {
        this.hgtpListenPort = Short.parseShort(getIniValue(SECTION_HGTP, FIELD_HGTP_LISTEN_PORT));
        if (hgtpListenPort < 1024 || hgtpListenPort > 32767) {
            log.debug("[{}] config [{}] : [{}] Error (1024 - 32767)", SECTION_HGTP, FIELD_HGTP_LISTEN_PORT, hgtpListenPort);
            System.exit(1);
        }

        this.hgtpThreadSize = Integer.parseInt(getIniValue(SECTION_HGTP, FIELD_HGTP_THREAD_SIZE));
        if (hgtpThreadSize <= 0) {
            log.debug("[{}] config [{}] : [{} -> 3] Warn", SECTION_HGTP, FIELD_HGTP_THREAD_SIZE, hgtpThreadSize);
            hgtpThreadSize = 3;
        }

        this.hgtpExpireTime = Long.parseLong(getIniValue(SECTION_HGTP, FIELD_HGTP_EXPIRE_TIME));
        if (hgtpExpireTime <= 0) {
            log.debug("[{}] config [{}] : [{} -> 3600] Warn", SECTION_HGTP, FIELD_HGTP_THREAD_SIZE, hgtpExpireTime);
            hgtpExpireTime = 3600;
        }
        log.debug("Load [{}] config...(OK)", SECTION_HGTP);
    }

    private void loadHttpConfig() {
        this.httpListenPort = Short.parseShort(getIniValue(SECTION_HTTP, FIELD_HTTP_LISTEN_PORT));
        if (httpListenPort < 1024 || httpListenPort > 32767) {
            log.debug("[{}] config [{}] : [{}] Error (1024 - 32767)", SECTION_HTTP, FIELD_HTTP_LISTEN_PORT, httpListenPort);
            System.exit(1);
        }

        log.debug("Load [{}] config...(OK)", SECTION_HGTP);
    }

    private String getIniValue(String section, String key){
        String value = ini.get(section, key);
        if (value == null) {
            log.warn("[{}] \"{}\" is null.", section, key);
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

    public short getHgtpListenPort() {return hgtpListenPort;}
    public int getHgtpThreadSize() {return hgtpThreadSize;}
    public long getHgtpExpireTime() {return hgtpExpireTime;}

    public short getHttpListenPort() {return httpListenPort;}
}
