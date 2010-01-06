package org.tramper.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.tramper.doc.Favorites;
import org.tramper.doc.Feed;
import org.tramper.doc.Library;
import org.tramper.doc.Target;

/**
 * @author Paul-Emile
 * 
 */
public class LoadFavoritesAction extends AbstractAction {
    /** LoadFavoritesAction.java long */
    private static final long serialVersionUID = 407300712205900469L;
    /** singleton */
    private static LoadFavoritesAction instance;
    
    /**
     * Returns the singleton
     * @return
     */
    public static LoadFavoritesAction getInstance() {
	if (instance == null) {
	    instance = new LoadFavoritesAction();
	}
	return instance;
    }
    
    /**
     * 
     */
    private LoadFavoritesAction() {
	super();
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
	Favorites favorites = Favorites.getInstance();
	Feed fav = favorites.getFavorites();
	Library lib = Library.getInstance();
	lib.addDocument(fav, new Target(Library.SECONDARY_FRAME, null));
    }
}
