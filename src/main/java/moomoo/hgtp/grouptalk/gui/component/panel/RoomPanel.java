package moomoo.hgtp.grouptalk.gui.component.panel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

/**
 * @class RoomPanel
 * @brief room 내 메시지 및 공지를 출력하는 panel
 */
public class RoomPanel extends JPanel {

    private static final Logger log = LoggerFactory.getLogger(RoomPanel.class);
    private static final String BLACK = "black";
    private static final String BLUE = "blue";
    private static final String DARK_GRAY = "dark_gray";
    private static final String LINE = "--------------------------------------------\n";


    private final JLabel roomName = new JLabel("");
    private final JTextPane textPane = new JTextPane();
    private final StyledDocument document = textPane.getStyledDocument();
    private final MessagePanel messagePanel = new MessagePanel();

    public RoomPanel() {
        BorderLayout borderLayout = new BorderLayout();
        borderLayout.setVgap(5);
        borderLayout.setHgap(3);
        setLayout(borderLayout);

        roomName.setHorizontalAlignment(SwingConstants.CENTER);
        roomName.setPreferredSize(new Dimension(this.getWidth(), 20));

        initJTextPane();

        this.add(roomName, BorderLayout.NORTH);
        this.add(new JScrollPane(textPane), BorderLayout.CENTER);
        this.add(messagePanel, BorderLayout.SOUTH);

    }

    private void initJTextPane() {
        Style def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
        Style style = document.addStyle(BLACK, def);
        StyleConstants.setForeground(style, Color.BLACK);
        style = document.addStyle(BLUE, def);
        StyleConstants.setForeground(style, Color.BLUE);
        style = document.addStyle(DARK_GRAY, def);
        StyleConstants.setForeground(style, Color.DARK_GRAY);

        textPane.setEditable(false);
    }

    public void setRoomName(String roomName) {
        this.roomName.setText(roomName);
    }

    public void initMessage() {
        try {
            document.remove(0, document.getLength());
        } catch (BadLocationException e) {
            log.error("RoomPanel.initMessage ", e);
        }
    }

    public void addMessage(String message, boolean isMyMessage) {
        try {
            if (isMyMessage) {
                document.insertString(document.getLength(), message, document.getStyle(BLUE));
            } else {
                document.insertString(document.getLength(), message, document.getStyle(BLACK));
            }
            document.insertString(document.getLength(), LINE, document.getStyle(DARK_GRAY));
        } catch (BadLocationException e) {
            log.error("RoomPanel.addMessage ", e);
        }
    }

    public void addNotice(String notice) {
        try {
            document.insertString(document.getLength(), notice, document.getStyle(DARK_GRAY));
            document.insertString(document.getLength(), LINE, document.getStyle(DARK_GRAY));
        } catch (BadLocationException e) {
            log.error("RoomPanel.addNotice ", e);
        }
    }

    public MessagePanel getMessagePanel() { return messagePanel; }
}
