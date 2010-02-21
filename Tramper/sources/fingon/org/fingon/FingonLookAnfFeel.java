package org.fingon;

import javax.swing.LookAndFeel;
import javax.swing.UIDefaults;

/**
 * 
 * @author Paul-Emile
 */
public class FingonLookAnfFeel extends LookAndFeel {
    /** UI defaults */
    private FingonUIDefaults uiDefaults;
    
    /**
     * 
     */
    public FingonLookAnfFeel() {
	uiDefaults = new FingonUIDefaults();
	uiDefaults.put("TextFieldUI", "org.fingon.FingonTextFieldUI");
	uiDefaults.put("TextAreaUI", "org.fingon.FingonTextAreaUI");
	uiDefaults.put("ButtonUI", "org.fingon.FingonButtonUI");
	uiDefaults.put("ToggleButtonUI", "org.fingon.FingonButtonUI");
	uiDefaults.put("ProgressBarUI", "org.fingon.FingonProgressBarUI");
	uiDefaults.put("OptionPaneUI", "org.fingon.FingonOptionPaneUI");
    }

    /**
     * @see javax.swing.LookAndFeel#getDescription()
     */
    @Override
    public String getDescription() {
	return "Fingon is an auxiliary look and feel which brings an audio look and feel to the default graphical look and feel";
    }

    /**
     * @see javax.swing.LookAndFeel#getID()
     */
    @Override
    public String getID() {
	return "Fingon";
    }

    /**
     * @see javax.swing.LookAndFeel#getName()
     */
    @Override
    public String getName() {
	return "Fingon";
    }

    /**
     * @see javax.swing.LookAndFeel#isNativeLookAndFeel()
     */
    @Override
    public boolean isNativeLookAndFeel() {
	return false;
    }

    /**
     * @see javax.swing.LookAndFeel#isSupportedLookAndFeel()
     */
    @Override
    public boolean isSupportedLookAndFeel() {
	return true;
    }

    /**
     * @see javax.swing.LookAndFeel#getDefaults()
     */
    @Override
    public UIDefaults getDefaults() {
	return uiDefaults;
    }
}
