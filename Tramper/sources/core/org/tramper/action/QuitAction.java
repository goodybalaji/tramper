package org.tramper.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.tramper.loader.LoaderFactory;
import org.tramper.ui.UserInterfaceFactory;

/**
 * 
 * @author Paul-Emile
 */
public class QuitAction extends AbstractAction {
    /** QuitAction.java long */
    private static final long serialVersionUID = -3020847266528300129L;
    /** singleton */
    private static QuitAction instance;
    
    /**
     * Returns the singleton
     * @return
     */
    public static QuitAction getInstance() {
	if (instance == null) {
	    instance = new QuitAction();
	}
	return instance;
    }
    
    /**
     * 
     */
    private QuitAction() {
	super();
	putValue(Action.ACTION_COMMAND_KEY, "quit");
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
	// stop the remaining loadings if any
	LoaderFactory.getInstance().unregister();

        UserInterfaceFactory.removeAudioUserInterface();
        UserInterfaceFactory.removeGraphicalUserInterface();
	
	System.exit(0);
    }
}
