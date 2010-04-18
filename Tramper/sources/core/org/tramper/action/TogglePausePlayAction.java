package org.tramper.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.tramper.conductor.Conductor;
import org.tramper.ui.UserInterfaceFactory;

/**
 * @author Paul-Emile
 * 
 */
public class TogglePausePlayAction extends AbstractAction {
    /** TogglePausePlayAction.java long */
    private static final long serialVersionUID = -1842936395710642626L;
    /** singleton */
    private static TogglePausePlayAction instance;
    
    /**
     * Returns the singleton
     * @return
     */
    public static TogglePausePlayAction getInstance() {
	if (instance == null) {
	    instance = new TogglePausePlayAction();
	}
	return instance;
    }
    
    /**
     * 
     */
    private TogglePausePlayAction() {
	super();
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
	if (UserInterfaceFactory.isAudioUserInterfaceInstanciated()) {
	    Conductor aConductor = UserInterfaceFactory.getAudioUserInterface().getActiveRenderer();
            if (aConductor != null) {
                if (aConductor.isPaused()) {
                    aConductor.resume();
                } else {
                    aConductor.pause();
                }
            }
	}
    }
}
