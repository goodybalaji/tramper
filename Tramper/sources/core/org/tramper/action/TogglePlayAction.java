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
public class TogglePlayAction extends AbstractAction {
    /** TogglePlayAction.java long */
    private static final long serialVersionUID = 3665262762877521708L;
    /** singleton */
    private static TogglePlayAction instance;
    
    /**
     * Returns the singleton
     * @return
     */
    public static TogglePlayAction getInstance() {
	if (instance == null) {
	    instance = new TogglePlayAction();
	}
	return instance;
    }
    
    /**
     * 
     */
    private TogglePlayAction() {
	super();
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
	if (UserInterfaceFactory.isAudioUserInterfaceInstanciated()) {
	    Conductor aConductor = UserInterfaceFactory.getAudioUserInterface().getActiveRenderer();
            if (aConductor != null) {
                if (aConductor.isRunning()) {
                    aConductor.stop();
                } else {
                    try {
                	aConductor.render(Renderer.ALL_PART);
                    } catch (RenderingException ex) {}
                }
            }
	}
    }

}
