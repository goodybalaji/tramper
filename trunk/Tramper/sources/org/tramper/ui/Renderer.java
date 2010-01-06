package org.tramper.ui;

import org.tramper.doc.SimpleDocument;
import org.tramper.doc.Target;

/**
 * @author Paul-Emile
 * 
 */
public interface Renderer {
    /** all document part */
    public static final int ALL_PART = 0;
    /** link document part */
    public static final int LINK_PART = 1;
    /** form document part */
    public static final int FORM_PART = 2;
    
    /**
     * Renders all the document
     * @param doc
     */
    public void render(SimpleDocument doc, Target target) throws RenderingException;
    /**
     * Renders the part of the current document
     * @param doc
     */
    public void render(int documentPart) throws RenderingException;
    /**
     * Renders a part of the document only
     * @param doc
     * @param documentPart 
     */
    public void render(SimpleDocument doc, Target target, int documentPart) throws RenderingException;
    /**
     * Returns the document rendered.
     * @return
     */
    public SimpleDocument getDocument();
    /**
     * 
     * @return true if the renderer has an active document, false otherwise
     */
    public boolean isActive();
}
