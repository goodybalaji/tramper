package org.tramper.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.tramper.conductor.Conductor;
import org.tramper.ui.Renderer;
import org.tramper.ui.RenderingException;
import org.tramper.ui.UserInterfaceFactory;

/**
 * @author Paul-Emile
 * 
 */
public class StartPlayAction extends AbstractAction {
    /** StartPlayAction.java long */
    private static final long serialVersionUID = -8724728840060128620L;
    /** singleton */
    private static StartPlayAction instance;
    
    /**
     * Returns the singleton
     * @return
     */
    public static StartPlayAction getInstance() {
	if (instance == null) {
	    instance = new StartPlayAction();
	}
	return instance;
    }
    
    /**
     * 
     */
    private StartPlayAction() {
	super();
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
	if (UserInterfaceFactory.isAudioUserInterfaceInstanciated()) {
	    Conductor aConductor = UserInterfaceFactory.getAudioUserInterface().getActiveRenderer();
            if (aConductor != null) {
                try {
                    aConductor.render(Renderer.ALL_PART);
                } catch (RenderingException ex) {}
            }
	}
    }
}
