package moomoo.hgtp.grouptalk.service.scheduler;

import moomoo.hgtp.grouptalk.session.SessionManager;
import moomoo.hgtp.grouptalk.session.base.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.scheduler.job.Job;
import service.scheduler.schedule.ScheduleManager;

import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class SessionCheckJob extends Job {

    private static final Logger log = LoggerFactory.getLogger(SessionCheckJob.class);
    private static final int REMOVE_TIME = 1000;

    private final SessionManager sessionManager = SessionManager.getInstance();

    public SessionCheckJob(ScheduleManager scheduleManager, String name, int initialDelay, int interval, TimeUnit timeUnit, int priority, int totalRunCount, boolean isLasted) {
        super(scheduleManager, name, initialDelay, interval, timeUnit, priority, totalRunCount, isLasted);
    }

    @Override
    public void run() {
        sessionRegisterCheck();
    }

    private void sessionRegisterCheck() {
        ConcurrentHashMap<String, UserInfo> userInfoHashMap = (ConcurrentHashMap<String, UserInfo>) sessionManager.getUserInfoHashMap();
        if (userInfoHashMap.size() > 0) {
            HashSet<String> removeUserInfoSet = new HashSet<>();
            long currentTime = System.currentTimeMillis();
            userInfoHashMap.forEach((key, userInfo) -> {
                if (!userInfo.isRegister() && userInfo.getCreateTime() + REMOVE_TIME < currentTime) {
                    removeUserInfoSet.add(key);
                }
            });
            if (removeUserInfoSet.isEmpty()) {
                removeUserInfoSet.forEach(key -> {
                    sessionManager.deleteUserInfo(key);
                    log.debug("{} UserInfo is Deleted. (Do not Received second register)", key);
                });
                removeUserInfoSet.clear();
            }
        }
    }
}
