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

    public void clientFrameInit() {
        getUserListPanel().setUserList(null);
        getRoomListPanel().setRoomList(null);
        getRoomUserListPanel().setRoomUserList(null);
        getControlPanel().setInitButtonStatus();
    }

    public void roomInit() {
        getRoomPanel().initMessage();
        getRoomUserListPanel().setRoomUserList(null);
    }

    public ClientFrame getClientFrame() { return clientFrame;}

    public UserListPanel getUserListPanel() { return clientFrame.getUserListPanel(); }
    public RoomListPanel getRoomListPanel() { return clientFrame.getRoomListPanel(); }
    public RoomUserListPanel getRoomUserListPanel() { return clientFrame.getRoomUserListPanel(); }

    public RoomPanel getRoomPanel() { return clientFrame.getRoomPanel(); }
    public MessagePanel getMessagePanel() { return clientFrame.getMessagePanel(); }

    public ControlPanel getControlPanel() { return clientFrame.getControlPanel(); }


}
