package org.tramper.gui.viewer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.Border;

import org.tramper.player.PlayListener;
import org.tramper.ui.Renderer;
import org.tramper.ui.RenderingException;
import org.tramper.doc.DocumentEvent;
import org.tramper.doc.DocumentListener;
import org.tramper.doc.SimpleDocument;
import org.tramper.doc.Target;
import org.tramper.doc.TextDocument;


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
     * @see org.tramper.gui.viewer.Viewer#setActive(boolean)
     */
    public void setActive(boolean active) {
	Color highLightColor = null;
	Border border = null;
	if (active) {
	    highLightColor = UIManager.getColor("TextField.selectionBackground");
	    if (highLightColor == null) {
		highLightColor = UIManager.getColor("textHighlight");
	    }
            border = BorderFactory.createLineBorder(highLightColor, 2);
	} else {
            border = BorderFactory.createEmptyBorder(2, 2, 2, 2);
	}
	this.setBorder(border);
    }

    /**
     * 
     * @see org.tramper.ui.Renderer#getDocument()
     */
    public SimpleDocument getDocument() {
        return document;
    }

    /**
     * @see org.tramper.gui.viewer.Viewer#getTarget()
     */
    public Target getTarget() {
	return target;
    }

    /**
     * @see org.tramper.gui.viewer.Viewer#getBody()
     */
    public Body getBody() {
	return body;
    }

    /**
     * @see org.tramper.gui.viewer.Viewer#setBody(java.awt.Component)
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
     * @param document
     * @return
     */
    public abstract boolean isDocumentSupported(TextDocument aDocument);
    
    /**
     * 
     */
    public abstract void initializeViewer();
}
