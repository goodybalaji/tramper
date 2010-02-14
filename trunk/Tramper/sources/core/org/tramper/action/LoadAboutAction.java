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
public class LoadAboutAction extends AbstractAction {
    /** LoadAboutAction.java long */
    private static final long serialVersionUID = 4453480297641332898L;
    /** singleton */
    private static LoadAboutAction instance;
    
    /**
     * Returns the singleton
     * @return
     */
    public static LoadAboutAction getInstance() {
	if (instance == null) {
	    instance = new LoadAboutAction();
	}
	return instance;
    }
    
    /**
     * 
     */
    private LoadAboutAction() {
	super();
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
	Loader loader = LoaderFactory.getLoader();
	URL aboutUrl = this.getClass().getResource("/org/tramper/doc/about.html");
	loader.download(aboutUrl.toString(), new Target(Library.PRIMARY_FRAME, null));
    }
}
