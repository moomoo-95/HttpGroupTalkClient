package moomoo.hgtp.grouptalk.service;

import moomoo.hgtp.grouptalk.config.ConfigManager;
import moomoo.hgtp.grouptalk.fsm.HgtpFsmManager;
import moomoo.hgtp.grouptalk.gui.GuiManager;
import moomoo.hgtp.grouptalk.network.NetworkManager;
import moomoo.hgtp.grouptalk.service.scheduler.SessionCheckJob;
import moomoo.hgtp.grouptalk.service.scheduler.SessionMonitorJob;
import moomoo.hgtp.grouptalk.session.SessionManager;
import moomoo.hgtp.grouptalk.session.base.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.scheduler.schedule.ScheduleManager;

import java.util.concurrent.TimeUnit;

public class ServiceManager {

    private static final Logger log = LoggerFactory.getLogger(ServiceManager.class);

    private static final int DELAY_TIME = 1000;

    private static ServiceManager serviceManager = null;

    private final AppInstance appInstance = AppInstance.getInstance();

    private NetworkManager networkManager;

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

        // HgtpFsmStateManager
        appInstance.setFsmManager(new HgtpFsmManager());

        // SessionManager
        SessionManager sessionManager = SessionManager.getInstance();


        // NetworkManager
        networkManager = NetworkManager.getInstance();
        networkManager.startSocket();

        switch (appInstance.getMode()){
            case SERVER:
                ScheduleManager scheduleManager = appInstance.getScheduleManager();
                scheduleManager.initJob(AppInstance.SERVER_SCHEDULE_KEY, 10, 10);

                scheduleManager.startJob(
                        AppInstance.SERVER_SCHEDULE_KEY,
                        new SessionCheckJob(
                                scheduleManager, SessionCheckJob.class.getSimpleName(),
                                0, 1000, TimeUnit.MILLISECONDS, 1, 0, true
                        )
                );
                scheduleManager.startJob(
                        AppInstance.SERVER_SCHEDULE_KEY,
                        new SessionMonitorJob(
                                scheduleManager, SessionMonitorJob.class.getSimpleName(),
                                0, 1000, TimeUnit.MILLISECONDS, 1, 0, true
                        )
                );

                break;
            case CLIENT:
                ConfigManager configManager = appInstance.getConfigManager();
                sessionManager.addUserInfo(appInstance.getUserId(), 0);
                UserInfo userInfo = sessionManager.getUserInfo(appInstance.getUserId());
                userInfo.setHgtpTargetNetAddress(configManager.getTargetListenIp(), configManager.getHgtpTargetPort());

                GuiManager.getInstance();
                break;
            case PROXY:
                break;
            default:
                return false;
        }

        return true;
    }

    public void stop() {
        networkManager.stopSocket();

        appInstance.getBaseEnvironment().stop();
    }
}
