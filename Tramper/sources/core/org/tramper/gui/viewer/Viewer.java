package org.tramper.gui.viewer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;

import org.tramper.player.PlayListener;
import org.tramper.ui.Renderer;
import org.tramper.ui.RenderingException;
import org.tramper.doc.DocumentEvent;
import org.tramper.doc.DocumentListener;
import org.tramper.doc.SimpleDocument;
import org.tramper.doc.Target;


/**
 * Document viewer
 * @author Paul-Emile
 */
public abstract class Viewer extends JPanel implements Renderer, PlayListener, DocumentListener {
    /** Viewer.java long */
    private static final long serialVersionUID = 1L;
    /** header */
    protected SimpleHeader header;
    /** body */
    protected Body body;
    /** speakable document */
    protected SimpleDocument document;
    /** target */
    protected Target target;

    /**
     * @see org.tramper.ui.Renderer#render(org.tramper.doc.SimpleDocument)
     */
    public void render(SimpleDocument doc, Target target) throws RenderingException {
	render(doc, target, Renderer.ALL_PART);
    }

    /**
     * @see org.tramper.ui.Renderer#render(int)
     */
    public void render(int documentPart) throws RenderingException {
	if (documentPart == Renderer.ALL_PART) {
	    render(document, target, Renderer.ALL_PART);
	}
    }

    /**
     * @see org.tramper.ui.Renderer#render(org.tramper.doc.SimpleDocument, int)
     */
    public void render(SimpleDocument doc, Target target, int documentPart) throws RenderingException {
	if (!isDocumentSupported(doc)) {
	    throw new RuntimeException(doc.getTitle()+" is not a supported document");
	}
	this.target = target;
	
	if (document != null) {
	    document.removeDocumentListener(this);
	}
        document = doc;
	document.addDocumentListener(this);
	setActive(document.isActive());
	header.displayDocument(doc, target);
	body.displayDocument(doc, target, documentPart);
    }

    /**
     *
     */
    public void relocalize() {
	header.relocalize();
    }

    /**
     * 
     * @see org.tramper.doc.DocumentListener#documentActivated(org.tramper.doc.DocumentEvent)
     */
    public void documentActivated(DocumentEvent event) {
	this.setActive(true);
    }

    /**
     * 
     * @see org.tramper.doc.DocumentListener#documentDeactivated(org.tramper.doc.DocumentEvent)
     */
    public void documentDeactivated(DocumentEvent event) {
	this.setActive(false);
    }

    /**
     * 
     * @see org.tramper.ui.Renderer#isActive()
     */
    public boolean isActive() {
	if (document != null) {
	    return document.isActive();
	} else {
	    return false;
	}
    }
    
    /**
     * 
     * @param active
     */
    public void setActive(boolean active) {
	Border border = null;
	if (active) {
	    Color highLightColor = UIManager.getColor("TextField.selectionBackground");
	    if (highLightColor == null) {
		highLightColor = UIManager.getColor("textHighlight");
	    }
            border = BorderFactory.createLineBorder(highLightColor, 2);
	} else {
            border = BorderFactory.createEmptyBorder(2, 2, 2, 2);
	}
	final Border activeBorder = border;
	Runnable r = new Runnable() {
	    public void run() {
		setBorder(activeBorder);
	    }
	};
	if (SwingUtilities.isEventDispatchThread()) {
	    r.run();
	} else {
	    SwingUtilities.invokeLater(r);
	}
    }

    /**
     * 
     * @see org.tramper.ui.Renderer#getDocument()
     */
    public SimpleDocument getDocument() {
        return document;
    }

    /**
     * 
     * @return
     */
    public Target getTarget() {
	return target;
    }

    /**
     * 
     * @return
     */
    public Body getBody() {
	return body;
    }

    /**
     * 
     * @param newBody
     */
    public void setBody(Component newBody) {
	this.remove((Component)body);
	body = (Body)newBody;
        this.add(newBody, BorderLayout.CENTER);
        body.displayDocument(document, target, Renderer.ALL_PART);
	this.validate();
    }

    /**
     * @see javax.swing.JPanel#updateUI()
     */
    @Override
    public void updateUI() {
	super.updateUI();
	setActive(isActive());
    }

    public void setHeaderVisible(boolean visible) {
	header.setVisible(visible);
    }
    
    /**
     * 
     */
    public abstract void initializeViewer();
}
