package moomoo.hgtp.client.gui.listener;

import moomoo.hgtp.client.config.ConfigManager;
import moomoo.hgtp.client.network.NetworkManager;
import moomoo.hgtp.client.protocol.hgtp.message.base.HgtpMessageType;
import moomoo.hgtp.client.protocol.hgtp.message.request.HgtpRegisterRequest;
import moomoo.hgtp.client.service.AppInstance;
import network.definition.DestinationRecord;
import org.apache.commons.net.ntp.TimeStamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegisterButtonListener implements ActionListener {

    private static final Logger log = LoggerFactory.getLogger(RegisterButtonListener.class);

    @Override
    public void actionPerformed(ActionEvent e) {
        AppInstance appInstance = AppInstance.getInstance();
        ConfigManager configManager = appInstance.getConfigManager();

        // Send Register
        HgtpRegisterRequest hgtpRegisterRequest = new HgtpRegisterRequest(
                AppInstance.MAGIC_COOKIE, HgtpMessageType.REGISTER, appInstance.getUserId(),
                1, TimeStamp.getCurrentTime().getSeconds(),
                configManager.getHgtpExpireTime(), configManager.getLocalListenIp(), configManager.getHgtpListenPort()
                );
        byte[] data = hgtpRegisterRequest.getByteData();

        DestinationRecord destinationRecord = NetworkManager.getInstance().getHgtpGroupSocket().getDestination(AppInstance.SERVER_SESSION_ID);
        if (destinationRecord == null) {
            log.warn("({}) () () DestinationRecord Channel is null.", appInstance.getUserId());
        }
        destinationRecord.getNettyChannel().sendData(data, data.length);
        log.debug("({}) () () [{}] SEND DATA {}", appInstance.getUserId(), HgtpMessageType.REQUEST_HASHMAP.get(HgtpMessageType.REGISTER), hgtpRegisterRequest);

    }
}
