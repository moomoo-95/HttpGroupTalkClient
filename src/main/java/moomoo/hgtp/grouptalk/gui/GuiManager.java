package moomoo.hgtp.grouptalk.gui;

import moomoo.hgtp.grouptalk.gui.component.ClientFrame;
import moomoo.hgtp.grouptalk.gui.component.panel.ControlPanel;
import moomoo.hgtp.grouptalk.gui.component.panel.RoomListPanel;
import moomoo.hgtp.grouptalk.gui.component.panel.RoomPanel;
import moomoo.hgtp.grouptalk.gui.component.panel.UserListPanel;
import moomoo.hgtp.grouptalk.service.AppInstance;

public class GuiManager {

    private static GuiManager guiManager = null;

    private final ClientFrame clientFrame;

    public GuiManager() {
        clientFrame = new ClientFrame("GroupTalk (" +AppInstance.getInstance().getUserId()+ ")");
    }

    public static GuiManager getInstance() {
        if (guiManager == null) {
            guiManager = new GuiManager();
        }
        return guiManager;
    }

    public ClientFrame getClientFrame() {
        return clientFrame;
    }

    public RoomListPanel getRoomListPanel() { return clientFrame.getRoomListPanel(); }
    public UserListPanel getUserListPanel() { return clientFrame.getUserListPanel(); }

    public RoomPanel getRoomPanel() { return clientFrame.getRoomPanel(); }

    public ControlPanel getControlPanel() { return clientFrame.getControlPanel(); }

}
