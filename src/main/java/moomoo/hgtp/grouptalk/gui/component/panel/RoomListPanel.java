package moomoo.hgtp.grouptalk.gui.component.panel;

import moomoo.hgtp.grouptalk.fsm.HgtpEvent;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.request.HgtpJoinRoomRequest;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.request.handler.HgtpRequestHandler;
import moomoo.hgtp.grouptalk.service.AppInstance;
import moomoo.hgtp.grouptalk.session.SessionManager;
import moomoo.hgtp.grouptalk.session.base.UserInfo;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.HashSet;

/**
 * @class RoomListPanel
 * @brief client 모드시 실행되는 GUI 내 room list 를 출력하는 Panel
 */
public class RoomListPanel extends JPanel {

    private final JList<String> roomListView = new JList<>();
    private final DefaultListModel<String> model = new DefaultListModel<>();

    public RoomListPanel() {
        BorderLayout borderLayout = new BorderLayout();
        borderLayout.setVgap(3);
        borderLayout.setHgap(3);
        setLayout(borderLayout);

        JLabel roomListName = new JLabel("ROOM LIST");
        roomListName.setHorizontalAlignment(JLabel.CENTER);
        roomListName.setPreferredSize(new Dimension(this.getWidth(), 20));
        this.add(roomListName, BorderLayout.NORTH);

        initRoomListView();

    }

    private void initRoomListView() {
        roomListView.setModel(model);
        roomListView.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        roomListView.addMouseListener(new MouseInputAdapter() {
            AppInstance appInstance = AppInstance.getInstance();
            SessionManager sessionManager = SessionManager.getInstance();
            @Override
            public void mouseClicked(MouseEvent e) {
                if (sessionManager.getUserInfo(appInstance.getUserId()).getRoomId().equals("") && SwingUtilities.isLeftMouseButton(e)) {
                    int index = roomListView.locationToIndex(e.getPoint());
                    roomListView.setSelectedIndex(index);

                    String focusRoomId = "";
                    if (index >= 0 && index < model.size()) {
                        focusRoomId = model.get(index);
                    }

                    if (focusRoomId.equals("")) {
                        return;
                    }

                    int isRemove = JOptionPane.showOptionDialog(
                            null,
                            "Would you like to join the [" + focusRoomId + "] room?",
                            "JOIN ROOM",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            new String[]{"YES", "NO"},
                            "YES"
                    );

                    if (isRemove == 0) {
                        joinRoom(focusRoomId);
                    }
                }
            }
        });

        this.add(new JScrollPane(roomListView), BorderLayout.CENTER);
    }

    private void joinRoom(String joinRoomId) {
        AppInstance appInstance = AppInstance.getInstance();


        UserInfo userInfo = SessionManager.getInstance().getUserInfo(appInstance.getUserId());
        appInstance.getStateHandler().fire(HgtpEvent.JOIN_ROOM, appInstance.getStateManager().getStateUnit(userInfo.getHgtpStateUnitId()));
        // create request join room
        HgtpJoinRoomRequest hgtpJoinRoomRequest = new HgtpJoinRoomRequest(
                appInstance.getUserId(), AppInstance.SEQ_INCREMENT, joinRoomId
        );

        HgtpRequestHandler hgtpRequestHandler = new HgtpRequestHandler();
        hgtpRequestHandler.sendJoinRoomRequest(hgtpJoinRoomRequest);
    }

    public void setRoomList(HashSet<String> roomList) {
        model.clear();

        if (roomList != null) {
            roomList.forEach( value -> model.addElement(value));
        }

    }
}
