package org.tramper.action;

import java.awt.event.ActionEvent;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.AbstractAction;

import org.apache.log4j.Logger;
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
    /** logger */
    private Logger logger = Logger.getLogger(LoadAboutAction.class);
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
    public void actionPerformed(ActionEvent evt) {
	try {
	    URL aboutUrl = new URL("http://sites.google.com/site/tramperproject/third-party-libraries");
	    Loader loader = LoaderFactory.getLoader();
	    loader.download(aboutUrl.toString(), new Target(Library.PRIMARY_FRAME, null));
	} catch (MalformedURLException ex) {
	    logger.error("about page unavailable");
	}
    }
}
