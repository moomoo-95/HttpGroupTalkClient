package moomoo.hgtp.client.service.scheduler.handler;

import moomoo.hgtp.client.service.scheduler.base.ScheduleUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @class SessionMonitor
 * @brief session (user, room) 의 현재 개수를 나타내는 클래스
 */
public class SessionMonitor extends ScheduleUnit {
    private static final Logger log = LoggerFactory.getLogger(SessionMonitor.class);

    public SessionMonitor(int interval) {
        super(interval);
    }

    @Override
    public void run() {
        sessionCount();
    }

    private void sessionCount() {
        // Session Average
        log.debug("[USER: {}] [ROOM: {}]", sessionManager.getUserInfoSize(), sessionManager.getRoomInfoSize());
    }
}
