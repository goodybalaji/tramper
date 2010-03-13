package org.fingon;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;

import javax.swing.JComponent;
import javax.swing.JProgressBar;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.ProgressBarUI;

import org.apache.log4j.Logger;
import org.tramper.player.PlayException;
import org.tramper.player.PlayerFactory;
import org.tramper.audio.SoundPlayer;

/**
 * 
 * @author Paul-Emile
 */
public class FingonProgressBarUI extends ProgressBarUI implements PropertyChangeListener {
    /** logger */
    private static Logger logger = Logger.getLogger(FingonProgressBarUI.class);
    /** music player */
    private SoundPlayer soundPlayer;
    /** sound to play url */
    private URL soundUrl;
    
    public FingonProgressBarUI() {
	try {
	    soundPlayer = (SoundPlayer)PlayerFactory.getPlayerByExtension("mp3");
	} catch (PlayException e) {
            logger.error(e.getMessage(), e);
	}
    }
    
    /**
     * Returns the instance of UI
     * @param c
     * @return
     */
    public static ComponentUI createUI(JComponent c) {
	return new FingonProgressBarUI();
    }

    /**
     * @see javax.swing.plaf.ComponentUI#installUI(javax.swing.JComponent)
     */
    @Override
    public void installUI(JComponent c) {
	JProgressBar progress = (JProgressBar)c;
	progress.addPropertyChangeListener(this);
	soundUrl = (URL)UIManager.get("ProgressBarUI.backgroundMusic");
    }

    /**
     * @see javax.swing.plaf.ComponentUI#uninstallUI(javax.swing.JComponent)
     */
    @Override
    public void uninstallUI(JComponent c) {
	JProgressBar progress = (JProgressBar)c;
	progress.removePropertyChangeListener(this);
    }

    /**
     * 
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent evt) {
	JProgressBar progressBar = (JProgressBar)evt.getSource();
	String prop = evt.getPropertyName();
        if ("indeterminate".equals(prop)) {
            if (progressBar.isIndeterminate()) {
        	// start playing music
        	try {
		    soundPlayer.playLoop(soundUrl);
		} catch (PlayException e) {
		    logger.error(e.getMessage(), e);
		}
            } else {
        	// stop playing music
                soundPlayer.stop();
            }
            progressBar.repaint();
        }
    }
}
