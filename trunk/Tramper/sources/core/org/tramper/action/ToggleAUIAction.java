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
public class ToggleAUIAction extends AbstractAction {
    /** ToggleAUIAction.java long */
    private static final long serialVersionUID = 6988555838960837910L;
    /** singleton */
    private static ToggleAUIAction instance;
    
    /**
     * 
     * @return
     */
    public static ToggleAUIAction getInstance() {
	if (instance == null){
	    instance = new ToggleAUIAction();
	}
	return instance;
    }

    /**
     * 
     */
    private ToggleAUIAction() {
	super();
	putValue(Action.ACTION_COMMAND_KEY, "toggleAUI");
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
	
	if (checked && !UserInterfaceFactory.isAudioUserInterfaceInstanciated()) {
	    UserInterfaceFactory.getAudioUserInterface();
	} else if (!checked && UserInterfaceFactory.isAudioUserInterfaceInstanciated()) {
	    UserInterfaceFactory.removeAudioUserInterface();
	}
    }
}
