package org.tramper.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.tramper.doc.History;

/**
 * @author Paul-Emile
 * 
 */
public class ForwardHistoryAction extends AbstractAction {
    /** ForwardHistoryAction.java long */
    private static final long serialVersionUID = 2443675893507430846L;
    /** singleton */
    private static ForwardHistoryAction instance;
    
    /**
     * Returns the singleton
     * @return
     */
    public static ForwardHistoryAction getInstance() {
	if (instance == null) {
	    instance = new ForwardHistoryAction();
	}
	return instance;
    }
    
    /**
     * 
     */
    private ForwardHistoryAction() {
	super();
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
	History history = History.getInstance();
	history.forward();
    }
}
