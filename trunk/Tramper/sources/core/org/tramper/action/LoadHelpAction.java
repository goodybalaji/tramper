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
public class LoadHelpAction extends AbstractAction {
    /** LoadHelpAction.java long */
    private static final long serialVersionUID = 165520303630403098L;
    /** logger */
    private Logger logger = Logger.getLogger(LoadHelpAction.class);
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
	try {
	    URL helpUrl = new URL("http://sites.google.com/site/tramperproject/");
	    Loader loader = LoaderFactory.getInstance().newLoader();
	    loader.download(helpUrl.toString(), new Target(Library.PRIMARY_FRAME, null));
	} catch (MalformedURLException ex) {
	    logger.error("help page unavailable");
	}
    }
}
