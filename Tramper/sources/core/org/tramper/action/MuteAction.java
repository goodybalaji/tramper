package org.tramper.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.tramper.ui.UserInterfaceFactory;

/**
 * 
 * @author Paul-Emile
 */
public class MuteAction extends AbstractAction {
    /**
     * MuteAction.java long
     */
    private static final long serialVersionUID = 1L;

    /** singleton */
    private static MuteAction instance;
    
    /**
     * Returns the singleton
     * @return
     */
    public static MuteAction getInstance() {
	if (instance == null) {
	    instance = new MuteAction();
	}
	return instance;
    }
    
    /**
     * 
     */
    private MuteAction() {
	super();
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
	if (UserInterfaceFactory.isAudioUserInterfaceInstanciated()) {
	    UserInterfaceFactory.removeAudioUserInterface();
	} else {
	    UserInterfaceFactory.restoreAudioUserInterface();
	}
    }
}
