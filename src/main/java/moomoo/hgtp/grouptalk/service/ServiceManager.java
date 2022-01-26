package moomoo.hgtp.grouptalk.service;

import moomoo.hgtp.grouptalk.config.ConfigManager;
import moomoo.hgtp.grouptalk.gui.GuiManager;
import moomoo.hgtp.grouptalk.network.NetworkManager;
import moomoo.hgtp.grouptalk.protocol.hgtp.HgtpManager;
import moomoo.hgtp.grouptalk.protocol.http.HttpManager;
import moomoo.hgtp.grouptalk.service.scheduler.ScheduleManager;
import moomoo.hgtp.grouptalk.session.SessionManager;
import moomoo.hgtp.grouptalk.session.base.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceManager {

    private static final Logger log = LoggerFactory.getLogger(ServiceManager.class);

    private static final int DELAY_TIME = 1000;

    private static ServiceManager serviceManager = null;

    private HgtpManager hgtpManager;
    private HttpManager httpManager;
    private NetworkManager networkManager;

    // server, proxy
    private ScheduleManager scheduleManager;

    private boolean isQuit = false;

    public ServiceManager() {
        // nothing
    }

    public static ServiceManager getInstance() {
        if (serviceManager == null) {
            serviceManager = new ServiceManager();
        }
        return serviceManager;
    }

    public void loop() {
        if (!start()) {
            log.error("() () () Fail to start service");
        }

        while (!isQuit) {
            try {
                Thread.sleep(DELAY_TIME);
            } catch (Exception e) {
                log.error("ServiceManager.loop ", e);
            }
        }
    }

    public boolean start() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.error("Process is about to quit (Ctrl+C)");
            this.isQuit = true;
            this.stop();
        }));

        // HgtpManager
        hgtpManager = HgtpManager.getInstance();
        hgtpManager.startHgtp();
        // HttpManager
        httpManager = HttpManager.getInstance();
        httpManager.startHttp();

        // SessionManager
        SessionManager sessionManager = SessionManager.getInstance();

        // NetworkManager
        networkManager = NetworkManager.getInstance();
        networkManager.startSocket();

        AppInstance appInstance = AppInstance.getInstance();
        switch (appInstance.getMode()){
            case AppInstance.SERVER_MODE:
                scheduleManager = ScheduleManager.getInstance();
                scheduleManager.start();
                break;
            case AppInstance.CLIENT_MODE:
                ConfigManager configManager = appInstance.getConfigManager();
                sessionManager.addUserInfo(appInstance.getUserId(), 0);
                UserInfo userInfo = sessionManager.getUserInfo(appInstance.getUserId());
                userInfo.setHgtpTargetNetAddress(configManager.getTargetListenIp(), configManager.getHgtpTargetPort());

                GuiManager.getInstance();
                break;
            case AppInstance.PROXY_MODE:
                break;
            default:
                return false;
        }

        return true;
    }

    public void stop() {
        hgtpManager.stopHgtp();
        httpManager.stopHttp();
        networkManager.stopSocket();

        switch (AppInstance.getInstance().getMode()){
            case AppInstance.SERVER_MODE:
                scheduleManager.stop();
                break;
            case AppInstance.CLIENT_MODE:
                break;
            case AppInstance.PROXY_MODE:
                break;
            default:
        }
    }
}
