package org.fingon;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.net.URL;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JToggleButton;
import javax.swing.UIManager;
import javax.swing.plaf.ButtonUI;
import javax.swing.plaf.ComponentUI;

import org.tramper.audio.SoundPlayer;
import org.tramper.player.PlayException;
import org.tramper.player.PlayerFactory;
import org.tramper.synthesizer.SpeechSynthesizer;

/**
 * 
 * @author Paul-Emile
 */
public class FingonButtonUI extends ButtonUI implements ActionListener, FocusListener {
    /** the instance common to every component */
    private static FingonButtonUI instance;
    /** selected sound URL */
    private URL selectedSound;
    /** pressed sound URL */
    private URL pressedSound;
    /** unselected sound URL */
    private URL unselectedSound;

    /**
     * @see javax.swing.plaf.ComponentUI#installUI(javax.swing.JComponent)
     */
    @Override
    public void installUI(JComponent c) {
	AbstractButton button = (AbstractButton)c;
	button.addActionListener(this);
	button.addFocusListener(this);
	pressedSound = (URL)UIManager.get("Button.pressedSound");
	selectedSound = (URL)UIManager.get("ToggleButton.selectedSound");
	unselectedSound = (URL)UIManager.get("ToggleButton.unselectedSound");
    }

    /**
     * @see javax.swing.plaf.ComponentUI#uninstallUI(javax.swing.JComponent)
     */
    @Override
    public void uninstallUI(JComponent c) {
	AbstractButton button = (AbstractButton)c;
	button.removeActionListener(this);
	button.removeFocusListener(this);
    }

    /**
     * Returns the instance of UI
     * @param c
     * @return
     */
    public static ComponentUI createUI(JComponent c) {
	if (instance == null) {
	    instance = new FingonButtonUI();
	}
	return instance;
    }
    
    /**
     * @see javax.swing.plaf.ComponentUI#update(java.awt.Graphics, javax.swing.JComponent)
     */
    @Override
    public void update(Graphics g, JComponent c) {
	
    }

    public void focusGained(FocusEvent e) {
    }

    public void focusLost(FocusEvent e) {
    }

    public void actionPerformed(ActionEvent e) {
	AbstractButton componentGainingFocus = (AbstractButton)e.getSource();
	String text = componentGainingFocus.getText();
	boolean selected = componentGainingFocus.isSelected();
	if (text != null && !text.equals("")) {
            SpeechSynthesizer synthesizer = PlayerFactory.getSpeechSynthesizer();
            synthesizer.stop();
            synthesizer.play(text);
	} else {
            try {
        	SoundPlayer player = (SoundPlayer)PlayerFactory.getPlayerByExtension("wav");
        	if (componentGainingFocus instanceof JToggleButton) {
        	    if (selected) {
        		player.play(selectedSound);
        	    } else {
            	    	player.play(unselectedSound);
        	    }
        	} else {
        	    player.play(pressedSound);
        	}
            } catch (PlayException e1) {
            }
	}
    }
}
