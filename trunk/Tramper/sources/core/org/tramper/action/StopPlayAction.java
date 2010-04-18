package org.tramper.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.tramper.conductor.Conductor;
import org.tramper.ui.UserInterfaceFactory;

/**
 * The action "stop the current play".
 * @author Paul-Emile
 */
public class StopPlayAction extends AbstractAction {
    /** StopPlayAction.java long */
    private static final long serialVersionUID = 2292031546110234344L;
    /** singleton */
    private static StopPlayAction instance;
    
    /**
     * Returns the singleton
     * @return
     */
    public static StopPlayAction getInstance() {
	if (instance == null) {
	    instance = new StopPlayAction();
	}
	return instance;
    }
    
    /**
     * 
     */
    private StopPlayAction() {
	super();
    }
    
    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
	if (UserInterfaceFactory.isAudioUserInterfaceInstanciated()) {
	    Conductor aConductor = UserInterfaceFactory.getAudioUserInterface().getActiveRenderer();
            if (aConductor != null) {
                aConductor.stop();
            }
	}
    }
}
