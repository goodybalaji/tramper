package org.tramper.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ResourceBundle;

import javax.swing.Action;
import javax.swing.SwingUtilities;

import java.awt.AWTException;
import java.awt.CheckboxMenuItem;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import org.tramper.action.QuitAction;
import org.tramper.ui.UserInterfaceFactory;

/**
 * Display an icon in the system tray (Windows, Mac OS X and Linux).
 * @author Paul-Emile
 */
public class SystemTrayMgr implements ActionListener, ItemListener, Runnable {
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
	if (!SystemTray.isSupported()) {
	    return;
	}
	ResourceBundle label = ResourceBundle.getBundle("label");
	
        PopupMenu menu = new PopupMenu(label.getString("javaspeaker.productTitle"));
        CheckboxMenuItem menuItemToggleGUIAction = new CheckboxMenuItem(label.getString("disappear"));
        menuItemToggleGUIAction.setName("disappear");
        menuItemToggleGUIAction.addItemListener(this);
        menu.add(menuItemToggleGUIAction);
        CheckboxMenuItem menuItemToggleAUIAction = new CheckboxMenuItem(label.getString("silence"));
        menuItemToggleAUIAction.setName("silence");
        menuItemToggleAUIAction.addItemListener(this);
        menu.add(menuItemToggleAUIAction);
        Action quitAction = QuitAction.getInstance();
        MenuItem menuItemQuitAction = new MenuItem(label.getString("javaspeaker.menu.file.quit"));
        menuItemQuitAction.addActionListener(quitAction);
        menu.add(menuItemQuitAction);
        
        Image img = Toolkit.getDefaultToolkit().getImage(getClass().getResource("images/Tramper.png"));
        trayIcon = new TrayIcon(img, label.getString("javaspeaker.productTitle"), menu);
        trayIcon.addActionListener(this);
        SystemTray tray = SystemTray.getSystemTray();
        try {
	    tray.add(trayIcon);
	} catch (AWTException e) {}

        String readyMsg = label.getString("javaspeaker.message.ready");
        this.displayInfo(readyMsg);
    }
    
    /**
     * 
     * @param message
     */
    public void displayInfo(String message) {
	ResourceBundle label = ResourceBundle.getBundle("label");
        trayIcon.displayMessage(label.getString("javaspeaker.productTitle"), message, TrayIcon.MessageType.INFO);
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

    public void itemStateChanged(ItemEvent e) {
	int state = e.getStateChange();
	CheckboxMenuItem item = (CheckboxMenuItem)e.getSource();
	String name = item.getName();
	if (name.equals("silence")) {
	    if (state == ItemEvent.SELECTED && UserInterfaceFactory.isAudioUserInterfaceInstanciated()) {
		UserInterfaceFactory.removeAudioUserInterface();
	    } else if (state == ItemEvent.DESELECTED && !UserInterfaceFactory.isAudioUserInterfaceInstanciated()) {
		UserInterfaceFactory.getAudioUserInterface();
	    }
	} else if (name.equals("disappear")) {
	    if (state == ItemEvent.SELECTED && UserInterfaceFactory.isGraphicalUserInterfaceInstanciated()) {
		UserInterfaceFactory.removeGraphicalUserInterface();
	    } else if (state == ItemEvent.DESELECTED && !UserInterfaceFactory.isGraphicalUserInterfaceInstanciated()) {
		UserInterfaceFactory.getGraphicalUserInterface();
	    }
	}
    }
}
