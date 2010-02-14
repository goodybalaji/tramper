package org.tramper.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.tramper.ui.UserInterfaceFactory;

/**
 * @author Paul-Emile
 * 
 */
public class OpenRecognizerAction extends AbstractAction {
    /** OpenRecognizerAction.java long */
    private static final long serialVersionUID = -6616242318496217601L;
    /** singleton */
    private static OpenRecognizerAction instance;
    
    /**
     * Returns the singleton
     * @return
     */
    public static OpenRecognizerAction getInstance() {
	if (instance == null) {
	    instance = new OpenRecognizerAction();
	}
	return instance;
    }
    
    /**
     * 
     */
    private OpenRecognizerAction() {
	super();
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
	if (UserInterfaceFactory.isGraphicalUserInterfaceInstanciated()) {
	    UserInterfaceFactory.getGraphicalUserInterface().openRecognizer();
	}
    }
}
