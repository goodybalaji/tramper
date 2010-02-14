package org.tramper.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;

import org.tramper.ui.UserInterfaceFactory;

/**
 * @author Paul-Emile
 * 
 */
public class ToggleGUIAction extends AbstractAction {
    /** ToggleGUIAction.java long */
    private static final long serialVersionUID = 3574224374580586067L;
    /** singleton */
    private static ToggleGUIAction instance;
    
    /**
     * 
     * @return
     */
    public static ToggleGUIAction getInstance() {
	if (instance == null){
	    instance = new ToggleGUIAction();
	}
	return instance;
    }

    /**
     * 
     */
    private ToggleGUIAction() {
	super();
	putValue(Action.ACTION_COMMAND_KEY, "toggleGUI");
    }
    
    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
	Object source = e.getSource();
	boolean checked = false;
	if (source instanceof JCheckBoxMenuItem) {
	    checked = ((JCheckBoxMenuItem)source).isSelected();
	}
	
	if (checked && !UserInterfaceFactory.isGraphicalUserInterfaceInstanciated()) {
	    UserInterfaceFactory.getGraphicalUserInterface();
	} else if (!checked && UserInterfaceFactory.isGraphicalUserInterfaceInstanciated()) {
	    UserInterfaceFactory.removeGraphicalUserInterface();
	}
    }
}
