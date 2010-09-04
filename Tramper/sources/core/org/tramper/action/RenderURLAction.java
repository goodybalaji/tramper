package org.tramper.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.tramper.doc.Target;
import org.tramper.loader.Loader;
import org.tramper.loader.LoaderFactory;

/**
 * @author Paul-Emile
 * 
 */
public class RenderURLAction extends AbstractAction {
    /** RenderURLAction.java long */
    private static final long serialVersionUID = 9169334324379715128L;
    /** url */
    private String url;
    /** target */
    private Target target;

    /**
     * 
     */
    public RenderURLAction(String url, Target target) {
	this.url = url;
	this.target = target;
    }
    
    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        Loader loader = LoaderFactory.getInstance().newLoader();
        loader.download(url, target);
    }
}
