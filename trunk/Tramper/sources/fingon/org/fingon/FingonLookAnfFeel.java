package org.fingon;

import javax.swing.LookAndFeel;
import javax.swing.UIDefaults;
import javax.swing.UIManager;

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
	
	UIManager.put("Button.pressedSound", getClass().getResource("Default.wav"));
	uiDefaults.put("ButtonUI", "org.fingon.FingonButtonUI");
	//uiDefaults.put("MenuItemUI", "org.fingon.FingonButtonUI");

	UIManager.put("ToggleButton.selectedSound", getClass().getResource("Connect.wav"));
	UIManager.put("ToggleButton.unselectedSound", getClass().getResource("Disconnect.wav"));
	uiDefaults.put("ToggleButtonUI", "org.fingon.FingonButtonUI");
	uiDefaults.put("RadioButtonUI", "org.fingon.FingonButtonUI");
	uiDefaults.put("CheckBoxUI", "org.fingon.FingonButtonUI");

	UIManager.put("ProgressBarUI.backgroundMusic", getClass().getResource("Le seigneur des anneaux - Le retour du roi[1]_chapter60-01.mp3"));
	uiDefaults.put("ProgressBarUI", "org.fingon.FingonProgressBarUI");

	UIManager.put("OptionPane.informationSound", getClass().getResource("Balloon.wav"));
	UIManager.put("OptionPane.questionSound", getClass().getResource("question.wav"));
	UIManager.put("OptionPane.warningSound", getClass().getResource("Exclamation.wav"));
	UIManager.put("OptionPane.errorSound", getClass().getResource("error.wav"));
	uiDefaults.put("OptionPaneUI", "org.fingon.FingonOptionPaneUI");
    }

    /**
     * @see javax.swing.LookAndFeel#getDescription()
     */
    @Override
    public String getDescription() {
	return "Fingon is an auxiliary look and feel which plugs a sound and feel in the current look and feel";
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
