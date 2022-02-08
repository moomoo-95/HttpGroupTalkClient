package moomoo.hgtp.grouptalk.gui.listener;

import moomoo.hgtp.grouptalk.protocol.hgtp.message.request.HgtpRefreshRequest;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.request.handler.HgtpRequestHandler;
import moomoo.hgtp.grouptalk.service.AppInstance;
import moomoo.hgtp.grouptalk.session.SessionManager;
import moomoo.hgtp.grouptalk.session.base.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RefreshButtonListener implements ActionListener {

    private static final Logger log = LoggerFactory.getLogger(CreateRoomButtonListener.class);

    private static SessionManager sessionManager = SessionManager.getInstance();

    private final HgtpRequestHandler hgtpRequestHandler = new HgtpRequestHandler();

    private long lastClickTime = 0L;

    @Override
    public void actionPerformed(ActionEvent e) {
        AppInstance appInstance = AppInstance.getInstance();

        long currentClickTime = System.currentTimeMillis();
        if (currentClickTime - lastClickTime > 5000) {
            UserInfo userInfo = sessionManager.getUserInfo(appInstance.getUserId());

            if (userInfo != null) {
                HgtpRefreshRequest hgtpRefreshRequest = new HgtpRefreshRequest(userInfo.getUserId(), AppInstance.SEQ_INCREMENT);
                hgtpRequestHandler.sendRefreshRequest(hgtpRefreshRequest);
            }
            lastClickTime = currentClickTime;
        } else {
            JOptionPane.showConfirmDialog(
                    null,
                    "at least after 5 seconds [" + (currentClickTime - lastClickTime) + "ms]",
                    "Too many requests.",
                    JOptionPane.YES_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null
            );
        }
    }
}
