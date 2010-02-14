package org.tramper.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.tramper.doc.Target;
import org.tramper.gui.GraphicalUserInterface;
import org.tramper.ui.UserInterfaceFactory;

/**
 * 
 * @author Paul-Emile
 */
public class LoadCurrentPrimaryTargetAction extends AbstractAction implements Action {
    /**
     * LoadCurrentPrimaryTargetAction.java long
     */
    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    public LoadCurrentPrimaryTargetAction() {
	super();
    }
    
    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
	if (UserInterfaceFactory.isGraphicalUserInterfaceInstanciated()) {
	    GraphicalUserInterface gui = UserInterfaceFactory.getGraphicalUserInterface();
	    Target currentPrimaryTarget = gui.getCurrentPrimaryTarget();
	    LoadTargetAction loadAction = new LoadTargetAction(currentPrimaryTarget);
	    loadAction.actionPerformed(e);
	}
    }
}
