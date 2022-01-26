package moomoo.hgtp.grouptalk.protocol.hgtp;


import moomoo.hgtp.grouptalk.config.ConfigManager;
import moomoo.hgtp.grouptalk.service.AppInstance;
import util.module.ConcurrentCyclicFIFO;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HgtpManager {

    private static HgtpManager hgtpManager = null;

    private final ExecutorService executorService;
    private final ConcurrentCyclicFIFO<byte[]> hgtpQueue;

    private ConfigManager configManager = AppInstance.getInstance().getConfigManager();

    public HgtpManager() {
        this.executorService = Executors.newFixedThreadPool(configManager.getHgtpThreadSize());
        this.hgtpQueue = new ConcurrentCyclicFIFO<>();
    }

    public static HgtpManager getInstance() {
        if (hgtpManager == null) {
            hgtpManager = new HgtpManager();
        }
        return hgtpManager;
    }

    public void startHgtp() {
        for (int index = 0; index < configManager.getHgtpThreadSize(); index++) {
            executorService.execute(new HgtpConsumer(hgtpQueue));
        }
    }

    public void stopHgtp() {
        executorService.shutdown();
    }


    public void putMessage(byte[] data) {
        this.hgtpQueue.offer(data);
    }
}
