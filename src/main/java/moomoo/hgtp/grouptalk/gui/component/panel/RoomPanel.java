package moomoo.hgtp.grouptalk.gui.component.panel;

import javax.swing.*;
import java.awt.*;

public class RoomPanel extends JPanel {

    private final JLabel roomName = new JLabel("-");
    private final JTextField jTextField = new JTextField();

    public RoomPanel() {
        BorderLayout borderLayout = new BorderLayout();
        borderLayout.setVgap(3);
        borderLayout.setHgap(3);
        setLayout(borderLayout);

        roomName.setHorizontalAlignment(JLabel.CENTER);
        roomName.setPreferredSize(new Dimension(this.getWidth(), 20));
        this.add(roomName, BorderLayout.NORTH);
        this.add(jTextField, BorderLayout.CENTER);

    }

    public void setRoomName(String roomName) {
        this.roomName.setText(roomName);
    }

}
