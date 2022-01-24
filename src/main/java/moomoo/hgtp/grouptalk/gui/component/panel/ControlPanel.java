package moomoo.hgtp.grouptalk.gui.component.panel;

import moomoo.hgtp.grouptalk.gui.listener.*;

import javax.swing.*;
import java.awt.*;

public class ControlPanel extends JPanel {

    private JButton registerButton = new JButton("REGISTER");
    private JButton unregisterButton = new JButton("UNREGISTER");
    private JButton createRoomButton = new JButton("CREATE ROOM");
    private JButton deleteRoomButton = new JButton("DELETE ROOM");
    private JButton joinRoomButton = new JButton("JOIN ROOM");
    private JButton exitRoomButton = new JButton("EXIT ROOM");
    private JButton inviteUserFromRoomButton = new JButton("INVITE USER FROM ROOM");
    private JButton removeUserFromRoomButton = new JButton("REMOVE USER FROM ROOM");

    public ControlPanel() {
        GridLayout gridLayout = new GridLayout(4, 2);
        gridLayout.setVgap(10);
        gridLayout.setHgap(5);
        setLayout(gridLayout);

        add(registerButton);
        add(unregisterButton);
        add(createRoomButton);
        add(deleteRoomButton);
        add(joinRoomButton);
        add(exitRoomButton);
        add(inviteUserFromRoomButton);
        add(removeUserFromRoomButton);


        registerButton.addActionListener(new RegisterButtonListener());
        unregisterButton.addActionListener(new UnregisterButtonListener());
        createRoomButton.addActionListener(new CreateRoomButtonListener());
        deleteRoomButton.addActionListener(new DeleteRoomButtonListener());
        joinRoomButton.addActionListener(new JoinRoomButtonListener());
        exitRoomButton.addActionListener(new ExitRoomButtonListener());
        inviteUserFromRoomButton.addActionListener(new InviteUserFromRoomButtonListener());
        removeUserFromRoomButton.addActionListener(new RemoveUserFromRoomButtonListener());

        setInitButtonStatus();
    }

    public void setInitButtonStatus(){
        registerButton.setEnabled(true);
        unregisterButton.setEnabled(false);
        createRoomButton.setEnabled(false);
        deleteRoomButton.setEnabled(false);
        joinRoomButton.setEnabled(false);
        exitRoomButton.setEnabled(false);
        inviteUserFromRoomButton.setEnabled(false);
        removeUserFromRoomButton.setEnabled(false);
    }

    public void setRegisterButtonStatus(){
        registerButton.setEnabled(false);
        unregisterButton.setEnabled(true);
        createRoomButton.setEnabled(true);
        deleteRoomButton.setEnabled(false);
        joinRoomButton.setEnabled(true);
        exitRoomButton.setEnabled(false);
        inviteUserFromRoomButton.setEnabled(false);
        removeUserFromRoomButton.setEnabled(false);
    }

    public void setCreateRoomButtonStatus(){
        registerButton.setEnabled(false);
        unregisterButton.setEnabled(false);
        createRoomButton.setEnabled(false);
        deleteRoomButton.setEnabled(true);
        joinRoomButton.setEnabled(false);
        exitRoomButton.setEnabled(true);
        inviteUserFromRoomButton.setEnabled(true);
        removeUserFromRoomButton.setEnabled(true);
    }

    public void setDeleteRoomButtonStatus(){
        registerButton.setEnabled(false);
        unregisterButton.setEnabled(true);
        createRoomButton.setEnabled(true);
        deleteRoomButton.setEnabled(false);
        joinRoomButton.setEnabled(true);
        exitRoomButton.setEnabled(false);
        inviteUserFromRoomButton.setEnabled(false);
        removeUserFromRoomButton.setEnabled(false);
    }

    public void setJoinRoomButtonStatus(){
        registerButton.setEnabled(false);
        unregisterButton.setEnabled(false);
        createRoomButton.setEnabled(false);
        deleteRoomButton.setEnabled(false);
        joinRoomButton.setEnabled(false);
        exitRoomButton.setEnabled(true);
        inviteUserFromRoomButton.setEnabled(false);
        removeUserFromRoomButton.setEnabled(false);
    }

}
