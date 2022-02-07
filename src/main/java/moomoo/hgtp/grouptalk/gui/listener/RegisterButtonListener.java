package moomoo.hgtp.grouptalk.gui.listener;

import moomoo.hgtp.grouptalk.config.ConfigManager;
import moomoo.hgtp.grouptalk.fsm.HgtpEvent;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.request.HgtpRegisterRequest;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.request.handler.HgtpRequestHandler;
import moomoo.hgtp.grouptalk.service.AppInstance;
import moomoo.hgtp.grouptalk.session.SessionManager;
import moomoo.hgtp.grouptalk.session.base.UserInfo;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegisterButtonListener implements ActionListener {

    private static SessionManager sessionManager = SessionManager.getInstance();

    private final HgtpRequestHandler hgtpRequestHandler = new HgtpRequestHandler();

    @Override
    public void actionPerformed(ActionEvent e) {
        AppInstance appInstance = AppInstance.getInstance();
        ConfigManager configManager = appInstance.getConfigManager();

        UserInfo userInfo = sessionManager.getUserInfo(appInstance.getUserId());
        appInstance.getStateHandler().fire(HgtpEvent.REGISTER, appInstance.getStateManager().getStateUnit(userInfo.getHgtpStateUnitId()));
        // create request Register
        HgtpRegisterRequest hgtpRegisterRequest = new HgtpRegisterRequest(
                appInstance.getUserId(), AppInstance.SEQ_INCREMENT,
                configManager.getHgtpExpireTime(), configManager.getLocalListenIp(), configManager.getHgtpListenPort()
        );

        hgtpRequestHandler.sendRegisterRequest(hgtpRegisterRequest, null);
    }
}
