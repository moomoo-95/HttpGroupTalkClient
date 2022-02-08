package moomoo.hgtp.grouptalk.gui.component.panel;

import moomoo.hgtp.grouptalk.gui.GuiManager;
import moomoo.hgtp.grouptalk.gui.listener.*;

import javax.swing.*;
import java.awt.*;

/**
 * @class ControlPanel
 * @brief client 모드시 실행되는 GUI 내 hgtp, http 메시지를 전송하기 위한 button control panel
 */
public class ControlPanel extends JPanel {

    private final JButton registerButton = new JButton("REGISTER");
    private final JButton createRoomButton = new JButton("CREATE ROOM");
    private final JButton exitRoomButton = new JButton("EXIT ROOM");
    private final JButton deleteRoomButton = new JButton("DELETE ROOM");
    private final JButton unregisterButton = new JButton("UNREGISTER");
    private final JButton exitButton = new JButton("EXIT");
    private final JButton refreshButton = new JButton("REFRESH");

    public ControlPanel(Dimension dimension) {
        GridLayout gridLayout = new GridLayout(7, 1);
        gridLayout.setVgap(3);
        gridLayout.setHgap(1);
        setLayout(gridLayout);

        setPreferredSize(dimension);

        add(registerButton);
        add(createRoomButton);
        add(exitRoomButton);
        add(deleteRoomButton);
        add(unregisterButton);
        add(refreshButton);
        add(exitButton);

        registerButton.addActionListener(new RegisterButtonListener());
        unregisterButton.addActionListener(new UnregisterButtonListener());
        createRoomButton.addActionListener(new CreateRoomButtonListener());
        deleteRoomButton.addActionListener(new DeleteRoomButtonListener());
        exitRoomButton.addActionListener(new ExitRoomButtonListener());
        refreshButton.addActionListener(new RefreshButtonListener());
        exitButton.addActionListener(new ExitButtonListener());

        registerButton.setEnabled(true);
        unregisterButton.setEnabled(false);
        createRoomButton.setEnabled(false);
        deleteRoomButton.setEnabled(false);
        exitRoomButton.setEnabled(false);
        refreshButton.setEnabled(false);
        exitButton.setEnabled(true);
    }

    public void setInitButtonStatus(){
        registerButton.setEnabled(true);
        unregisterButton.setEnabled(false);
        createRoomButton.setEnabled(false);
        deleteRoomButton.setEnabled(false);
        exitRoomButton.setEnabled(false);
        refreshButton.setEnabled(false);
        exitButton.setEnabled(true);

        GuiManager.getInstance().getMessagePanel().setEnableSendButton(false);
    }

    public void setRegisterButtonStatus(){
        registerButton.setEnabled(false);
        unregisterButton.setEnabled(true);
        createRoomButton.setEnabled(true);
        deleteRoomButton.setEnabled(false);
        exitRoomButton.setEnabled(false);
        refreshButton.setEnabled(true);
        exitButton.setEnabled(false);

        GuiManager.getInstance().getMessagePanel().setEnableSendButton(false);
    }

    public void setCreateRoomButtonStatus(){
        registerButton.setEnabled(false);
        unregisterButton.setEnabled(false);
        createRoomButton.setEnabled(false);
        deleteRoomButton.setEnabled(true);
        exitRoomButton.setEnabled(false);
        refreshButton.setEnabled(true);
        exitButton.setEnabled(false);

        GuiManager.getInstance().getMessagePanel().setEnableSendButton(true);
    }

    public void setJoinRoomButtonStatus(){
        registerButton.setEnabled(false);
        unregisterButton.setEnabled(false);
        createRoomButton.setEnabled(false);
        deleteRoomButton.setEnabled(false);
        exitRoomButton.setEnabled(true);
        exitButton.setEnabled(false);
        refreshButton.setEnabled(true);
        exitButton.setEnabled(false);

        GuiManager.getInstance().getMessagePanel().setEnableSendButton(true);
    }
}
