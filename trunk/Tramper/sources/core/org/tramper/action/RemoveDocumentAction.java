package org.tramper.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.EnhancedIcon;
import javax.swing.Icon;

import org.tramper.doc.Library;
import org.tramper.doc.Target;

/**
 * @author Paul-Emile
 * 
 */
public class RemoveDocumentAction extends AbstractAction {
    /** RemoveDocumentAction.java long */
    private static final long serialVersionUID = 1L;
    /** document's target */
    private Target target;
    
    /**
     * 
     */
    public RemoveDocumentAction(Target target) {
	super();
	this.target = target;
        Icon closeIcon = new EnhancedIcon(getClass().getResource("/org/tramper/gui/images/Error.png"));
	putValue(Action.SMALL_ICON, closeIcon);
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
	Library.getInstance().removeDocumentAndActivateFirst(target);
    }
}
