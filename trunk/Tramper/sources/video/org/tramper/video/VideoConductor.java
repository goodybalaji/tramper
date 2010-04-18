package org.tramper.video;

import org.apache.log4j.Logger;
import org.tramper.conductor.Conductor;
import org.tramper.doc.SimpleDocument;
import org.tramper.doc.Target;
import org.tramper.doc.Video;
import org.tramper.player.PlayException;
import org.tramper.player.PlayListener;
import org.tramper.player.Player;
import org.tramper.player.PlayerFactory;
import org.tramper.ui.Renderer;
import org.tramper.ui.RenderingException;

import com.sun.media.jmc.MediaProvider;

/**
 * 
 * @author Paul-Emile
 */
public class VideoConductor extends Conductor {
    /** logger */
    private Logger logger = Logger.getLogger(VideoConductor.class);
    /** the video player */
    private VideoPlayer principal;

    public Player getPrincipal() {
	return principal;
    }
    
    /**
     * @see org.tramper.ui.Renderer#render(org.tramper.doc.SimpleDocument, org.tramper.doc.Target, int)
     */
    public void render(SimpleDocument doc, Target target, int documentPart) throws RenderingException {
        if (!(doc instanceof Video)) {
            throw new RenderingException("wrong document class");
        }
	if (documentPart != Renderer.ALL_PART) {
	    return;
	}
	
	this.target = target;
        this.document = doc;
        
        MediaProvider mediaProvider = ((Video)document).getMediaProvider();
        try {
            principal = (VideoPlayer)PlayerFactory.getPlayerByExtension("avi");
            principal.play(mediaProvider);
        } catch (PlayException e) {
            logger.error(e);
            throw new RenderingException(e);
	}
    }

    /**
     * @see org.tramper.ui.Renderer#isDocumentSupported(org.tramper.doc.SimpleDocument)
     */
    public boolean isDocumentSupported(SimpleDocument document) {
	if (document instanceof Video) {
	    return true;
	}
	return false;
    }

    /**
     * @see org.tramper.player.Musician#addPlayListener(org.tramper.player.PlayListener)
     */
    public void addPlayListener(PlayListener listener) {
	principal.addPlayListener(listener);
    }

    /**
     * @see org.tramper.player.Musician#removePlayListener(org.tramper.player.PlayListener)
     */
    public void removePlayListener(PlayListener listener) {
	principal.removePlayListener(listener);
    }

    /**
     * @see org.tramper.player.Musician#getSpeed()
     */
    public int getSpeed() throws PlayException {
	return principal.getSpeed();
    }

    /**
     * @see org.tramper.player.Musician#setSpeed(int)
     */
    public void setSpeed(int speed) {
	principal.setSpeed(speed);
    }

    /**
     * @see org.tramper.player.Musician#getVolume()
     */
    public int getVolume() throws PlayException {
	return principal.getVolume();
    }

    /**
     * @see org.tramper.player.Musician#setVolume(int)
     */
    public void setVolume(int volume) {
	principal.setVolume(volume);
    }

    /**
     * @see org.tramper.player.Musician#isPaused()
     */
    public boolean isPaused() {
	return principal.isPaused();
    }

    /**
     * @see org.tramper.player.Musician#isRunning()
     */
    public boolean isRunning() {
	return principal.isRunning();
    }

    /**
     * @see org.tramper.player.Musician#next()
     */
    public void next() {
	principal.next();
    }

    /**
     * @see org.tramper.player.Musician#previous()
     */
    public void previous() {
	principal.previous();
    }

    /**
     * @see org.tramper.player.Musician#pause()
     */
    public void pause() {
	principal.pause();
    }

    /**
     * @see org.tramper.player.Musician#resume()
     */
    public void resume() {
	principal.resume();
    }

    /**
     * @see org.tramper.player.Musician#stop()
     */
    public void stop() {
	principal.stop();
    }
}
