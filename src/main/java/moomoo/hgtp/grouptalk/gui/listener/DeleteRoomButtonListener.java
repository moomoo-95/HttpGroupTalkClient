package moomoo.hgtp.grouptalk.gui.listener;

import moomoo.hgtp.grouptalk.protocol.hgtp.message.request.HgtpDeleteRoomRequest;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.request.handler.HgtpRequestHandler;
import moomoo.hgtp.grouptalk.service.AppInstance;
import moomoo.hgtp.grouptalk.session.SessionManager;
import moomoo.hgtp.grouptalk.session.base.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DeleteRoomButtonListener implements ActionListener {

    private static final Logger log = LoggerFactory.getLogger(DeleteRoomButtonListener.class);

    private static SessionManager sessionManager = SessionManager.getInstance();

    private final HgtpRequestHandler hgtpRequestHandler = new HgtpRequestHandler();

    @Override
    public void actionPerformed(ActionEvent e) {
        AppInstance appInstance = AppInstance.getInstance();

        UserInfo userInfo = sessionManager.getUserInfo(appInstance.getUserId());

        if (userInfo.getRoomId().equals("")) {
            log.warn("({}) ({}) () UserInfo has already exit the room.", userInfo.getUserId(), userInfo.getRoomId());
        } else {
            String roomId = userInfo.getRoomId();

            // create request delete room
            HgtpDeleteRoomRequest hgtpDeleteRoomRequest = new HgtpDeleteRoomRequest(
                    appInstance.getUserId(), AppInstance.SEQ_INCREMENT, roomId
            );

            hgtpRequestHandler.sendDeleteRoomRequest(hgtpDeleteRoomRequest);
        }
    }
}
