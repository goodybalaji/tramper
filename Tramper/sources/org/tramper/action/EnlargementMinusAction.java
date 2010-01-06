package org.tramper.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.tramper.gui.GraphicalUserInterface;
import org.tramper.ui.UserInterfaceFactory;

/**
 * @author Paul-Emile
 * 
 */
public class EnlargementMinusAction extends AbstractAction {
    /** EnlargementMinusAction.java long */
    private static final long serialVersionUID = 464436166634704997L;
    /** singleton */
    private static EnlargementMinusAction instance;
    
    /**
     * Returns the singleton
     * @return
     */
    public static EnlargementMinusAction getInstance() {
	if (instance == null) {
	    instance = new EnlargementMinusAction();
	}
	return instance;
    }
    
    /**
     * 
     */
    private EnlargementMinusAction() {
	super();
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
	if (UserInterfaceFactory.isGraphicalUserInterfaceInstanciated()) {
	    GraphicalUserInterface main = UserInterfaceFactory.getGraphicalUserInterface();
	    main.changeEnlargement(-4);
	}
    }

}
