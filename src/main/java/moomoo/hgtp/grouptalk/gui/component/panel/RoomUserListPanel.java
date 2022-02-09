package moomoo.hgtp.grouptalk.gui.component.panel;

import moomoo.hgtp.grouptalk.protocol.hgtp.message.request.HgtpRemoveUserFromRoomRequest;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.request.handler.HgtpRequestHandler;
import moomoo.hgtp.grouptalk.service.AppInstance;
import moomoo.hgtp.grouptalk.session.SessionManager;
import moomoo.hgtp.grouptalk.session.base.RoomInfo;
import moomoo.hgtp.grouptalk.session.base.UserInfo;
import moomoo.hgtp.grouptalk.util.NetworkUtil;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.HashSet;

/**
 * @class RoomUserListPanel
 * @brief client 모드시 실행되는 GUI 내 user list in my room 를 출력하는 Panel
 */
public class RoomUserListPanel extends JPanel {

    private final JList<String> roomUserListView = new JList<>();
    private final DefaultListModel<String> model = new DefaultListModel<>();

    public RoomUserListPanel() {
        BorderLayout borderLayout = new BorderLayout();
        borderLayout.setVgap(3);
        borderLayout.setHgap(3);
        setLayout(borderLayout);

        JLabel roomUserListName = new JLabel("ROOM USER LIST");
        roomUserListName.setHorizontalAlignment(SwingConstants.CENTER);
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

                    String focusHostName = "";
                    if (index >= 0 && index < model.size()) {
                        focusHostName = model.get(index);
                    }

                    if (focusHostName.equals("") || focusHostName.equals(appInstance.getUserId())) {
                        return;
                    }
                    int isRemove = JOptionPane.showOptionDialog(
                            null,
                            "Will you force [" + focusHostName + "] to leave?",
                            "USER REFUSE",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            new String[]{"YES", "NO"},
                            "YES"
                    );

                    if (isRemove == 0) {
                        removeUser(focusHostName);
                    }
                }
            }
        });

        this.add(new JScrollPane(roomUserListView), BorderLayout.CENTER);
    }

    private void removeUser(String removeHostName) {
        AppInstance appInstance = AppInstance.getInstance();
        SessionManager sessionManager = SessionManager.getInstance();

        UserInfo userInfo = sessionManager.getUserInfo(appInstance.getUserId());
        RoomInfo roomInfo = sessionManager.getRoomInfo(userInfo.getRoomId());

        // create request remove user from room
        HgtpRemoveUserFromRoomRequest hgtpRemoveUserFromRoomRequest = new HgtpRemoveUserFromRoomRequest(
                appInstance.getUserId(), AppInstance.SEQ_INCREMENT, roomInfo.getRoomName(), removeHostName
        );

        HgtpRequestHandler hgtpRequestHandler = new HgtpRequestHandler();
        hgtpRequestHandler.sendRemoveUserFromRoomRequest(hgtpRemoveUserFromRoomRequest);
    }

    public void setRoomUserList(HashSet<String> groupUserList) {
        model.clear();

        if (groupUserList != null) {
            groupUserList.forEach(user -> model.addElement(NetworkUtil.messageDecoding(user)));
        }
    }
}
