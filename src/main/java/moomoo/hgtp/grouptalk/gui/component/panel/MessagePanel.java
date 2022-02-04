package moomoo.hgtp.grouptalk.gui.component.panel;

import moomoo.hgtp.grouptalk.gui.listener.SendButtonListener;

import javax.swing.*;
import java.awt.*;

/**
 * @class MessagePanel
 * @brief client 모드시 실행되는 GUI 내 http message request 의 content 정보를 입력 및 전송하는 panel
 */
public class MessagePanel extends JPanel {

    private final JTextField sendText = new JTextField();
    private final JButton sendButton = new JButton("SEND");

    public MessagePanel() {
        this.setLayout(new GridBagLayout());

        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx=0.8;
        gridBagConstraints.gridy=0;
        gridBagConstraints.gridx=0;
        this.add(sendText, gridBagConstraints);

        gridBagConstraints.fill = GridBagConstraints.BOTH;

        gridBagConstraints.weightx=0.2;
        gridBagConstraints.gridx=1;
        this.add(sendButton);

        sendText.addActionListener(new SendButtonListener());
        sendButton.addActionListener(new SendButtonListener());

        sendText.setEnabled(false);
        sendButton.setEnabled(false);
    }

    public String getSendText() {
        return sendText.getText();
    }

    public void initSendText() {
        sendText.setText("");
    }

    public void setEnableSendButton(boolean enable) {
        sendText.setEnabled(enable);
        sendButton.setEnabled(enable);
    }

}
