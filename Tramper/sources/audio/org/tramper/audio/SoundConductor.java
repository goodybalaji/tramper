package org.tramper.audio;

import java.net.URL;

import org.apache.log4j.Logger;
import org.tramper.conductor.Conductor;
import org.tramper.doc.SimpleDocument;
import org.tramper.doc.Sound;
import org.tramper.doc.Target;
import org.tramper.player.PlayException;
import org.tramper.player.PlayListener;
import org.tramper.player.Player;
import org.tramper.player.PlayerFactory;
import org.tramper.ui.Renderer;
import org.tramper.ui.RenderingException;

/**
 * Knows what to do with a Sound document.
 * @author Paul-Emile
 */
public class SoundConductor extends Conductor {
    /** logger */
    private Logger logger = Logger.getLogger(SoundConductor.class);
    /** the sound player */
    private Player principal;

    public Player getPrincipal() {
	return principal;
    }
    
    /**
     * Play a document
     * @param document
     * @param documentPart
     * @throws PlayException
     */
    public void render(SimpleDocument doc, Target target, int documentPart) throws RenderingException {
	this.target = target;
	
	if (documentPart != Renderer.ALL_PART) {
	    return;
	}
	
        if (!(doc instanceof Sound)) {
            throw new RenderingException("wrong document class");
        }
        document = (Sound)doc;
        
        URL url = document.getUrl();
        String path = url.getPath();
        int lastIndexOfPoint = path.lastIndexOf(".");
        if (lastIndexOfPoint != -1) {
            String extension = path.substring(lastIndexOfPoint+1);
            try {
                principal = PlayerFactory.getPlayerByExtension(extension);
        	principal.play(url);
            } catch (PlayException e) {
        	logger.error(e);
        	throw new RenderingException(e);
            }
        }
    }

    /**
     * @see org.tramper.ui.Renderer#isDocumentSupported(org.tramper.doc.SimpleDocument)
     */
    public boolean isDocumentSupported(SimpleDocument document) {
	if (document instanceof Sound) {
	    return true;
	}
	return false;
    }

    /**
     * @see org.tramper.player.Player#addPlayListener(org.tramper.player.PlayListener)
     */
    public void addPlayListener(PlayListener listener) {
	principal.addPlayListener(listener);
    }

    /**
     * @see org.tramper.player.Player#removePlayListener(org.tramper.player.PlayListener)
     */
    public void removePlayListener(PlayListener listener) {
	principal.removePlayListener(listener);
    }

    /**
     * @see org.tramper.player.Player#isPaused()
     */
    public boolean isPaused() {
	return principal.isPaused();
    }

    /**
     * @see org.tramper.player.Player#isRunning()
     */
    public boolean isRunning() {
	return principal.isRunning();
    }

    /**
     * @see org.tramper.player.Player#pause()
     */
    public void pause() {
	principal.pause();
    }
    
    /**
     * @see org.tramper.player.Player#resume()
     */
    public void resume() {
	principal.resume();
    }

    /**
     * @see org.tramper.player.Player#stop()
     */
    public void stop() {
	principal.stop();
    }

    /**
     * @see org.tramper.player.Player#next()
     */
    public void next() {
	principal.next();
    }

    /**
     * @see org.tramper.player.Player#previous()
     */
    public void previous() {
	principal.previous();
    }

    /**
     * @see org.tramper.player.Player#getSpeed()
     */
    public int getSpeed() throws PlayException {
	return principal.getSpeed();
    }

    /**
     * @see org.tramper.player.Player#setSpeed(int)
     */
    public void setSpeed(int speed) {
	principal.setSpeed(speed);
    }

    /**
     * @see org.tramper.player.Player#getVolume()
     */
    public int getVolume() throws PlayException {
	return principal.getVolume();
    }

    /**
     * @see org.tramper.player.Player#setVolume(int)
     */
    public void setVolume(int volume) {
	principal.setVolume(volume);
    }
}
