package moomoo.hgtp.grouptalk.gui.listener;

import moomoo.hgtp.grouptalk.gui.GuiManager;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.request.HgtpJoinRoomRequest;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.request.handler.HgtpRequestHandler;
import moomoo.hgtp.grouptalk.service.AppInstance;
import moomoo.hgtp.grouptalk.session.SessionManager;
import moomoo.hgtp.grouptalk.session.base.UserInfo;
import org.apache.commons.net.ntp.TimeStamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class JoinRoomButtonListener implements ActionListener {

    private static final Logger log = LoggerFactory.getLogger(JoinRoomButtonListener.class);

    private static SessionManager sessionManager = SessionManager.getInstance();

    private final HgtpRequestHandler hgtpRequestHandler = new HgtpRequestHandler();

    @Override
    public void actionPerformed(ActionEvent e) {
        AppInstance appInstance = AppInstance.getInstance();

        UserInfo userInfo = sessionManager.getUserInfo(appInstance.getUserId());

        if (!userInfo.getRoomId().equals("")) {
            log.warn("({}) ({}) () UserInfo has already join the room.", userInfo.getUserId(), userInfo.getRoomId());
            return;
        }

        String roomId = GuiManager.getInstance().getRoomListPanel().getFocusRoomId();
        if (roomId.equals("")) {
            log.warn("({}) () () UserInfo haven't chosen a room yet.", userInfo.getUserId());
            return;
        }

        // create request join room
        HgtpJoinRoomRequest hgtpJoinRoomRequest = new HgtpJoinRoomRequest(
                AppInstance.MAGIC_COOKIE, appInstance.getUserId(),
                AppInstance.SEQ_INCREMENT, TimeStamp.getCurrentTime().getSeconds(), roomId
        );

        hgtpRequestHandler.sendJoinRoomRequest(hgtpJoinRoomRequest);
    }
}
