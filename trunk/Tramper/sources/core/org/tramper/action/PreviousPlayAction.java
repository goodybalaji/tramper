package org.tramper.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.tramper.conductor.Conductor;
import org.tramper.ui.UserInterfaceFactory;

/**
 * @author Paul-Emile
 * 
 */
public class PreviousPlayAction extends AbstractAction {
    /** PreviousPlayAction.java long */
    private static final long serialVersionUID = 7697999543665347895L;
    /** singleton */
    private static PreviousPlayAction instance;
    
    /**
     * Returns the singleton
     * @return
     */
    public static PreviousPlayAction getInstance() {
	if (instance == null) {
	    instance = new PreviousPlayAction();
	}
	return instance;
    }
    
    /**
     * 
     */
    private PreviousPlayAction() {
	super();
    }
    
    /**
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
	if (UserInterfaceFactory.isAudioUserInterfaceInstanciated()) {
	    Conductor aConductor = UserInterfaceFactory.getAudioUserInterface().getActiveRenderer();
            if (aConductor != null) {
                aConductor.previous();
            }
	}
    }
}
