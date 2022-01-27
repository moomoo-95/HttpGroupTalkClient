package moomoo.hgtp.grouptalk.protocol.http;

import moomoo.hgtp.grouptalk.config.ConfigManager;
import moomoo.hgtp.grouptalk.protocol.http.base.HttpMessageType;
import moomoo.hgtp.grouptalk.service.AppInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.module.ConcurrentCyclicFIFO;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpManager {

    private static final Logger log = LoggerFactory.getLogger(HttpManager.class);

    private static HttpManager httpManager = null;

    private final ExecutorService executorService;
    private final ConcurrentCyclicFIFO<Object[]> httpQueue;

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


    public void putMessage(Object[] httpObject) {
        if (httpObject.length != HttpMessageType.PARSE_SIZE) {
            log.warn("HttpMessage is too long. ({}) {}", httpObject.length, httpObject);
            return;
        }
        this.httpQueue.offer(httpObject);
    }
}
