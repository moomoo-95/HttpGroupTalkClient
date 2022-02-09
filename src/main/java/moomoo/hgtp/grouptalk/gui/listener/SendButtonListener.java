package moomoo.hgtp.grouptalk.gui.listener;


import moomoo.hgtp.grouptalk.gui.GuiManager;
import moomoo.hgtp.grouptalk.gui.component.panel.MessagePanel;
import moomoo.hgtp.grouptalk.protocol.http.handler.HttpMessageHandler;
import moomoo.hgtp.grouptalk.protocol.http.message.content.HttpMessageContent;
import moomoo.hgtp.grouptalk.service.AppInstance;
import moomoo.hgtp.grouptalk.session.SessionManager;
import moomoo.hgtp.grouptalk.session.base.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SendButtonListener implements ActionListener {

    private static final Logger log = LoggerFactory.getLogger(SendButtonListener.class);

    private static SessionManager sessionManager = SessionManager.getInstance();

    @Override
    public void actionPerformed(ActionEvent e) {
        AppInstance appInstance = AppInstance.getInstance();

        UserInfo userInfo = sessionManager.getUserInfo(appInstance.getUserId());
        MessagePanel messagePanel = GuiManager.getInstance().getMessagePanel();

        String message = messagePanel.getSendText();
        messagePanel.initSendText();


        if (userInfo.getRoomId().equals("")) {
            log.warn("({}) () () UserInfo are already exit the room.", userInfo.getUserId());
        } else if (message.equals("")) {
            log.warn("({}) ({}) () message is null", userInfo.getUserId(), userInfo.getRoomId());
        } else {
            // create request message
            HttpMessageContent httpMessageContent = new HttpMessageContent(userInfo.getHostName(), message);

            HttpMessageHandler httpRequestMessageHandler = new HttpMessageHandler();
            httpRequestMessageHandler.sendMessageRequest(httpMessageContent, userInfo);
        }
    }
}
