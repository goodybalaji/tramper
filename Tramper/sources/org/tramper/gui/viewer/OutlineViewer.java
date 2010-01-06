package org.tramper.gui.viewer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;

import org.tramper.doc.DocumentEvent;
import org.tramper.doc.DocumentListener;
import org.tramper.doc.Outline;
import org.tramper.doc.SimpleDocument;
import org.tramper.doc.Target;
import org.tramper.doc.TextDocument;
import org.tramper.player.PlayEvent;
import org.tramper.ui.Renderer;

/**
 * Outline viewer
 * @author Paul-Emile
 */
public class OutlineViewer extends JPanel implements Viewer, DocumentListener {
    /** OutlineViewer.java long */
    private static final long serialVersionUID = 4411966231460047613L;
    /** speakable document */
    private Outline document;
    /** header */
    private OutlineHeader header;
    /** body */
    private OutlineBody body;
    /** target */
    private Target target;
    
    /**
     * 
     */
    public OutlineViewer() {
        super();
        BorderLayout layout = new BorderLayout();
        this.setLayout(layout);
        
        header = new OutlineHeader();
        this.add(header, BorderLayout.NORTH);

        body = new OutlineBody();
        add(body, BorderLayout.CENTER);
    }

    /**
     * @see org.tramper.gui.viewer.Viewer#render(org.tramper.doc.SimpleDocument)
     */
    public void render(SimpleDocument doc, Target target) {
	render(doc, target, Renderer.ALL_PART);
    }

    /**
     * 
     * @see org.tramper.ui.Renderer#render(int)
     */
    public void render(int documentPart) {
	render(document, target, documentPart);
    }

    /**
     * 
     * @see org.tramper.ui.Renderer#render(org.tramper.doc.SimpleDocument, int)
     */
    public void render(SimpleDocument doc, Target target, int documentPart) {
	if (!(doc instanceof Outline)) {
	    throw new RuntimeException(doc.getTitle()+" is not an Outline");
	}
	this.target = target;
	
	if (document != null) {
	    document.removeDocumentListener(this);
	}
        document = (Outline)doc;
	document.addDocumentListener(this);
	setActive(document.isActive());
	
	header.displayDocument(doc, target);
	body.displayDocument(doc, target, documentPart);
    }

    /**
     * Returns the document
     * @see org.tramper.gui.viewer.Viewer#getDocument()
     */
    public SimpleDocument getDocument() {
        return document;
    }

    /**
     * Select the first child in the list of selected node's parent's children
     * @see org.tramper.gui.viewer.Viewer#first()
     */
    public void first() {
	body.first();
    }
    
    /**
     * Select the next child in the list of selected node's parent's children
     * @see org.tramper.gui.viewer.Viewer#next()
     */
    public void next() {
	body.next();
    }

    /**
     * Select the previous child in the list of selected node's parent's children
     * @see org.tramper.gui.viewer.Viewer#previous()
     */
    public void previous() {
	body.previous();
    }

    /**
     * Select the last child in the list of selected node's parent's children
     * @see org.tramper.gui.viewer.Viewer#last()
     */
    public void last() {
	body.last();
    }
    
    /**
     * @see javax.swing.JPanel#updateUI()
     */
    @Override
    public void updateUI() {
	super.updateUI();
	
	setActive(isActive());
    }

    /**
     * 
     * @see org.tramper.gui.viewer.Viewer#relocalize()
     */
    public void relocalize() {
	header.relocalize();
    }

    /**
     * @see org.tramper.player.PlayListener#nextRead(org.tramper.player.PlayEvent)
     */
    public void nextRead(PlayEvent event) {
	Runnable r = new Runnable() {
	    public void run() {
		next();
	    }
	};
	SwingUtilities.invokeLater(r);
    }

    /**
     * @see org.tramper.player.PlayListener#previousRead(org.tramper.player.PlayEvent)
     */
    public void previousRead(PlayEvent event) {
	Runnable r = new Runnable() {
	    public void run() {
		previous();
	    }
	};
	SwingUtilities.invokeLater(r);
    }

    /**
     * @see org.tramper.player.PlayListener#readingEnded(org.tramper.player.PlayEvent)
     */
    public void readingEnded(PlayEvent event) {
	Runnable r = new Runnable() {
	    public void run() {
		first();
	    }
	};
	SwingUtilities.invokeLater(r);
    }

    /**
     * @see org.tramper.player.PlayListener#readingPaused(org.tramper.player.PlayEvent)
     */
    public void readingPaused(PlayEvent event) {
    }

    /**
     * @see org.tramper.player.PlayListener#readingResumed(org.tramper.player.PlayEvent)
     */
    public void readingResumed(PlayEvent event) {
    }

    /**
     * @see org.tramper.player.PlayListener#readingStarted(org.tramper.player.PlayEvent)
     */
    public void readingStarted(PlayEvent event) {
    }

    /**
     * @see org.tramper.player.PlayListener#readingStopped(org.tramper.player.PlayEvent)
     */
    public void readingStopped(PlayEvent event) {
	Runnable r = new Runnable() {
	    public void run() {
		first();
	    }
	};
	SwingUtilities.invokeLater(r);
    }

    /**
     * @see org.tramper.player.PlayListener#sampleRateChanged(org.tramper.player.PlayEvent)
     */
    public void sampleRateChanged(PlayEvent event) {
    }

    /**
     * @see org.tramper.player.PlayListener#volumeChanged(org.tramper.player.PlayEvent)
     */
    public void volumeChanged(PlayEvent event) {
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
	if (active) {
	    highLightColor = UIManager.getColor("TextField.selectionBackground");
	    if (highLightColor == null) {
		highLightColor = UIManager.getColor("textHighlight");
	    }
	} else {
	    highLightColor = UIManager.getColor("TextField.background");
	    if (highLightColor == null) {
		highLightColor = UIManager.getColor("text");
	    }
	}
	Border border = null;
        if (highLightColor != null) {
            border = BorderFactory.createLineBorder(highLightColor, 3);
        }
	this.setBorder(border);
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
     * @see org.tramper.gui.viewer.Viewer#getBody()
     */
    public Body getBody() {
	return body;
    }

    /**
     * 
     * @see org.tramper.gui.viewer.Viewer#setBody(java.awt.Component)
     */
    public void setBody(Component newBody) {
	this.remove(body);
	body = (OutlineBody)newBody;
        this.add(newBody, BorderLayout.CENTER);
        body.displayDocument(document, target, Renderer.ALL_PART);
	this.validate();
    }
    
    /**
     * 
     * @see org.tramper.gui.viewer.Viewer#getTarget()
     */
    public Target getTarget() {
	return target;
    }

    /**
     * 
     * @see org.tramper.gui.viewer.Viewer#canRender(org.tramper.doc.TextDocument)
     */
    public boolean canRender(TextDocument aDocument) {
	if (aDocument instanceof Outline) {
	    return true;
	} else {
	    return false;
	}
    }
}
