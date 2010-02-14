package org.fingon;

import javax.swing.UIDefaults;

/**
 * 
 * @author Paul-Emile
 */
public class FingonUIDefaults extends UIDefaults {
    /**
     * FingonUIDefaults.java long
     */
    private static final long serialVersionUID = 1L;

    /**
     * Do not log an error because Fingon is an auxiliary look and feel which
     * doesn't provide a UI for every component.
     * @see javax.swing.UIDefaults#getUIError(java.lang.String)
     */
    @Override
    protected void getUIError(String msg) {
    }
}
