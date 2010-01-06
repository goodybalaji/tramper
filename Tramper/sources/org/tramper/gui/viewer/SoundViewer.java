package org.tramper.gui.viewer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
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

/**
 * Sound viewer
 * @author Paul-Emile
 */
public class SoundViewer extends JPanel implements Viewer, DocumentListener {
    /** SoundViewer.java long */
    private static final long serialVersionUID = -4543381389041033478L;
    /** document */
    protected Sound document;
    /** header */
    private SoundHeader header;
    /** body */
    private SoundBody body;
    /** target */
    private Target target;
    
    /**
     * 
     */
    public SoundViewer() {
	BorderLayout layout = new BorderLayout();
	this.setLayout(layout);

        header = new SoundHeader();
        this.add(header, BorderLayout.NORTH);
        
        body = new SoundBody();
        this.add((Component)body, BorderLayout.CENTER);
    }

    /**
     * @see org.tramper.gui.viewer.Viewer#getDocument()
     */
    public SimpleDocument getDocument() {
        return document;
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
	if (documentPart == Renderer.ALL_PART) {
	    render(document, target);
	}
    }

    /**
     * 
     * @see org.tramper.ui.Renderer#render(org.tramper.doc.SimpleDocument, int)
     */
    public void render(SimpleDocument doc, Target target, int documentPart) {
	if (!(doc instanceof Sound)) {
	    throw new RuntimeException(doc.getTitle()+" is not a Media");
	}
	this.target = target;
	
	if (document != null) {
	    document.removeDocumentListener(this);
	}
        document = (Sound)doc;
	document.addDocumentListener(this);
	setActive(document.isActive());
	header.displayDocument(doc, target);
	body.displayDocument(doc, target, documentPart);
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
    }

    /**
     * @see org.tramper.player.PlayListener#previousRead(org.tramper.player.PlayEvent)
     */
    public void previousRead(PlayEvent event) {
    }

    /**
     * @see org.tramper.player.PlayListener#readingEnded(org.tramper.player.PlayEvent)
     */
    public void readingEnded(PlayEvent event) {
	body.animate(false);
    }

    /**
     * @see org.tramper.player.PlayListener#readingPaused(org.tramper.player.PlayEvent)
     */
    public void readingPaused(PlayEvent event) {
	body.animate(false);
    }

    /**
     * @see org.tramper.player.PlayListener#readingResumed(org.tramper.player.PlayEvent)
     */
    public void readingResumed(PlayEvent event) {
	body.animate(true);
    }

    /**
     * @see org.tramper.player.PlayListener#readingStarted(org.tramper.player.PlayEvent)
     */
    public void readingStarted(PlayEvent event) {
	body.animate(true);
    }

    /**
     * @see org.tramper.player.PlayListener#readingStopped(org.tramper.player.PlayEvent)
     */
    public void readingStopped(PlayEvent event) {
	body.animate(false);
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

    public Body getBody() {
	return body;
    }

    public void setBody(Component newBody) {
	this.remove((Component)body);
	body = (SoundBody)newBody;
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
	if (aDocument instanceof Sound) {
	    return true;
	} else {
	    return false;
	}
    }
}
