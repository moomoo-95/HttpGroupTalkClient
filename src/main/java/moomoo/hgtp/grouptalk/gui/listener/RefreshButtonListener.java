package moomoo.hgtp.grouptalk.gui.listener;

import moomoo.hgtp.grouptalk.protocol.http.handler.HttpMessageHandler;
import moomoo.hgtp.grouptalk.service.AppInstance;
import moomoo.hgtp.grouptalk.session.SessionManager;
import moomoo.hgtp.grouptalk.session.base.UserInfo;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RefreshButtonListener implements ActionListener {

    private static SessionManager sessionManager = SessionManager.getInstance();

    private long lastClickTime = 0L;

    @Override
    public void actionPerformed(ActionEvent e) {
        long currentClickTime = System.currentTimeMillis();
        if (currentClickTime - lastClickTime > 5000) {
            UserInfo userInfo = sessionManager.getUserInfo(AppInstance.getInstance().getUserId());

            if (userInfo != null) {
                HttpMessageHandler httpRequestMessageHandler = new HttpMessageHandler();
                httpRequestMessageHandler.sendRefreshRequest(userInfo);
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
