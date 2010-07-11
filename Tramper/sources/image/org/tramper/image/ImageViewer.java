package org.tramper.image;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.SwingUtilities;

import org.tramper.doc.ImageDocument;
import org.tramper.doc.SimpleDocument;
import org.tramper.gui.viewer.Viewer;
import org.fingon.player.PlayEvent;

/**
 * Display an image
 * @author Paul-Emile
 */
public class ImageViewer extends Viewer {
    /** ImageViewer.java long */
    private static final long serialVersionUID = -2391448239044632144L;

    /**
     * 
     */
    public ImageViewer() {
    }
    
    /**
     * @see org.tramper.gui.viewer.Viewer#first()
     */
    public void first() {
    }

    /**
     * @see org.tramper.gui.viewer.Viewer#last()
     */
    public void last() {
    }

    /**
     * @see org.tramper.gui.viewer.Viewer#next()
     */
    public void next() {
    }

    /**
     * @see org.tramper.gui.viewer.Viewer#previous()
     */
    public void previous() {
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
     * @param aDocument
     * @return
     */
    public boolean isDocumentSupported(SimpleDocument aDocument) {
	if (aDocument instanceof ImageDocument) {
	    return true;
	}
	return false;
    }

    @Override
    public void initializeViewer() {
	BorderLayout layout = new BorderLayout();
	this.setLayout(layout);

        header = new ImageHeader();
        this.add(header, BorderLayout.NORTH);

        body = new ImageBody();
        add((Component)body, BorderLayout.CENTER);
    }
}
