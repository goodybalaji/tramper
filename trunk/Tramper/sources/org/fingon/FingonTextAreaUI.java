package org.fingon;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

public class FingonTextAreaUI extends FingonTextFieldUI {

    /**
     * 
     */
    public FingonTextAreaUI() {
	super();
    }

    /**
     * Returns the instance of UI
     * @param c
     * @return
     */
    public static ComponentUI createUI(JComponent c) {
	return new FingonTextAreaUI();
    }
    
    @Override
    protected String getPropertyPrefix() {
	return "TextArea";
    }
}
