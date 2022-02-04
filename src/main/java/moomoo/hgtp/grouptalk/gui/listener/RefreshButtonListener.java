package moomoo.hgtp.grouptalk.gui.listener;

import moomoo.hgtp.grouptalk.protocol.http.handler.HttpRequestMessageHandler;
import moomoo.hgtp.grouptalk.service.AppInstance;
import moomoo.hgtp.grouptalk.session.SessionManager;
import moomoo.hgtp.grouptalk.session.base.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RefreshButtonListener implements ActionListener {

    private static final Logger log = LoggerFactory.getLogger(RefreshButtonListener.class);

    private static SessionManager sessionManager = SessionManager.getInstance();

    @Override
    public void actionPerformed(ActionEvent e) {
        UserInfo userInfo = sessionManager.getUserInfo(AppInstance.getInstance().getUserId());

        if (userInfo != null) {
            HttpRequestMessageHandler httpRequestMessageHandler = new HttpRequestMessageHandler();
            httpRequestMessageHandler.sendRefreshRequest(userInfo);
        }
    }
}
