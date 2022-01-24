package moomoo.hgtp.grouptalk.service.scheduler;

import moomoo.hgtp.grouptalk.service.scheduler.base.ScheduleUnit;
import moomoo.hgtp.grouptalk.service.scheduler.handler.SessionChecker;
import moomoo.hgtp.grouptalk.service.scheduler.handler.SessionMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScheduleManager {

    private static final Logger log = LoggerFactory.getLogger(ScheduleManager.class);
    private static final int SCHEDULE_INTERVAL = 1000;

    private static ScheduleManager scheduleManager = null;

    private final HashMap<String, ScheduleUnit> scheduleUnitHashMap = new HashMap<>();

    private ScheduledExecutorService executorService;

    public ScheduleManager() {
        // nothing
    }

    public static ScheduleManager getInstance() {
        if (scheduleManager == null) {
            scheduleManager = new ScheduleManager();
        }
        return scheduleManager;
    }

    private void init() {
        addSchedule(SessionMonitor.class.getSimpleName(), new SessionMonitor(SCHEDULE_INTERVAL));
        addSchedule(SessionChecker.class.getSimpleName(), new SessionChecker(SCHEDULE_INTERVAL*2));
    }

    public void start() {
        init();

        executorService = Executors.newScheduledThreadPool(scheduleUnitHashMap.size());
        for (ScheduleUnit runner : scheduleUnitHashMap.values()) {
            executorService.scheduleAtFixedRate(runner,
                    runner.getInterval() - System.currentTimeMillis() % runner.getInterval(),
                    runner.getInterval(),
                    TimeUnit.MILLISECONDS);
        }
        log.debug("ScheduleManager start.");
    }

    public void stop() {
        executorService.shutdown();
        log.debug("ScheduleManager stop.");
    }

    private void addSchedule(String name, ScheduleUnit runner) {
        if (scheduleUnitHashMap.containsKey(name)) {
            log.warn("{} Runner already exist in Hashmap");
            return;
        }
        log.debug("Add Runner [{}]", name);
        scheduleUnitHashMap.put(name, runner);

    }


}
