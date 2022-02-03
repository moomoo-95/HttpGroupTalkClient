package moomoo.hgtp.grouptalk.gui.component.panel;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.HashSet;

public class UserListPanel extends JPanel {

    private final JList<String> userListView = new JList<>();
    private final DefaultListModel<String> model = new DefaultListModel<>();

    private String focusUserId = "";

    public UserListPanel() {
        BorderLayout borderLayout = new BorderLayout();
        borderLayout.setVgap(3);
        borderLayout.setHgap(3);
        setLayout(borderLayout);

        JLabel userListName = new JLabel("USER LIST");
        userListName.setHorizontalAlignment(JLabel.CENTER);
        userListName.setPreferredSize(new Dimension(this.getWidth(), 20));
        this.add(userListName, BorderLayout.NORTH);

        initUserListView();

    }


    private void initUserListView() {
        userListView.setModel(model);
        userListView.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userListView.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    int index = userListView.locationToIndex(e.getPoint());
                    userListView.setSelectedIndex(index);
                    if (index >= 0 && index < model.size()) {
                        focusUserId = model.get(index);
                    }
                }
            }
        });

        this.add(new JScrollPane(userListView), BorderLayout.CENTER);
    }

    public void setUserList(HashSet<String> userList) {
        model.clear();

        if (userList != null) {
            userList.forEach( value -> model.addElement(value));
        }

    }

    public String getFocusUserId() { return focusUserId; }
}
