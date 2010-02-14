package org.tramper.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.tramper.gui.GraphicalUserInterface;
import org.tramper.ui.UserInterfaceFactory;

/**
 * @author Paul-Emile
 * 
 */
public class WindowAction extends AbstractAction {
    /** WindowAction.java long */
    private static final long serialVersionUID = -4102159177890463770L;
    /** singleton */
    private static WindowAction instance;
    
    /**
     * Returns the singleton
     * @return
     */
    public static WindowAction getInstance() {
	if (instance == null) {
	    instance = new WindowAction();
	}
	return instance;
    }
    
    /**
     * 
     */
    private WindowAction() {
	super();
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
	if (UserInterfaceFactory.isGraphicalUserInterfaceInstanciated()) {
	    GraphicalUserInterface gui = UserInterfaceFactory.getGraphicalUserInterface();
	    if (gui.isFullScreenMode()) {
		gui.displayWindowMode();
	    }
	}
    }
}
