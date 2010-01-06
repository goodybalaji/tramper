package org.tramper.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.tramper.gui.GraphicalUserInterface;
import org.tramper.ui.UserInterfaceFactory;

/**
 * @author Paul-Emile
 * 
 */
public class EnlargementPlusAction extends AbstractAction {
    /** EnlargementPlusAction.java long */
    private static final long serialVersionUID = -5802662502245843389L;
    /** singleton */
    private static EnlargementPlusAction instance;
    
    /**
     * Returns the singleton
     * @return
     */
    public static EnlargementPlusAction getInstance() {
	if (instance == null) {
	    instance = new EnlargementPlusAction();
	}
	return instance;
    }
    
    /**
     * 
     */
    private EnlargementPlusAction() {
	super();
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
	if (UserInterfaceFactory.isGraphicalUserInterfaceInstanciated()) {
            GraphicalUserInterface main = UserInterfaceFactory.getGraphicalUserInterface();
            main.changeEnlargement(+4);
	}
    }

}
