package moomoo.hgtp.client.gui.listener;

import moomoo.hgtp.client.config.ConfigManager;
import moomoo.hgtp.client.protocol.hgtp.message.base.HgtpMessageType;
import moomoo.hgtp.client.protocol.hgtp.message.request.HgtpCreateRoomRequest;
import moomoo.hgtp.client.protocol.hgtp.message.request.HgtpDeleteRoomRequest;
import moomoo.hgtp.client.protocol.hgtp.message.request.handler.HgtpRequestHandler;
import moomoo.hgtp.client.service.AppInstance;
import moomoo.hgtp.client.util.CnameGenerator;
import org.apache.commons.net.ntp.TimeStamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DeleteRoomButtonListener implements ActionListener {

    private static final Logger log = LoggerFactory.getLogger(DeleteRoomButtonListener.class);

    private final HgtpRequestHandler hgtpRequestHandler = new HgtpRequestHandler();

    @Override
    public void actionPerformed(ActionEvent e) {
        AppInstance appInstance = AppInstance.getInstance();

        if (appInstance.getRoomId().equals("")) {
            log.debug("({}) ({}) () UserInfo has already exit the room.", appInstance.getUserId(), appInstance.getRoomId());
        } else {
            String roomId = appInstance.getRoomId();

            // Send create room
            HgtpDeleteRoomRequest hgtpDeleteRoomRequest = new HgtpDeleteRoomRequest(
                    AppInstance.MAGIC_COOKIE, HgtpMessageType.DELETE_ROOM, appInstance.getUserId(),
                    AppInstance.SEQ_INCREMENT, TimeStamp.getCurrentTime().getSeconds(), roomId
            );

            hgtpRequestHandler.sendDeleteRoomRequest(hgtpDeleteRoomRequest);
        }
    }
}
