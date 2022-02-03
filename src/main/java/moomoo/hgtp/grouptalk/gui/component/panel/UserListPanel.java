package moomoo.hgtp.grouptalk.gui.component.panel;

import moomoo.hgtp.grouptalk.gui.GuiManager;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.request.HgtpInviteUserFromRoomRequest;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.request.handler.HgtpRequestHandler;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.response.handler.HgtpResponseHandler;
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

public class UserListPanel extends JPanel {

    private static final Logger log = LoggerFactory.getLogger(UserListPanel.class);

    private final JList<String> userListView = new JList<>();
    private final DefaultListModel<String> model = new DefaultListModel<>();

    public UserListPanel() {
        BorderLayout borderLayout = new BorderLayout();
        borderLayout.setVgap(3);
        borderLayout.setHgap(3);
        setLayout(borderLayout);

        JLabel userListName = new JLabel("USER LIST");
        userListName.setHorizontalAlignment(JLabel.CENTER);
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

                    String focusUserId = "";
                    if (index >= 0 && index < model.size()) {
                        focusUserId = model.get(index);
                    }

                    if (focusUserId.equals("") || focusUserId.equals(appInstance.getUserId())) {
                        return;
                    }
                    int isInvite = JOptionPane.showOptionDialog(
                            null,
                            "Do you want to invite [" + focusUserId + "] ?",
                            "USER INVITE",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            new String[]{"YES", "NO"},
                            "YES"
                    );

                    if (isInvite == 0) {
                        inviteUser(focusUserId);
                    }
                }
            }
        });

        this.add(new JScrollPane(userListView), BorderLayout.CENTER);
    }

    private void inviteUser(String inviteUserId) {
        AppInstance appInstance = AppInstance.getInstance();
        SessionManager sessionManager = SessionManager.getInstance();

        UserInfo userInfo = sessionManager.getUserInfo(appInstance.getUserId());

        if (userInfo.getRoomId().equals("")) {
            log.warn("({}) ({}) () UserInfo has already exit the room.", userInfo.getUserId(), userInfo.getRoomId());
            return;
        }

        if (inviteUserId.equals("") || inviteUserId.equals(userInfo.getUserId())) {
            log.warn("({}) () () UserInfo haven't chosen a invite userId yet.", userInfo.getUserId());
            return;
        }

        HgtpRequestHandler hgtpRequestHandler = new HgtpRequestHandler();
        // create request invite user from room
        HgtpInviteUserFromRoomRequest hgtpInviteUserFromRoomRequest = new HgtpInviteUserFromRoomRequest(
                AppInstance.MAGIC_COOKIE, appInstance.getUserId(),
                AppInstance.SEQ_INCREMENT, TimeStamp.getCurrentTime().getSeconds(),
                userInfo.getRoomId(), inviteUserId
        );

        hgtpRequestHandler.sendInviteUserFromRoomRequest(hgtpInviteUserFromRoomRequest);
    }

    public void setUserList(HashSet<String> userList) {
        model.clear();

        if (userList != null) {
            userList.forEach( value -> model.addElement(value));
        }

    }
}
