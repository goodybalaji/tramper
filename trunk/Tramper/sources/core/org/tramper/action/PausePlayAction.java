package org.tramper.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.tramper.conductor.Conductor;
import org.tramper.ui.UserInterfaceFactory;

/**
 * @author Paul-Emile
 * 
 */
public class PausePlayAction extends AbstractAction {
    /** PausePlayAction.java long */
    private static final long serialVersionUID = -4581121566575367256L;
    /** singleton */
    private static PausePlayAction instance;
    
    /**
     * Returns the singleton
     * @return
     */
    public static PausePlayAction getInstance() {
	if (instance == null) {
	    instance = new PausePlayAction();
	}
	return instance;
    }
    
    /**
     * 
     */
    private PausePlayAction() {
	super();
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
	if (UserInterfaceFactory.isAudioUserInterfaceInstanciated()) {
	    Conductor aConductor = UserInterfaceFactory.getAudioUserInterface().getActiveRenderer();
            if (aConductor != null) {
                aConductor.pause();
            }
	}
    }
}
