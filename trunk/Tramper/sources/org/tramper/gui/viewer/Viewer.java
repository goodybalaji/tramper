package org.tramper.gui.viewer;

import java.awt.Component;

import org.tramper.player.PlayListener;
import org.tramper.ui.Renderer;
import org.tramper.doc.Target;
import org.tramper.doc.TextDocument;


/**
 * Document viewer
 * @author Paul-Emile
 */
public interface Viewer extends Renderer, PlayListener {
    /**
     *
     */
    public void relocalize();
    /**
     * 
     */
    public void setActive(boolean active);
    /**
     * 
     */
    public Body getBody();
    /**
     * 
     * @param body
     */
    public void setBody(Component body);
    /**
     * 
     * @return
     */
    public Target getTarget();
    /**
     * 
     * @param document
     * @return
     */
    public boolean canRender(TextDocument aDocument);
}
