package org.tramper.webPage;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.SwingUtilities;

import org.tramper.doc.SimpleDocument;
import org.tramper.doc.WebPage;
import org.tramper.gui.viewer.Viewer;
import org.fingon.player.PlayEvent;

/**
 * Web page viewer
 * @author Paul-Emile
 */
public class WebPageViewer extends Viewer {
    /** FeedViewer.java long */
    protected static final long serialVersionUID = 4490739741816480539L;
    
    /**
     * 
     */
    public WebPageViewer() {
        super();
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

    /**
     * 
     * @param aDocument
     * @return
     */
    public boolean isDocumentSupported(SimpleDocument aDocument) {
	if (aDocument instanceof WebPage) {
	    return true;
	}
	return false;
    }

    @Override
    public void initializeViewer() {
        BorderLayout layout = new BorderLayout();
        this.setLayout(layout);

        header = new WebPageHeader();
        this.add(header, BorderLayout.NORTH);
        
        try {
	    body = new NativeWebPageBody();
	    this.add((Component)body, BorderLayout.CENTER);
	} catch (Exception e) {}
    }
}
