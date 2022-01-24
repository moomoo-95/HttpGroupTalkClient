package moomoo.hgtp.grouptalk.gui;

import moomoo.hgtp.grouptalk.gui.component.ClientFrame;
import moomoo.hgtp.grouptalk.gui.component.panel.ControlPanel;
import moomoo.hgtp.grouptalk.service.AppInstance;

public class GuiManager {

    private static GuiManager guiManager = null;

    private final ClientFrame clientFrame;

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

    public ControlPanel getControlPanel() { return clientFrame.getControlPanel(); }
}
