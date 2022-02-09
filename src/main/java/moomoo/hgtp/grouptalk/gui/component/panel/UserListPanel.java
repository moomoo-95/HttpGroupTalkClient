package moomoo.hgtp.grouptalk.gui.component.panel;

import moomoo.hgtp.grouptalk.protocol.hgtp.message.request.HgtpInviteUserFromRoomRequest;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.request.handler.HgtpRequestHandler;
import moomoo.hgtp.grouptalk.service.AppInstance;
import moomoo.hgtp.grouptalk.session.SessionManager;
import moomoo.hgtp.grouptalk.session.base.UserInfo;
import moomoo.hgtp.grouptalk.util.NetworkUtil;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Set;

/**
 * @class UserListPanel
 * @brief client 모드시 실행되는 GUI 내 user list 를 출력하는 Panel
 */
public class UserListPanel extends JPanel {

    private final JList<String> userListView = new JList<>();
    private final DefaultListModel<String> model = new DefaultListModel<>();

    public UserListPanel() {
        BorderLayout borderLayout = new BorderLayout();
        borderLayout.setVgap(3);
        borderLayout.setHgap(3);
        setLayout(borderLayout);

        JLabel userListName = new JLabel("USER LIST");
        userListName.setHorizontalAlignment(SwingConstants.CENTER);
        userListName.setPreferredSize(new Dimension(this.getWidth(), 20));
        this.add(userListName, BorderLayout.NORTH);

        initUserListView();

    }


    private void initUserListView() {
        userListView.setModel(model);
        userListView.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userListView.addMouseListener(new MouseInputAdapter() {
            AppInstance appInstance = AppInstance.getInstance();
            @Override
            public void mouseClicked(MouseEvent e) {
                if (appInstance.isManager() && SwingUtilities.isLeftMouseButton(e)) {
                    int index = userListView.locationToIndex(e.getPoint());
                    userListView.setSelectedIndex(index);

                    String focusHostName = "";
                    if (index >= 0 && index < model.size()) {
                        focusHostName = model.get(index);
                    }

                    if (focusHostName.equals("") || focusHostName.equals(appInstance.getUserId())) {
                        return;
                    }
                    int isInvite = JOptionPane.showOptionDialog(
                            null,
                            "Do you want to invite [" + focusHostName + "] ?",
                            "USER INVITE",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            new String[]{"YES", "NO"},
                            "YES"
                    );

                    if (isInvite == 0) {
                        inviteUser(focusHostName);
                    }
                }
            }
        });

        this.add(new JScrollPane(userListView), BorderLayout.CENTER);
    }

    private void inviteUser(String inviteHostName) {
        AppInstance appInstance = AppInstance.getInstance();
        SessionManager sessionManager = SessionManager.getInstance();

        UserInfo userInfo = sessionManager.getUserInfo(appInstance.getUserId());

        // create request invite user from room
        HgtpInviteUserFromRoomRequest hgtpInviteUserFromRoomRequest = new HgtpInviteUserFromRoomRequest(
                appInstance.getUserId(), AppInstance.SEQ_INCREMENT, userInfo.getRoomId(), inviteHostName
        );

        HgtpRequestHandler hgtpRequestHandler = new HgtpRequestHandler();
        hgtpRequestHandler.sendInviteUserFromRoomRequest(hgtpInviteUserFromRoomRequest);
    }

    public void setUserList(Set<String> userList) {
        model.clear();

        if (userList != null) {
            userList.forEach(user -> model.addElement(NetworkUtil.messageDecoding(user)));
        }

    }
}
