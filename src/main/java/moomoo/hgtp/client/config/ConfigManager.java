package moomoo.hgtp.client.config;

import org.ini4j.Ini;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class ConfigManager {

    private static final Logger log = LoggerFactory.getLogger(ConfigManager.class);

    private Ini ini = null;

    // SECTION
    private static final String SECTION_COMMON = "COMMON";
    private static final String SECTION_NETWORK = "NETWORK";
    private static final String SECTION_HGTP = "HGTP";

    // Field
    private static final String FIELD_LOCAL_LISTEN_IP = "LOCAL_LISTEN_IP";
    private static final String FIELD_LOCAL_HGTP_LISTEN_PORT = "LOCAL_HGTP_LISTEN_PORT";

    // COMMON
    // NETWORK
    private String localListenIp = "";
    private int localListenPort = 0;
    // HGTP

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

        this.localListenPort = Integer.parseInt(getIniValue(SECTION_NETWORK, FIELD_LOCAL_HGTP_LISTEN_PORT));
        if (localListenPort < 1024 || localListenPort > 65535) {
            log.debug("[{}] config [{}] : [{}] Error (1024 - 65535)", SECTION_NETWORK, FIELD_LOCAL_HGTP_LISTEN_PORT, localListenPort);
            System.exit(1);
        }
        log.debug("Load [{}] config...(OK)", SECTION_NETWORK);
    }

    private void loadHgtpConfig() {
        // nothing
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
    public int getLocalListenPort() {return localListenPort;}
}
