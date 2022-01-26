package moomoo.hgtp.grouptalk.protocol.http;


import io.netty.handler.codec.http.HttpObject;
import moomoo.hgtp.grouptalk.config.ConfigManager;
import moomoo.hgtp.grouptalk.service.AppInstance;
import util.module.ConcurrentCyclicFIFO;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpManager {

    private static HttpManager httpManager = null;

    private final ExecutorService executorService;
    private final ConcurrentCyclicFIFO<HttpObject> httpQueue;

    private ConfigManager configManager = AppInstance.getInstance().getConfigManager();

    public HttpManager() {
        this.executorService = Executors.newFixedThreadPool(configManager.getHttpThreadSize());
        this.httpQueue = new ConcurrentCyclicFIFO<>();
    }

    public static HttpManager getInstance() {
        if (httpManager == null) {
            httpManager = new HttpManager();
        }
        return httpManager;
    }

    public void startHttp() {
        for (int index = 0; index < configManager.getHttpThreadSize(); index++) {
            executorService.execute(new HttpConsumer(httpQueue));
        }
    }

    public void stopHttp() {
        executorService.shutdown();
    }


    public void putMessage(HttpObject httpObject) {
        this.httpQueue.offer(httpObject);
    }
}
