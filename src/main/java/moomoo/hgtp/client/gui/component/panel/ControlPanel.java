package moomoo.hgtp.client.gui.component.panel;

import moomoo.hgtp.client.gui.listener.CreateRoomButtonListener;
import moomoo.hgtp.client.gui.listener.RegisterButtonListener;

import javax.swing.*;
import java.awt.*;

public class ControlPanel extends JPanel {

    private JButton registerButton = new JButton("REGISTER");
    private JButton createRoomButton = new JButton("CREATE ROOM");

    public ControlPanel() {
        GridLayout gridLayout = new GridLayout(4, 2);
        gridLayout.setVgap(10);
        gridLayout.setHgap(5);
        setLayout(gridLayout);

        add(registerButton);
        add(createRoomButton);

        registerButton.addActionListener(new RegisterButtonListener());
        createRoomButton.addActionListener(new CreateRoomButtonListener());
    }
}
