package moomoo.hgtp.client.gui;

import moomoo.hgtp.client.gui.component.ClientFrame;
import moomoo.hgtp.client.service.AppInstance;

public class GuiManager {

    private static GuiManager guiManager = null;

    private ClientFrame clientFrame = null;

    public GuiManager() {
        clientFrame = new ClientFrame(AppInstance.getInstance().getUserId());
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
}
