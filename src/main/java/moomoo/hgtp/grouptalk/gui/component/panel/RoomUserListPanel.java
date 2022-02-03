package moomoo.hgtp.grouptalk.gui.component.panel;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.HashSet;

public class RoomUserListPanel extends JPanel {

    private final JList<String> roomUserListView = new JList<>();
    private final DefaultListModel<String> model = new DefaultListModel<>();

    private String focusRoomUserId = "";

    public RoomUserListPanel() {
        BorderLayout borderLayout = new BorderLayout();
        borderLayout.setVgap(3);
        borderLayout.setHgap(3);
        setLayout(borderLayout);

        JLabel roomUserListName = new JLabel("ROOM USER LIST");
        roomUserListName.setHorizontalAlignment(JLabel.CENTER);
        roomUserListName.setPreferredSize(new Dimension(this.getWidth(), 20));
        this.add(roomUserListName, BorderLayout.NORTH);

        initUserListView();
    }


    private void initUserListView() {
        roomUserListView.setModel(model);
        roomUserListView.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        roomUserListView.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    int index = roomUserListView.locationToIndex(e.getPoint());
                    roomUserListView.setSelectedIndex(index);
                    if (index >= 0 && index < model.size()) {
                        focusRoomUserId = model.get(index);
                    }
                }
            }
        });

        this.add(new JScrollPane(roomUserListView), BorderLayout.CENTER);
    }

    public void setRoomUserList(HashSet<String> groupUserList) {
        model.clear();

        if (groupUserList != null) {
            groupUserList.forEach( value -> model.addElement(value));
        }
    }

    public String getFocusRoomUserId() { return focusRoomUserId; }
}
