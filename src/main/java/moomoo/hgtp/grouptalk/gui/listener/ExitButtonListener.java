package moomoo.hgtp.grouptalk.gui.listener;


import moomoo.hgtp.grouptalk.service.ServiceManager;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ExitButtonListener implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        ServiceManager.getInstance().stop();
        System.exit(1);
    }
}
