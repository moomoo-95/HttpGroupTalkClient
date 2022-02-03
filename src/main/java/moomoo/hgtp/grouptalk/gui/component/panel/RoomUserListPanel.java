package moomoo.hgtp.grouptalk.gui.component.panel;

import moomoo.hgtp.grouptalk.gui.GuiManager;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.request.HgtpRemoveUserFromRoomRequest;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.request.handler.HgtpRequestHandler;
import moomoo.hgtp.grouptalk.service.AppInstance;
import moomoo.hgtp.grouptalk.session.SessionManager;
import moomoo.hgtp.grouptalk.session.base.UserInfo;
import org.apache.commons.net.ntp.TimeStamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.HashSet;

public class RoomUserListPanel extends JPanel {

    private static final Logger log = LoggerFactory.getLogger(RoomUserListPanel.class);

    private final JList<String> roomUserListView = new JList<>();
    private final DefaultListModel<String> model = new DefaultListModel<>();

    private String focusRoomUserId = "";

    public RoomUserListPanel() {
        BorderLayout borderLayout = new BorderLayout();
        borderLayout.setVgap(3);
        borderLayout.setHgap(3);
        setLayout(borderLayout);

        JLabel roomUserListName = new JLabel("ROOM USER LIST");
        roomUserListName.setHorizontalAlignment(JLabel.CENTER);
        roomUserListName.setPreferredSize(new Dimension(this.getWidth(), 20));
        this.add(roomUserListName, BorderLayout.NORTH);

        initUserListView();
    }


    private void initUserListView() {
        roomUserListView.setModel(model);
        roomUserListView.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        roomUserListView.addMouseListener(new MouseInputAdapter() {
            AppInstance appInstance = AppInstance.getInstance();
            @Override
            public void mouseClicked(MouseEvent e) {
                if (appInstance.isManager() && SwingUtilities.isLeftMouseButton(e)) {
                    int index = roomUserListView.locationToIndex(e.getPoint());
                    roomUserListView.setSelectedIndex(index);

                    String focusUserId = "";
                    if (index >= 0 && index < model.size()) {
                        focusUserId = model.get(index);
                    }

                    if (focusUserId.equals("") || focusUserId.equals(appInstance.getUserId())) {
                        return;
                    }
                    int isRemove = JOptionPane.showOptionDialog(
                            null,
                            "Will you force [" + focusUserId + "] to leave?",
                            "USER REFUSE",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            new String[]{"YES", "NO"},
                            "YES"
                    );

                    if (isRemove == 0) {
                        removeUser(focusUserId);
                    }
                }
            }
        });

        this.add(new JScrollPane(roomUserListView), BorderLayout.CENTER);
    }

    private void removeUser(String removeUserId) {
        AppInstance appInstance = AppInstance.getInstance();
        SessionManager sessionManager = SessionManager.getInstance();

        UserInfo userInfo = sessionManager.getUserInfo(appInstance.getUserId());

        if (userInfo.getRoomId().equals("")) {
            log.warn("({}) ({}) () UserInfo has already exit the room.", userInfo.getUserId(), userInfo.getRoomId());
            return;
        }

        if (removeUserId.equals("") || removeUserId.equals(userInfo.getUserId())) {
            log.warn("({}) () () UserInfo haven't chosen a invite userId yet.", userInfo.getUserId());
            return;
        }

        HgtpRequestHandler hgtpRequestHandler = new HgtpRequestHandler();
        // create request remove user from room
        HgtpRemoveUserFromRoomRequest hgtpRemoveUserFromRoomRequest = new HgtpRemoveUserFromRoomRequest(
                AppInstance.MAGIC_COOKIE, appInstance.getUserId(),
                AppInstance.SEQ_INCREMENT, TimeStamp.getCurrentTime().getSeconds(),
                userInfo.getRoomId(), removeUserId
        );

        hgtpRequestHandler.sendRemoveUserFromRoomRequest(hgtpRemoveUserFromRoomRequest);
    }

    public void setRoomUserList(HashSet<String> groupUserList) {
        model.clear();

        if (groupUserList != null) {
            groupUserList.forEach( value -> model.addElement(value));
        }
    }
}
