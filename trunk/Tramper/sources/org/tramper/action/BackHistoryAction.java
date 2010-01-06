package org.tramper.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.tramper.doc.History;

/**
 * @author Paul-Emile
 * 
 */
public class BackHistoryAction extends AbstractAction {
    /** BackHistoryAction.java long */
    private static final long serialVersionUID = 6552701410472285650L;
    /** singleton */
    private static BackHistoryAction instance;
    
    /**
     * Returns the singleton
     * @return
     */
    public static BackHistoryAction getInstance() {
	if (instance == null) {
	    instance = new BackHistoryAction();
	}
	return instance;
    }
    
    /**
     * 
     */
    private BackHistoryAction() {
	super();
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
	History history = History.getInstance();
	history.back();
    }
}
