package moomoo.hgtp.grouptalk.gui.listener;

import moomoo.hgtp.grouptalk.fsm.HgtpEvent;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.request.HgtpUnregisterRequest;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.request.handler.HgtpRequestHandler;
import moomoo.hgtp.grouptalk.service.AppInstance;
import moomoo.hgtp.grouptalk.session.SessionManager;
import moomoo.hgtp.grouptalk.session.base.UserInfo;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UnregisterButtonListener implements ActionListener {

    private static SessionManager sessionManager = SessionManager.getInstance();

    private final HgtpRequestHandler hgtpRequestHandler = new HgtpRequestHandler();

    @Override
    public void actionPerformed(ActionEvent e) {
        AppInstance appInstance = AppInstance.getInstance();

        UserInfo userInfo = sessionManager.getUserInfo(appInstance.getUserId());
        appInstance.getStateHandler().fire(HgtpEvent.UNREGISTER, appInstance.getStateManager().getStateUnit(userInfo.getHgtpStateUnitId()));
        // create request Register
        HgtpUnregisterRequest hgtpUnregisterRequest = new HgtpUnregisterRequest(
                appInstance.getUserId(), AppInstance.SEQ_INCREMENT
        );

        hgtpRequestHandler.sendUnregisterRequest(hgtpUnregisterRequest);
    }
}
