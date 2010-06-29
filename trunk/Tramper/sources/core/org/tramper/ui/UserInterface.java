package org.tramper.ui;

import org.tramper.doc.SimpleDocument;
import org.tramper.doc.Target;

/**
 * @author Paul-Emile
 * 
 */
public interface UserInterface {
    /** default target */
    public static final Integer DEFAULT_TARGET = Integer.valueOf(0);
    /**
     * 
     * @param doc
     */
    public void renderDocument(SimpleDocument doc, Target target);
    /**
     * 
     * @return
     */
    public Renderer getActiveRenderer();
    /**
     * 
     */
    public void unregister();
}
