package moomoo.hgtp.grouptalk.service.scheduler;

import moomoo.hgtp.grouptalk.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.scheduler.job.Job;
import service.scheduler.schedule.ScheduleManager;

import java.util.concurrent.TimeUnit;

public class SessionMonitorJob extends Job {

    private static final Logger log = LoggerFactory.getLogger(SessionMonitorJob.class);

    private final SessionManager sessionManager = SessionManager.getInstance();

    public SessionMonitorJob(ScheduleManager scheduleManager, String name, int initialDelay, int interval, TimeUnit timeUnit, int priority, int totalRunCount, boolean isLasted) {
        super(scheduleManager, name, initialDelay, interval, timeUnit, priority, totalRunCount, isLasted);
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
