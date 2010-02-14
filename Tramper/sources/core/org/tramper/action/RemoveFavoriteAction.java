package org.tramper.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.tramper.doc.Favorites;
import org.tramper.doc.Library;
import org.tramper.doc.SimpleDocument;

/**
 * @author Paul-Emile
 * 
 */
public class RemoveFavoriteAction extends AbstractAction {
    /** RemoveFavoriteAction.java long */
    private static final long serialVersionUID = -8410387294039442740L;
    /** singleton */
    private static RemoveFavoriteAction instance;
    
    /**
     * Returns the singleton
     * @return
     */
    public static RemoveFavoriteAction getInstance() {
	if (instance == null) {
	    instance = new RemoveFavoriteAction();
	}
	return instance;
    }
    
    /**
     * 
     */
    private RemoveFavoriteAction() {
	super();
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
	SimpleDocument document = Library.getInstance().getActiveDocument();
	if (document != null) {
	    Favorites favorites = Favorites.getInstance();
	    favorites.removeFavorite(document);
	}
    }

}
