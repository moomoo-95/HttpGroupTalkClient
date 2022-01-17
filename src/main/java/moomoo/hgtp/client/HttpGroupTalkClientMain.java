package moomoo.hgtp.client;

import moomoo.hgtp.client.service.AppInstance;
import moomoo.hgtp.client.service.ServiceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HttpGroupTalkClientMain {

    private static final Logger log = LoggerFactory.getLogger(HttpGroupTalkClientMain.class);

    public static void main(String[] args) {
        if (args.length != 2) {
            log.error("Fail to argument &config path &mode");
            return;
        }

        log.debug("HttpGroupTalkClientMain Start.");
        AppInstance appInstance = AppInstance.getInstance();
        switch (args[1]){
            case "server":
                appInstance.setServer(true);
                break;
            case "client":
                appInstance.setServer(false);
                break;
            default:
                log.error("Fail to mode argument");
                return;
        }
        appInstance.setConfigManager(args[0]);

        ServiceManager serviceManager = ServiceManager.getInstance();
        serviceManager.loop();

    }
}
