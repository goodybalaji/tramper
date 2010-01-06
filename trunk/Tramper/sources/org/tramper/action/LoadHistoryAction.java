package org.tramper.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.tramper.doc.Feed;
import org.tramper.doc.History;
import org.tramper.doc.Library;
import org.tramper.doc.Target;

/**
 * @author Paul-Emile
 * 
 */
public class LoadHistoryAction extends AbstractAction {
    /** LoadHistoryAction.java long */
    private static final long serialVersionUID = -2816593407602207228L;
    /** singleton */
    private static LoadHistoryAction instance;
    
    /**
     * Returns the singleton
     * @return
     */
    public static LoadHistoryAction getInstance() {
	if (instance == null) {
	    instance = new LoadHistoryAction();
	}
	return instance;
    }
    
    /**
     * 
     */
    private LoadHistoryAction() {
	super();
    }
    
    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
	History history = History.getInstance();
	Feed historyDoc = history.getHistory();
	Library lib = Library.getInstance();
	lib.addDocument(historyDoc, new Target(Library.SECONDARY_FRAME, null));
    }

}
