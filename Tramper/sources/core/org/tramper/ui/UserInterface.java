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
     * @param msgKey
     */
    public void raiseError(String msgKey);
    /**
     * 
     * @param msgKey
     */
    public void raiseWarning(String msgKey);
    /**
     * 
     * @param msgKey
     */
    public void raiseInfo(String msgKey);
    /**
     * 
     * @param msgKey
     * @return
     */
    public boolean confirmMessage(String msgKey);
    /**
     * 
     * @param msgKey
     * @param params
     * @return
     */
    public boolean confirmMessage(String msgKey, Object[] params);
    /**
     * 
     */
    public void unregister();
}
