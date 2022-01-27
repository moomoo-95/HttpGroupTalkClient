package moomoo.hgtp.grouptalk.gui.component.panel;

import javax.swing.*;
import java.awt.*;

public class ListPanel extends JPanel {

    private final RoomListPanel roomListPanel = new RoomListPanel();
    private final UserListPanel userListPanel = new UserListPanel();

    public ListPanel() {
        GridLayout gridLayout = new GridLayout(2, 1);
        gridLayout.setVgap(10);
        gridLayout.setHgap(5);
        setLayout(gridLayout);

        this.add(roomListPanel);
        this.add(userListPanel);
    }

    public RoomListPanel getRoomListPanel() { return roomListPanel; }

    public UserListPanel getUserListPanel() { return userListPanel; }
}
