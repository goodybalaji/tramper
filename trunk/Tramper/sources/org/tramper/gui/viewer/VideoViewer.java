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
import org.tramper.doc.Sound;
import org.tramper.doc.SimpleDocument;
import org.tramper.doc.Target;
import org.tramper.doc.TextDocument;
import org.tramper.player.PlayEvent;
import org.tramper.ui.Renderer;
import org.tramper.ui.RenderingException;

/**
 * 
 * @author Paul-Emile
 */
public class VideoViewer extends JPanel implements Viewer, DocumentListener {
    /** VideoViewer.java long */
    private static final long serialVersionUID = 1L;
    /** speakable document */
    private SimpleDocument document;
    /** header */
    private VideoHeader header;
    /** target */
    private Target target;
    /** body */
    private VideoBody body;

    public VideoViewer() {
        BorderLayout layout = new BorderLayout();
        this.setLayout(layout);
        
        header = new VideoHeader();
        this.add(header, BorderLayout.NORTH);

        body = new VideoBody();
        add(body, BorderLayout.CENTER);
    }
    
    /**
     * @see org.tramper.gui.viewer.Viewer#canRender(org.tramper.doc.TextDocument)
     */
    public boolean canRender(TextDocument aDocument) {
	if (aDocument instanceof Sound) {
	    return true;
	} else {
	    return false;
	}
    }

    /**
     * @see org.tramper.gui.viewer.Viewer#getBody()
     */
    public Body getBody() {
	return body;
    }

    /**
     * @see org.tramper.gui.viewer.Viewer#getTarget()
     */
    public Target getTarget() {
	return target;
    }

    /**
     * @see org.tramper.gui.viewer.Viewer#relocalize()
     */
    public void relocalize() {
	header.relocalize();
    }

    /**
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
     * @see org.tramper.gui.viewer.Viewer#setBody(java.awt.Component)
     */
    public void setBody(Component newBody) {
	this.remove(body);
	body = (VideoBody)newBody;
        this.add(newBody, BorderLayout.CENTER);
        body.displayDocument(document, target, Renderer.ALL_PART);
	this.validate();
    }

    /**
     * @see org.tramper.ui.Renderer#getDocument()
     */
    public SimpleDocument getDocument() {
	return document;
    }

    /**
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
     * @see org.tramper.ui.Renderer#render(org.tramper.doc.SimpleDocument, org.tramper.doc.Target)
     */
    public void render(SimpleDocument doc, Target target) throws RenderingException {
	render(doc, target, Renderer.ALL_PART);
    }

    /**
     * @see org.tramper.ui.Renderer#render(int)
     */
    public void render(int documentPart) throws RenderingException {
	render(document, target, Renderer.ALL_PART);
    }

    /**
     * @see org.tramper.ui.Renderer#render(org.tramper.doc.SimpleDocument, org.tramper.doc.Target, int)
     */
    public void render(SimpleDocument doc, Target target, int documentPart) throws RenderingException {
	this.target = target;
	if (document != null) {
	    document.removeDocumentListener(this);
	}
        document = doc;
	document.addDocumentListener(this);
	setActive(document.isActive());
	
        header.displayDocument(document, target);
        body.displayDocument(document, target, documentPart);
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
     * @see org.tramper.player.PlayListener#nextRead(org.tramper.player.PlayEvent)
     */
    public void nextRead(PlayEvent event) {
	Runnable r = new Runnable() {
	    public void run() {
		body.next();
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
		body.previous();
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
		body.first();
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
		body.first();
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

    public void documentActivated(DocumentEvent event) {
	this.setActive(true);
    }

    public void documentDeactivated(DocumentEvent event) {
	this.setActive(false);
    }
}
