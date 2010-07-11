package org.tramper.audio;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.SwingUtilities;

import org.tramper.doc.SimpleDocument;
import org.tramper.doc.Sound;
import org.tramper.gui.viewer.Viewer;
import org.fingon.player.PlayEvent;

/**
 * Sound viewer
 * @author Paul-Emile
 */
public class SoundViewer extends Viewer {
    /** SoundViewer.java long */
    private static final long serialVersionUID = -4543381389041033478L;
    
    /**
     * 
     */
    public SoundViewer() {
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
	Runnable r = new Runnable() {
	    public void run() {
		((SoundBody)body).animate(false);
	    }
	};
	SwingUtilities.invokeLater(r);
    }

    /**
     * @see org.tramper.player.PlayListener#readingPaused(org.tramper.player.PlayEvent)
     */
    public void readingPaused(PlayEvent event) {
	Runnable r = new Runnable() {
	    public void run() {
		((SoundBody)body).animate(false);
	    }
	};
	SwingUtilities.invokeLater(r);
    }

    /**
     * @see org.tramper.player.PlayListener#readingResumed(org.tramper.player.PlayEvent)
     */
    public void readingResumed(PlayEvent event) {
	Runnable r = new Runnable() {
	    public void run() {
		((SoundBody)body).animate(true);
            }
        };
        SwingUtilities.invokeLater(r);
    }

    /**
     * @see org.tramper.player.PlayListener#readingStarted(org.tramper.player.PlayEvent)
     */
    public void readingStarted(PlayEvent event) {
	Runnable r = new Runnable() {
	    public void run() {
		((SoundBody)body).animate(true);
            }
        };
        SwingUtilities.invokeLater(r);
    }

    /**
     * @see org.tramper.player.PlayListener#readingStopped(org.tramper.player.PlayEvent)
     */
    public void readingStopped(PlayEvent event) {
	Runnable r = new Runnable() {
	    public void run() {
		((SoundBody)body).animate(false);
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
	if (aDocument instanceof Sound) {
	    return true;
	}
	return false;
    }

    @Override
    public void initializeViewer() {
	BorderLayout layout = new BorderLayout();
	this.setLayout(layout);

        header = new SoundHeader();
        this.add(header, BorderLayout.NORTH);
        
        body = new SoundBody();
        this.add((Component)body, BorderLayout.CENTER);
    }
}
