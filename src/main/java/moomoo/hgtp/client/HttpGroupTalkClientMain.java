package moomoo.hgtp.client;

import moomoo.hgtp.client.config.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import moomoo.hgtp.client.service.ServiceManager;


public class HttpGroupTalkClientMain {

    private static final Logger log = LoggerFactory.getLogger(HttpGroupTalkClientMain.class);

    public static void main(String[] args) {
        if (args.length != 2) {
            log.error("Fail to argument &local port &remote port");
            return;
        }

        log.debug("HttpGroupTalkClientMain Start.");
        ConfigManager.getInstance();

        ServiceManager serviceManager = ServiceManager.getInstance();
        serviceManager.setPort(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
        serviceManager.loop();

    }
}
