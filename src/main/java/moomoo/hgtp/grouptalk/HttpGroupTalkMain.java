package moomoo.hgtp.grouptalk;

import moomoo.hgtp.grouptalk.service.AppInstance;
import moomoo.hgtp.grouptalk.service.ServiceManager;
import moomoo.hgtp.grouptalk.util.ClassUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HttpGroupTalkMain {

    private static final Logger log = LoggerFactory.getLogger(HttpGroupTalkMain.class);

    public static void main(String[] args) {
        if (args.length != 2) {
            log.error("Fail to argument &config_path &mode");
            return;
        }

        AppInstance appInstance = AppInstance.getInstance();
        if (ClassUtil.isInteger(args[1])) {
            if (!appInstance.setMode(Integer.parseInt(args[1]))) {
                log.error("{} is not defined", args[1]);
                return;
            }
        }

        log.debug("HttpGroupTalk {} Start.", args[1]);
        appInstance.setConfigManager(args[0]);

        ServiceManager serviceManager = ServiceManager.getInstance();
        serviceManager.loop();
    }
}
