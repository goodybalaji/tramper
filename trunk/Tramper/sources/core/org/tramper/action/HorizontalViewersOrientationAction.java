package org.tramper.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.tramper.gui.GraphicalUserInterface;
import org.tramper.ui.UserInterfaceFactory;

/**
 * 
 * @author Paul-Emile
 */
public class HorizontalViewersOrientationAction extends AbstractAction {
    /**
     * SwitchViewersOrientation.java long
     */
    private static final long serialVersionUID = 1L;

    public HorizontalViewersOrientationAction() {
	super();
    }
    
    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
	if (UserInterfaceFactory.isGraphicalUserInterfaceInstanciated()) {
	    GraphicalUserInterface gui = UserInterfaceFactory.getGraphicalUserInterface();
	    if (!gui.isSplitPaneHorizontal()) {
		gui.switchSplitPaneOrientation();
	    }
	}
    }
}
