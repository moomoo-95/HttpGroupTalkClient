package moomoo.hgtp.grouptalk.gui;

import moomoo.hgtp.grouptalk.gui.component.ClientFrame;
import moomoo.hgtp.grouptalk.gui.component.panel.*;
import moomoo.hgtp.grouptalk.service.AppInstance;

/**
 * @class GuiManager
 * @brief client 모드시 실행되는 GUI를 관리하는 class
 */
public class GuiManager {

    private static GuiManager guiManager = null;

    private final ClientFrame clientFrame;

    public GuiManager() {
        clientFrame = new ClientFrame("GroupTalk" + "["+AppInstance.getInstance().getUserId()+"]");
    }

    public static GuiManager getInstance() {
        if (guiManager == null) {
            guiManager = new GuiManager();
        }
        return guiManager;
    }

    public void setClientTitle(String title) {
        if (title == null) return;
        clientFrame.setTitle("GroupTalk [" + title + "]");
    }

    public void clientFrameInit() {
        clientFrame.setTitle("GroupTalk");
        getUserListPanel().setUserList(null);
        getRoomListPanel().setRoomList(null);
        getRoomUserListPanel().setRoomUserList(null);
        getControlPanel().setInitButtonStatus();
    }

    public void roomInit() {
        getRoomPanel().setRoomName("");
        getRoomPanel().initMessage();
        getRoomUserListPanel().setRoomUserList(null);
    }

    public UserListPanel getUserListPanel() { return clientFrame.getUserListPanel(); }
    public RoomListPanel getRoomListPanel() { return clientFrame.getRoomListPanel(); }
    public RoomUserListPanel getRoomUserListPanel() { return clientFrame.getRoomUserListPanel(); }

    public RoomPanel getRoomPanel() { return clientFrame.getRoomPanel(); }
    public MessagePanel getMessagePanel() { return clientFrame.getMessagePanel(); }

    public ControlPanel getControlPanel() { return clientFrame.getControlPanel(); }


}
