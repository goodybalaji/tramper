package org.tramper.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.tramper.conductor.Conductor;
import org.tramper.ui.UserInterfaceFactory;

/**
 * @author Paul-Emile
 * 
 */
public class NextPlayAction extends AbstractAction {
    /** NextPlayAction.java long */
    private static final long serialVersionUID = 4348112275933056385L;
    /** singleton */
    private static NextPlayAction instance;
    
    /**
     * Returns the singleton
     * @return
     */
    public static NextPlayAction getInstance() {
	if (instance == null) {
	    instance = new NextPlayAction();
	}
	return instance;
    }
    
    /**
     * 
     */
    private NextPlayAction() {
	super();
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
	if (UserInterfaceFactory.isAudioUserInterfaceInstanciated()) {
            Conductor aConductor = UserInterfaceFactory.getAudioUserInterface().getActiveRenderer();
            if (aConductor != null) {
        	aConductor.next();
            }
	}
    }
}
