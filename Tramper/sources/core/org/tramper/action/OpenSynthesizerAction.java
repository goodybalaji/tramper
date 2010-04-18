package org.tramper.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.EnhancedIcon;
import javax.swing.Icon;

import org.tramper.ui.UserInterfaceFactory;

/**
 * 
 * @author Paul-Emile
 */
public class OpenSynthesizerAction extends AbstractAction {
    /** OpenSynthesizerAction.java long */
    private static final long serialVersionUID = -2643522601520860141L;
    
    /**
     * 
     * @param target 
     */
    public OpenSynthesizerAction() {
	super();
        Icon closeIcon = new EnhancedIcon(getClass().getResource("/org/tramper/gui/images/speaker.png"));
	putValue(Action.SMALL_ICON, closeIcon);
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
	if (UserInterfaceFactory.isGraphicalUserInterfaceInstanciated()) {
	    UserInterfaceFactory.getGraphicalUserInterface().openSynthesizer();
	}
    }
}
