package moomoo.hgtp.grouptalk.gui.listener;

import moomoo.hgtp.grouptalk.protocol.hgtp.message.request.HgtpExitRoomRequest;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.request.handler.HgtpRequestHandler;
import moomoo.hgtp.grouptalk.service.AppInstance;
import moomoo.hgtp.grouptalk.session.SessionManager;
import moomoo.hgtp.grouptalk.session.base.UserInfo;
import org.apache.commons.net.ntp.TimeStamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ExitRoomButtonListener implements ActionListener {

    private static final Logger log = LoggerFactory.getLogger(ExitRoomButtonListener.class);

    private static SessionManager sessionManager = SessionManager.getInstance();

    private final HgtpRequestHandler hgtpRequestHandler = new HgtpRequestHandler();

    @Override
    public void actionPerformed(ActionEvent e) {
        AppInstance appInstance = AppInstance.getInstance();

        UserInfo userInfo = sessionManager.getUserInfo(appInstance.getUserId());

        String roomId = userInfo.getRoomId();
        if (roomId.equals("")) {
            log.warn("({}) ({}) () UserInfo has already exit the room.", userInfo.getUserId(), userInfo.getRoomId());
        } else {

            // create request exit room
            HgtpExitRoomRequest hgtpExitRoomRequest = new HgtpExitRoomRequest(
                    AppInstance.MAGIC_COOKIE, appInstance.getUserId(),
                    AppInstance.SEQ_INCREMENT, TimeStamp.getCurrentTime().getSeconds(), roomId
            );

            hgtpRequestHandler.sendExitRoomRequest(hgtpExitRoomRequest);
        }
    }
}
