package org.tramper.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.tramper.gui.GraphicalUserInterface;
import org.tramper.ui.UserInterfaceFactory;

/**
 * @author Paul-Emile
 * 
 */
public class FullScreenAction extends AbstractAction {
    /** FullScreenAction.java long */
    private static final long serialVersionUID = -1969847192073340854L;
    /** singleton */
    private static FullScreenAction instance;
    
    /**
     * Returns the singleton
     * @return
     */
    public static FullScreenAction getInstance() {
	if (instance == null) {
	    instance = new FullScreenAction();
	}
	return instance;
    }
    
    /**
     * 
     */
    private FullScreenAction() {
	super();
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
	if (UserInterfaceFactory.isGraphicalUserInterfaceInstanciated()) {
	    GraphicalUserInterface gui = UserInterfaceFactory.getGraphicalUserInterface();
	    if (gui.isFullScreenMode() == false) {
		gui.displayFullScreenMode();
	    }
	}
    }
}
