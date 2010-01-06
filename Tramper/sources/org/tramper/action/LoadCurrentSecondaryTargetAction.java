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
public class LoadCurrentSecondaryTargetAction extends AbstractAction implements Action {
    /**
     * LoadCurrentSecondaryTargetAction.java long
     */
    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    public LoadCurrentSecondaryTargetAction() {
	super();
    }
    
    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
	if (UserInterfaceFactory.isGraphicalUserInterfaceInstanciated()) {
	    GraphicalUserInterface gui = UserInterfaceFactory.getGraphicalUserInterface();
	    Target currentSecondaryTarget = gui.getCurrentSecondaryTarget();
	    LoadTargetAction loadAction = new LoadTargetAction(currentSecondaryTarget);
	    loadAction.actionPerformed(e);
	}
    }
}
