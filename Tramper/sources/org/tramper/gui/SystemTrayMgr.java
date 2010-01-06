package org.tramper.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import org.jdesktop.jdic.tray.SystemTray;
import org.jdesktop.jdic.tray.TrayIcon;
import org.tramper.action.QuitAction;
import org.tramper.action.ToggleAUIAction;
import org.tramper.action.ToggleGUIAction;
import org.tramper.ui.UserInterfaceFactory;

/**
 * Display an icon in the system tray (Windows, Mac OS X and Linux).
 * @author Paul-Emile
 */
public class SystemTrayMgr implements ActionListener, Runnable {
    /** tray icon */
    private TrayIcon trayIcon;
    
    /**
     * 
     */
    public SystemTrayMgr() {
	super();
	SwingUtilities.invokeLater(this);
    }

    public void run() {
	ResourceBundle label = ResourceBundle.getBundle("label");
	
        JPopupMenu menu = new JPopupMenu(label.getString("javaspeaker.productTitle"));
        Action toggleGUIAction = ToggleGUIAction.getInstance();
        toggleGUIAction.putValue(Action.NAME, label.getString("javaspeaker.toggleGUI"));
        JMenuItem menuItemToggleGUIAction = new JCheckBoxMenuItem(toggleGUIAction);
        menuItemToggleGUIAction.setSelected(true);
        menu.add(menuItemToggleGUIAction);
        Action toggleAUIAction = ToggleAUIAction.getInstance();
        toggleAUIAction.putValue(Action.NAME, label.getString("javaspeaker.toggleAUI"));
        JMenuItem menuItemToggleAUIAction = new JCheckBoxMenuItem(toggleAUIAction);
        menuItemToggleAUIAction.setSelected(true);
        menu.add(menuItemToggleAUIAction);
        Action quitAction = QuitAction.getInstance();
        quitAction.putValue(Action.NAME, label.getString("javaspeaker.menu.file.quit"));
        JMenuItem menuItemQuitAction = new JMenuItem(quitAction);
        menu.add(menuItemQuitAction);
        
        Icon icon = new ImageIcon(getClass().getResource("images/Tramper.png"));
        trayIcon = new TrayIcon(icon, label.getString("javaspeaker.productTitle"), menu);
        trayIcon.addActionListener(this);
        //trayIcon.addBalloonActionListener(this);
        SystemTray tray = SystemTray.getDefaultSystemTray();
        tray.addTrayIcon(trayIcon);

        String readyMsg = label.getString("javaspeaker.message.ready");
        this.displayInfo(readyMsg);
    }
    
    /**
     * 
     * @param message
     */
    public void displayInfo(String message) {
	ResourceBundle label = ResourceBundle.getBundle("label");
        trayIcon.displayMessage(label.getString("javaspeaker.productTitle"), message, TrayIcon.INFO_MESSAGE_TYPE);
    }
    
    /**
     * When a click upon the tray icon, open the GUI if not already done 
     * and put it in front of all the windows.
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
	if (UserInterfaceFactory.isGraphicalUserInterfaceInstanciated()) {
	    GraphicalUserInterface gui = UserInterfaceFactory.getGraphicalUserInterface();
	    gui.toFront();
	}
    }
}
