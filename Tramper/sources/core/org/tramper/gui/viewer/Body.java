package org.tramper.gui.viewer;

import java.awt.Dimension;
import java.awt.Graphics2D;

import org.tramper.doc.SimpleDocument;
import org.tramper.doc.Target;

/**
 * 
 * @author Paul-Emile
 */
public interface Body {
    /**
     * 
     */
    public void first();
    /**
     * 
     */
    public void next();
    /**
     * 
     */
    public void previous();
    /**
     * 
     */
    public void last();
    /**
     * 
     * @param doc
     * @param target
     * @param documentPart
     */
    public void displayDocument(SimpleDocument doc, Target target, int documentPart);
    /**
     * 
     * @param g
     */
    public void paintMiniature(Graphics2D g2d, Dimension miniatureSize, boolean mouseOver);
}
