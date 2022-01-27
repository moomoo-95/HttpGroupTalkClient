package moomoo.hgtp.grouptalk.gui;

import moomoo.hgtp.grouptalk.gui.component.ClientFrame;
import moomoo.hgtp.grouptalk.gui.component.panel.*;
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

    public UserListPanel getUserListPanel() { return clientFrame.getUserListPanel(); }
    public RoomListPanel getRoomListPanel() { return clientFrame.getRoomListPanel(); }
    public RoomUserListPanel getRoomUSerListPanel() { return clientFrame.getRoomUserListPanel(); }

    public RoomPanel getRoomPanel() { return clientFrame.getRoomPanel(); }

    public ControlPanel getControlPanel() { return clientFrame.getControlPanel(); }

}
