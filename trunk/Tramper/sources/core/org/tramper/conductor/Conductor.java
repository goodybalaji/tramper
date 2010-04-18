package org.tramper.conductor;

import org.tramper.doc.SimpleDocument;
import org.tramper.doc.Target;
import org.tramper.player.Musician;
import org.tramper.player.Player;
import org.tramper.ui.Renderer;
import org.tramper.ui.RenderingException;

/**
 * A conductor leads the players to play a document.
 * @author Paul-Emile
 */
public abstract class Conductor implements Renderer, Musician {
    /** document to render */
    protected SimpleDocument document;
    /** target */
    protected Target target;
    /** flag to stop the current play (break from the thread) */
    protected boolean stopped = true;
    /** flag to pause the current play (wait for notify) */
    protected boolean paused = false;
    /** flag to go to the next item */
    protected boolean goNext = false;
    /** flag to go to the previous item */
    protected boolean goPrevious = false;

    /**
     * @see org.tramper.ui.Renderer#getDocument()
     */
    public SimpleDocument getDocument() {
        return this.document;
    }

    /**
     * @see org.tramper.ui.Renderer#isActive()
     */
    public boolean isActive() {
	return document.isActive();
    }

    /**
     * @see org.tramper.ui.Renderer#render(int)
     */
    public void render(int documentPart) throws RenderingException {
	render(document, target, documentPart);
    }

    /**
     * @see org.tramper.ui.Renderer#render(org.tramper.doc.SimpleDocument, org.tramper.doc.Target)
     */
    public void render(SimpleDocument doc, Target target) throws RenderingException {
	render(doc, target, Renderer.ALL_PART);
    }
    
    public abstract Player getPrincipal();
}
