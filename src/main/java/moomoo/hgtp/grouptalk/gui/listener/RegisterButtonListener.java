package moomoo.hgtp.grouptalk.gui.listener;

import moomoo.hgtp.grouptalk.config.ConfigManager;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.request.HgtpRegisterRequest;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.request.handler.HgtpRequestHandler;
import moomoo.hgtp.grouptalk.service.AppInstance;
import org.apache.commons.net.ntp.TimeStamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegisterButtonListener implements ActionListener {

    private static final Logger log = LoggerFactory.getLogger(RegisterButtonListener.class);

    private final HgtpRequestHandler hgtpRequestHandler = new HgtpRequestHandler();

    @Override
    public void actionPerformed(ActionEvent e) {
        AppInstance appInstance = AppInstance.getInstance();
        ConfigManager configManager = appInstance.getConfigManager();

        // create request Register
        HgtpRegisterRequest hgtpRegisterRequest = new HgtpRegisterRequest(
                AppInstance.MAGIC_COOKIE, appInstance.getUserId(),
                AppInstance.SEQ_INCREMENT, TimeStamp.getCurrentTime().getSeconds(),
                configManager.getHgtpExpireTime(), configManager.getLocalListenIp(), configManager.getHgtpListenPort()
        );

        hgtpRequestHandler.sendRegisterRequest(hgtpRegisterRequest, null);
    }
}
