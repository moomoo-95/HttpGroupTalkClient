package moomoo.hgtp.client.gui.component.panel;

import moomoo.hgtp.client.gui.listener.*;
import moomoo.hgtp.client.service.AppInstance;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.HashMap;

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

    }


    private void initPlaylistView() {
        playlistView.setModel(model);
        playlistView.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        playlistView.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    int index = playlistView.locationToIndex(e.getPoint());
                    playlistView.setSelectedIndex(index);
                    String roomId = model.get(index);
                    // todo 입장 처리
                }
            }
        });

        this.add(new JScrollPane(playlistView), BorderLayout.CENTER);
    }
}
