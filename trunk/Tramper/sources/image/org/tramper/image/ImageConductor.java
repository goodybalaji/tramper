package org.tramper.image;

import java.awt.image.BufferedImage;

import org.apache.log4j.Logger;
import org.tramper.conductor.Conductor;
import org.tramper.doc.ImageDocument;
import org.tramper.doc.SimpleDocument;
import org.tramper.doc.Target;
import org.tramper.player.PlayException;
import org.tramper.player.PlayListener;
import org.tramper.player.Player;
import org.tramper.player.PlayerFactory;
import org.tramper.ui.Renderer;
import org.tramper.ui.RenderingException;

/**
 * 
 * @author Paul-Emile
 */
public class ImageConductor extends Conductor {
    /** logger */
    private Logger logger = Logger.getLogger(ImageConductor.class);
    /** the image player */
    private ImagePlayer principal;

    public Player getPrincipal() {
	return principal;
    }
    
    /**
     * @see org.tramper.ui.Renderer#isDocumentSupported(org.tramper.doc.SimpleDocument)
     */
    public boolean isDocumentSupported(SimpleDocument document) {
	if (document instanceof ImageDocument) {
	    return true;
	}
	return false;
    }
    
    public void render(SimpleDocument doc, Target target, int documentPart) throws RenderingException {
        if (!(doc instanceof ImageDocument)) {
            throw new RenderingException("wrong document class: "+doc);
        }
        
	if (documentPart != Renderer.ALL_PART) {
	    return;
	}
	
	this.target = target;
	this.document = (ImageDocument)doc;
	BufferedImage image = ((ImageDocument)doc).getImage();
	
	try {
	    principal = (ImagePlayer)PlayerFactory.getPlayerByExtension("jpg");
	    principal.play(image);
	} catch (PlayException e) {
	    logger.error(e);
	}
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
