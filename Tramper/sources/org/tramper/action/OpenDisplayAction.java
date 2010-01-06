package org.tramper.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.tramper.ui.UserInterfaceFactory;

/**
 * @author Paul-Emile
 * 
 */
public class OpenDisplayAction extends AbstractAction {
    /** OpenDisplayAction.java long */
    private static final long serialVersionUID = -4525388746827966940L;
    /** singleton */
    private static OpenDisplayAction instance;
    
    /**
     * Returns the singleton
     * @return
     */
    public static OpenDisplayAction getInstance() {
	if (instance == null) {
	    instance = new OpenDisplayAction();
	}
	return instance;
    }
    
    /**
     * 
     */
    private OpenDisplayAction() {
	super();
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
	if (UserInterfaceFactory.isGraphicalUserInterfaceInstanciated()) {
	    UserInterfaceFactory.getGraphicalUserInterface().openDisplay();
	}
    }

}
