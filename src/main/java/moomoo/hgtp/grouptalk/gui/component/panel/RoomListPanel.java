package moomoo.hgtp.grouptalk.gui.component.panel;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.HashSet;

public class RoomListPanel extends JPanel {

    private final JList<String> roomListView = new JList<>();
    private final DefaultListModel<String> model = new DefaultListModel<>();

    private String focusRoomId = "";

    public RoomListPanel() {
        BorderLayout borderLayout = new BorderLayout();
        borderLayout.setVgap(3);
        borderLayout.setHgap(3);
        setLayout(borderLayout);

        JLabel roomListName = new JLabel("ROOM LIST");
        roomListName.setHorizontalAlignment(JLabel.CENTER);
        roomListName.setPreferredSize(new Dimension(this.getWidth(), 20));
        this.add(roomListName, BorderLayout.NORTH);

        initRoomListView();

    }


    private void initRoomListView() {
        roomListView.setModel(model);
        roomListView.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        roomListView.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    int index = roomListView.locationToIndex(e.getPoint());
                    roomListView.setSelectedIndex(index);
                    if (index >= 0 && index < model.size()) {
                        focusRoomId = model.get(index);
                    }
                }
            }



        });

        this.add(new JScrollPane(roomListView), BorderLayout.CENTER);
    }

    public void setRoomList(HashSet<String> roomList) {
        model.clear();

        if (roomList != null) {
            roomList.forEach( value -> model.addElement(value));
        }

    }

    public String getFocusRoomId() { return focusRoomId; }
}
