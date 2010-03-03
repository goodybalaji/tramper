package org.tramper.outline;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.SwingUtilities;

import org.tramper.doc.Outline;
import org.tramper.doc.TextDocument;
import org.tramper.gui.viewer.Viewer;
import org.tramper.player.PlayEvent;

/**
 * Outline viewer
 * @author Paul-Emile
 */
public class OutlineViewer extends Viewer {
    /** OutlineViewer.java long */
    private static final long serialVersionUID = 4411966231460047613L;
    
    /**
     * 
     */
    public OutlineViewer() {
        super();
    }

    /**
     * Selects the first child in the list of selected node's parent's children.
     */
    public void first() {
	body.first();
    }
    
    /**
     * Selects the next child in the list of selected node's parent's children.
     */
    public void next() {
	body.next();
    }

    /**
     * Selects the previous child in the list of selected node's parent's children.
     */
    public void previous() {
	body.previous();
    }

    /**
     * Select the last child in the list of selected node's parent's children
     */
    public void last() {
	body.last();
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
     * @see org.tramper.gui.viewer.Viewer#isDocumentSupported(org.tramper.doc.TextDocument)
     */
    public boolean isDocumentSupported(TextDocument aDocument) {
	if (aDocument instanceof Outline) {
	    return true;
	}
	return false;
    }

    @Override
    public void initializeViewer() {
        BorderLayout layout = new BorderLayout();
        this.setLayout(layout);
        
        header = new OutlineHeader();
        this.add(header, BorderLayout.NORTH);

        body = new OutlineBody();
        add((Component)body, BorderLayout.CENTER);
    }
}
