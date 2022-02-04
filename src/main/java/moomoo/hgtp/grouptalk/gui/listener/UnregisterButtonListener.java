package moomoo.hgtp.grouptalk.gui.listener;

import moomoo.hgtp.grouptalk.config.ConfigManager;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.request.HgtpUnregisterRequest;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.request.handler.HgtpRequestHandler;
import moomoo.hgtp.grouptalk.service.AppInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UnregisterButtonListener implements ActionListener {

    private static final Logger log = LoggerFactory.getLogger(UnregisterButtonListener.class);

    private final HgtpRequestHandler hgtpRequestHandler = new HgtpRequestHandler();

    @Override
    public void actionPerformed(ActionEvent e) {
        AppInstance appInstance = AppInstance.getInstance();
        ConfigManager configManager = appInstance.getConfigManager();

        // create request Register
        HgtpUnregisterRequest hgtpUnregisterRequest = new HgtpUnregisterRequest(
                appInstance.getUserId(), AppInstance.SEQ_INCREMENT
        );

        hgtpRequestHandler.sendUnregisterRequest(hgtpUnregisterRequest);
    }
}
