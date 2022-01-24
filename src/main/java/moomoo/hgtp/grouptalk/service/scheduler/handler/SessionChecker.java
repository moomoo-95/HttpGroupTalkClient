package moomoo.hgtp.grouptalk.service.scheduler.handler;

import moomoo.hgtp.grouptalk.service.scheduler.base.ScheduleUnit;
import moomoo.hgtp.grouptalk.session.base.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @class SessionChecker
 * @brief register 를 진행 중인 userInfo 중 비정상적인 상태의 userInfo 를 정리하는 클래스
 */
public class SessionChecker extends ScheduleUnit {

    private static final Logger log = LoggerFactory.getLogger(SessionChecker.class);
    private static final int REMOVE_TIME = 1000;

    public SessionChecker(int interval) {
        super(interval);
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
