package org.tramper.action;

import java.awt.event.ActionEvent;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.AbstractAction;

import org.tramper.browser.SearchEngine;
import org.tramper.browser.SearchEngineFactory;
import org.tramper.doc.Target;
import org.tramper.gui.GraphicalUserInterface;
import org.tramper.loader.Loader;
import org.tramper.loader.LoaderFactory;
import org.tramper.ui.UserInterfaceFactory;

/**
 * @author Paul-Emile
 * 
 */
public class LoadTargetAction extends AbstractAction {
    /** LoadTargetAction.java long */
    private static final long serialVersionUID = 1L;
    /** target */
    private Target target;

    /**
     * 
     */
    public LoadTargetAction(Target target) {
	super();
	this.target = target;
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
	if (UserInterfaceFactory.isGraphicalUserInterfaceInstanciated()) {
	    GraphicalUserInterface gui = UserInterfaceFactory.getGraphicalUserInterface();
	    String initAddress = gui.getAddress();
	    String address = initAddress;

            try {
		new URL(address);
	    } catch (MalformedURLException urlEvent) {
		if (initAddress.contains(".") && !initAddress.contains(" ")) {
		    // try to append the HTTP protocol
		    address = "http://" + initAddress;
		} else {
		    // not an URL, maybe they are keywords search
	            //keywords are separated by white spaces
	            String[] keywords = initAddress.split("\\s+");
	            //Get the selected feed search engine
	            SearchEngineFactory engineFactory = SearchEngineFactory.getInstance();
	            SearchEngine searchEngine = engineFactory.getSelectedSearchEngine();
	            //Make the research URL 
	            address = searchEngine.makeResearchUrl(keywords);
		}
	    }
            Loader aLoader = LoaderFactory.getLoader();
            aLoader.download(address, target);
	}
    }
}
