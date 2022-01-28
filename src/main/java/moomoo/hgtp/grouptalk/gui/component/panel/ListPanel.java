package moomoo.hgtp.grouptalk.gui.component.panel;

import javax.swing.*;
import java.awt.*;

public class ListPanel extends JPanel {

    private final UserListPanel userListPanel = new UserListPanel();
    private final RoomListPanel roomListPanel = new RoomListPanel();
    private final RoomUserListPanel roomUserListPanel = new RoomUserListPanel();

    public ListPanel(Dimension dimension) {
        GridLayout gridLayout = new GridLayout(3, 1);
        gridLayout.setVgap(10);
        gridLayout.setHgap(5);
        setLayout(gridLayout);

        setPreferredSize(dimension);

        this.add(userListPanel);
        this.add(roomListPanel);
        this.add(roomUserListPanel);

    }

    public UserListPanel getUserListPanel() { return userListPanel; }

    public RoomListPanel getRoomListPanel() { return roomListPanel; }

    public RoomUserListPanel getRoomUserListPanel() {return roomUserListPanel;}
}
