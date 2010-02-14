package org.tramper.action;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;

import org.tramper.ui.Renderer;
import org.tramper.ui.RenderingException;
import org.tramper.ui.UserInterface;
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
	List<UserInterface> ui = UserInterfaceFactory.getAllUserInterfaces();
	for (UserInterface anUi : ui) {
	    Renderer renderer = anUi.getActiveRenderer();
	    try {
		renderer.render(Renderer.ALL_PART);
            } catch (RenderingException ex) {}
	}
    }
}
