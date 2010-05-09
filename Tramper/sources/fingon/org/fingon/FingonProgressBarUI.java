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
    /** music for indeterminate state */
    private URL indeterminateMusicUrl;
    /** sound for intermediate determinate state */
    private URL intermediateDeterminateUrl;
    /** sound for final determinate state */
    private URL finalDeterminateUrl;
    
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
	indeterminateMusicUrl = (URL)UIManager.get("ProgressBarUI.backgroundMusic");
	intermediateDeterminateUrl = (URL)UIManager.get("ProgressBarUI.intermediateSound");
	finalDeterminateUrl = (URL)UIManager.get("ProgressBarUI.finalSound");
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
            if (soundPlayer != null) {
                if (progressBar.isIndeterminate()) {
                    // start playing music
                    try {
                	soundPlayer.playLoop(indeterminateMusicUrl);
                    } catch (PlayException e) {
                	logger.error(e.getMessage(), e);
                    }
                } else {
                    // stop playing music
                    soundPlayer.stop();
                }
            }
            progressBar.repaint();
        } else if ("value".equals(prop)) {
            if (soundPlayer != null) {
                if (!progressBar.isIndeterminate()) {
                    int newValue = progressBar.getValue();
                    int max = progressBar.getMaximum();
                    int frameCount = 10;
                    float step = max/frameCount;
                    try {
                	if (newValue == max) {
                	    soundPlayer.play(finalDeterminateUrl);
                	} else if (newValue%step == 0) {
                	    soundPlayer.play(intermediateDeterminateUrl);
                	}
                    } catch (PlayException e) {
                	logger.error(e.getMessage(), e);
                    }
                }
            }
        }
    }
}
