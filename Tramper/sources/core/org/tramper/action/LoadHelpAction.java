package org.tramper.action;

import java.awt.event.ActionEvent;
import java.net.URL;

import javax.swing.AbstractAction;

import org.tramper.doc.Library;
import org.tramper.doc.Target;
import org.tramper.loader.Loader;
import org.tramper.loader.LoaderFactory;

/**
 * @author Paul-Emile
 * 
 */
public class LoadHelpAction extends AbstractAction {
    /** LoadHelpAction.java long */
    private static final long serialVersionUID = 165520303630403098L;
    /** singleton */
    private static LoadHelpAction instance;
    
    /**
     * Returns the singleton
     * @return
     */
    public static LoadHelpAction getInstance() {
	if (instance == null) {
	    instance = new LoadHelpAction();
	}
	return instance;
    }
    
    /**
     * 
     */
    private LoadHelpAction() {
	super();
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
	Loader loader = LoaderFactory.getLoader();
	URL helpUrl = this.getClass().getResource("/org/tramper/doc/help.html");
	loader.download(helpUrl.toString(), new Target(Library.PRIMARY_FRAME, null));
    }
}
