package moomoo.hgtp.grouptalk.service;

import com.fsm.StateManager;
import com.fsm.module.StateHandler;
import instance.BaseEnvironment;
import instance.DebugLevel;
import moomoo.hgtp.grouptalk.config.ConfigManager;
import moomoo.hgtp.grouptalk.fsm.HgtpFsmManager;
import moomoo.hgtp.grouptalk.protocol.hgtp.HgtpMessageHandler;
import moomoo.hgtp.grouptalk.service.base.ProcessMode;
import moomoo.hgtp.grouptalk.util.CnameGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.ResourceManager;
import service.scheduler.schedule.ScheduleManager;
import util.module.ConcurrentCyclicFIFO;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.concurrent.TimeUnit;

public class AppInstance {

    private static final Logger log = LoggerFactory.getLogger(AppInstance.class);

    public static final String SERVER_SCHEDULE_KEY = "server-schedule-key";
    public static final String HGTP_SCHEDULE_KEY = "hgtp-schedule-key";

    public static final String ALGORITHM = "MD5";
    public static final String MD5_REALM = "HGTP_SERVICE";
    public static final String MD5_HASH_KEY = "950817";

    public static final short MAGIC_COOKIE = 0x4853; // HS

    public static final int USER_ID_SIZE = 8;
    public static final int ROOM_ID_SIZE = 12;
    public static final int SEQ_INCREMENT = 1;

    private static AppInstance appInstance = null;

    // 프로그램 모드 init (-1) , server (0) , client (1) , proxy (2)
    private ProcessMode mode = ProcessMode.DOWN;

    private ConfigManager configManager = null;

    private BaseEnvironment baseEnvironment;

    private final ConcurrentCyclicFIFO<byte[]> hgtpMessageQueue = new ConcurrentCyclicFIFO<>();
    private final ConcurrentCyclicFIFO<Object[]> httpMessageQueue = new ConcurrentCyclicFIFO<>();

    private HgtpFsmManager fsmManager;
    private StateManager stateManager;
    private StateHandler stateHandler;

    // only client
    private String userId = "";
    private boolean isManager = false;

    // only server
    private String serverNonce = "";

    public AppInstance() {
        // nothing
    }

    public static AppInstance getInstance() {
        if (appInstance == null) {
            appInstance = new AppInstance();
        }
        return appInstance;
    }

    private void initServerInstance(){
        try {
            // Decoding nonce -> realm
            MessageDigest messageDigestNonce = MessageDigest.getInstance(ALGORITHM);
            messageDigestNonce.update(MD5_REALM.getBytes(StandardCharsets.UTF_8));
            messageDigestNonce.update(MD5_HASH_KEY.getBytes(StandardCharsets.UTF_8));
            byte[] digestNonce = messageDigestNonce.digest();
            messageDigestNonce.reset();
            messageDigestNonce.update(digestNonce);

            serverNonce = new String(messageDigestNonce.digest());
        } catch (Exception e) {
            log.error("AppInstance.initServerInstance ", e);
            System.exit(1);
        }
    }

    private void initClientInstance() {
        userId = CnameGenerator.generateCnameUserId();
    }

    private void initScheduleManager() {
        ScheduleManager scheduleManager = appInstance.getScheduleManager();

        scheduleManager.initJob(AppInstance.HGTP_SCHEDULE_KEY, configManager.getHgtpThreadSize(), configManager.getRecvBufSize());

        scheduleManager.startJob(
                AppInstance.HGTP_SCHEDULE_KEY,
                new HgtpMessageHandler(
                        scheduleManager, 0, 1, TimeUnit.MILLISECONDS,
                        1, 0, true,
                        appInstance.getHgtpMessageQueue()
                )
        );
    }

    public ProcessMode getMode() {return mode;}

    public boolean setMode(int mode) {
        if (this.mode != ProcessMode.DOWN) { return false; }
        switch (mode){
            case 0:
                this.mode = ProcessMode.SERVER;
                initServerInstance();
                break;
            case 1:
                this.mode = ProcessMode.CLIENT;
                initClientInstance();
                break;
            case 2:
                this.mode = ProcessMode.PROXY;
                break;
            default:
                return false;
        }
        return true;
    }

    public ConfigManager getConfigManager() {return configManager;}

    public void setConfigManager(String configPath) {
        this.configManager = new ConfigManager(configPath);
        this.baseEnvironment = new BaseEnvironment(
                new ScheduleManager(),
                new ResourceManager(configManager.getHttpMinPort(), configManager.getHttpMaxPort()), DebugLevel.DEBUG
        );
        initScheduleManager();
    }

    public long getTimeStamp() { return System.currentTimeMillis();}

    public BaseEnvironment getBaseEnvironment() {return baseEnvironment;}

    public ScheduleManager getScheduleManager() { return this.baseEnvironment.getScheduleManager() ; }
    public ResourceManager getResourceManager() { return this.baseEnvironment.getPortResourceManager(); }


    public ConcurrentCyclicFIFO<byte[]> getHgtpMessageQueue() {return hgtpMessageQueue;}

    public ConcurrentCyclicFIFO<Object[]> getHttpMessageQueue() {return httpMessageQueue;}

    public void putHgtpMessage(byte[] data) {this.hgtpMessageQueue.offer(data);}

    public HgtpFsmManager getFsmManager() {return fsmManager;}
    public void setFsmManager(HgtpFsmManager fsmManager) {
        this.fsmManager = fsmManager;
        this.fsmManager.initState();
        this.stateManager = this.fsmManager.getStateManager();
        this.stateHandler = this.fsmManager.getStateHandler();
    }

    public StateManager getStateManager() {return stateManager;}
    public StateHandler getStateHandler() {return stateHandler;}

    // only server
    public String getServerNonce() {return serverNonce;}

    // only client
    public String getUserId() {return userId;}

    public boolean isManager() {return isManager;}
    public void setManager(boolean manager) {isManager = manager;}
}
