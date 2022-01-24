package moomoo.hgtp.grouptalk.gui.listener;

import moomoo.hgtp.grouptalk.protocol.hgtp.message.base.HgtpMessageType;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.request.HgtpCreateRoomRequest;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.request.handler.HgtpRequestHandler;
import moomoo.hgtp.grouptalk.service.AppInstance;
import moomoo.hgtp.grouptalk.util.CnameGenerator;
import org.apache.commons.net.ntp.TimeStamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CreateRoomButtonListener implements ActionListener {

    private static final Logger log = LoggerFactory.getLogger(CreateRoomButtonListener.class);

    private final HgtpRequestHandler hgtpRequestHandler = new HgtpRequestHandler();

    @Override
    public void actionPerformed(ActionEvent e) {
        AppInstance appInstance = AppInstance.getInstance();

        if (!appInstance.getRoomId().equals("")) {
            log.debug("({}) ({}) () UserInfo are already in the room.", appInstance.getUserId(), appInstance.getRoomId());
        } else {
            String roomId = CnameGenerator.generateCnameRoomId();
            appInstance.setRoomId(roomId);

            // Send create room
            HgtpCreateRoomRequest hgtpCreateRoomRequest = new HgtpCreateRoomRequest(
                    AppInstance.MAGIC_COOKIE, HgtpMessageType.CREATE_ROOM, appInstance.getUserId(),
                    AppInstance.SEQ_INCREMENT, TimeStamp.getCurrentTime().getSeconds(), roomId
            );

            hgtpRequestHandler.sendCreateRoomRequest(hgtpCreateRoomRequest);
        }


    }
}
