package moomoo.hgtp.grouptalk.gui.component;

import moomoo.hgtp.grouptalk.gui.component.panel.*;

import javax.swing.*;
import java.awt.*;

public class ClientFrame extends JFrame {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 400;

    private final ListPanel listPanel;
    private final RoomPanel roomPanel = new RoomPanel();
    private final ControlPanel controlPanel;


    public ClientFrame(String title) {
        super(title);

        // 프레임 크기
        setPreferredSize( new Dimension(WIDTH, HEIGHT) );
        setMinimumSize( new Dimension(WIDTH, HEIGHT) );
        // 화면 가운데 배치
        setLocationRelativeTo(null);
        // 닫을 때 메모리에서 제거되도록 설정
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        // layout 설정
        BorderLayout borderLayout = new BorderLayout();
        borderLayout.setVgap(5);
        borderLayout.setHgap(3);
        setLayout(borderLayout);

        listPanel = new ListPanel(new Dimension(WIDTH / 4, HEIGHT) );
        controlPanel = new ControlPanel(new Dimension(WIDTH / 5, HEIGHT));

        add(listPanel, BorderLayout.WEST);
        add(roomPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.EAST);

        // 보이게 설정
        setVisible(true);
    }
    
    public UserListPanel getUserListPanel() {return listPanel.getUserListPanel(); }
    public RoomListPanel getRoomListPanel() {return listPanel.getRoomListPanel(); }
    public RoomUserListPanel getRoomUserListPanel() {return listPanel.getRoomUserListPanel(); }

    public RoomPanel getRoomPanel() { return roomPanel; }
    public MessagePanel getMessagePanel() { return roomPanel.getMessagePanel(); }

    public ControlPanel getControlPanel() {return controlPanel;}
}
