package org.tramper.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.tramper.player.Player;
import org.tramper.ui.UserInterfaceFactory;

/**
 * @author Paul-Emile
 * 
 */
public class ResumePlayAction extends AbstractAction {
    /** ResumePlayAction.java long */
    private static final long serialVersionUID = 2150107793041545844L;
    /** singleton */
    private static ResumePlayAction instance;
    
    /**
     * Returns the singleton
     * @return
     */
    public static ResumePlayAction getInstance() {
	if (instance == null) {
	    instance = new ResumePlayAction();
	}
	return instance;
    }
    
    /**
     * 
     */
    private ResumePlayAction() {
	super();
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
	if (UserInterfaceFactory.isAudioUserInterfaceInstanciated()) {
            Player aPlayer = UserInterfaceFactory.getAudioUserInterface().getActiveRenderer();
            if (aPlayer != null) {
                aPlayer.resume();
            }
	}
    }
}
