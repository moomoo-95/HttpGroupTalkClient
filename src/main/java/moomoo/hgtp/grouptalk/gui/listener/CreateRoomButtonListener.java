package moomoo.hgtp.grouptalk.gui.listener;

import moomoo.hgtp.grouptalk.fsm.HgtpEvent;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.request.HgtpCreateRoomRequest;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.request.handler.HgtpRequestHandler;
import moomoo.hgtp.grouptalk.service.AppInstance;
import moomoo.hgtp.grouptalk.session.SessionManager;
import moomoo.hgtp.grouptalk.session.base.UserInfo;
import moomoo.hgtp.grouptalk.util.CnameGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CreateRoomButtonListener implements ActionListener {

    private static final Logger log = LoggerFactory.getLogger(CreateRoomButtonListener.class);

    private static SessionManager sessionManager = SessionManager.getInstance();

    private final HgtpRequestHandler hgtpRequestHandler = new HgtpRequestHandler();

    @Override
    public void actionPerformed(ActionEvent e) {
        AppInstance appInstance = AppInstance.getInstance();

        UserInfo userInfo = sessionManager.getUserInfo(appInstance.getUserId());

        if (!userInfo.getRoomId().equals("")) {
            log.warn("({}) ({}) () UserInfo are already in the room.", userInfo.getUserId(), userInfo.getRoomId());
        } else {
            String inputName = JOptionPane.showInputDialog(null, "Put room name.");
            if (inputName.equals("")) {
                JOptionPane.showConfirmDialog(
                        null, "Fail : The name is blank.",
                        "CREATE ROOM FAIL", JOptionPane.YES_OPTION,
                        JOptionPane.INFORMATION_MESSAGE, null
                );
                return;
            } else if (inputName.length() > 10) {
                JOptionPane.showConfirmDialog(
                        null, "Fail : The name is too long. (20 < ["+ inputName.length() +"])",
                        "CREATE ROOM FAIL", JOptionPane.YES_OPTION,
                        JOptionPane.INFORMATION_MESSAGE, null
                );
                return;
            }

            String roomId = CnameGenerator.generateCnameRoomId();
            sessionManager.addRoomInfo(roomId, inputName, userInfo.getUserId());

            appInstance.getStateHandler().fire(HgtpEvent.CREATE_ROOM, appInstance.getStateManager().getStateUnit(userInfo.getHgtpStateUnitId()));
            // create request create room
            HgtpCreateRoomRequest hgtpCreateRoomRequest = new HgtpCreateRoomRequest(
                    userInfo.getUserId(), AppInstance.SEQ_INCREMENT, roomId, inputName
            );

            hgtpRequestHandler.sendCreateRoomRequest(hgtpCreateRoomRequest);
        }
    }
}
