package moomoo.hgtp.client.gui.listener;

import moomoo.hgtp.client.config.ConfigManager;
import moomoo.hgtp.client.protocol.hgtp.message.base.HgtpMessageType;
import moomoo.hgtp.client.protocol.hgtp.message.request.HgtpRegisterRequest;
import moomoo.hgtp.client.protocol.hgtp.message.request.handler.HgtpRequestHandler;
import moomoo.hgtp.client.service.AppInstance;
import org.apache.commons.net.ntp.TimeStamp;
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
    }
}
