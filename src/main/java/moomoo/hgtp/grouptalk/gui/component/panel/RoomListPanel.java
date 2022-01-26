package moomoo.hgtp.grouptalk.gui.component.panel;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;

public class RoomListPanel extends JPanel {

    private final JList<String> playlistView = new JList<>();
    private final DefaultListModel<String> model = new DefaultListModel<>();

    public RoomListPanel() {
        BorderLayout borderLayout = new BorderLayout();
        borderLayout.setVgap(3);
        borderLayout.setHgap(3);
        setLayout(borderLayout);

        JLabel roomlistName = new JLabel("ROOM LIST");
        roomlistName.setHorizontalAlignment(JLabel.CENTER);
        roomlistName.setPreferredSize(new Dimension(this.getWidth(), 20));
        this.add(roomlistName, BorderLayout.NORTH);

        initRoomListView();

    }


    private void initRoomListView() {
        playlistView.setModel(model);
        playlistView.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        playlistView.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    int index = playlistView.locationToIndex(e.getPoint());
                    playlistView.setSelectedIndex(index);
                    if (index >= 0 && index < model.size()) {
                        String roomId = model.get(index);
                        // todo 입장 처리
                    }
                }
            }
        });

        this.add(new JScrollPane(playlistView), BorderLayout.CENTER);
    }

    public void setRoomList(String[] roomList) {
        model.clear();

        if (roomList != null) {
            for (int index = 0; index < roomList.length; index++) {
                model.addElement(roomList[index]);
            }
        }

    }
}
