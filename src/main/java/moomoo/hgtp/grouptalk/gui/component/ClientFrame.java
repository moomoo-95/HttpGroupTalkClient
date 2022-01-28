package moomoo.hgtp.grouptalk.gui.component;

import moomoo.hgtp.grouptalk.gui.component.panel.*;

import javax.swing.*;
import java.awt.*;

public class ClientFrame extends JFrame {

    private static final int WIDTH = 1200;
    private static final int HEIGHT = 600;

    private final ListPanel listPanel = new ListPanel();
    private final RoomPanel roomPanel = new RoomPanel();
    private final ControlPanel controlPanel = new ControlPanel();


    public ClientFrame(String title) {
        super(title);

        // 프레임 크기
        setSize(WIDTH, HEIGHT);
        // 화면 가운데 배치
        setLocationRelativeTo(null);
        // 닫을 때 메모리에서 제거되도록 설정
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        // layout 설정
        BorderLayout borderLayout = new BorderLayout();
        setLayout(borderLayout);

        add(listPanel, BorderLayout.WEST);
        add(roomPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.EAST);

        // 보이게 설정
        setVisible(true);
    }

    public void clientFrameInit() {
        getUserListPanel().setUserList(null);
        getRoomListPanel().setRoomList(null);
        getRoomUserListPanel().setRoomUserList(null);
        getControlPanel().setInitButtonStatus();

    }


    public UserListPanel getUserListPanel() {return listPanel.getUserListPanel(); }
    public RoomListPanel getRoomListPanel() {return listPanel.getRoomListPanel(); }
    public RoomUserListPanel getRoomUserListPanel() {return listPanel.getRoomUserListPanel(); }

    public RoomPanel getRoomPanel() { return roomPanel; }

    public ControlPanel getControlPanel() {return controlPanel;}
}
