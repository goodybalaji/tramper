package org.fingon;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.plaf.ButtonUI;
import javax.swing.plaf.ComponentUI;

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

    /**
     * @see javax.swing.plaf.ComponentUI#installUI(javax.swing.JComponent)
     */
    @Override
    public void installUI(JComponent c) {
	AbstractButton button = (AbstractButton)c;
	button.addActionListener(this);
	button.addFocusListener(this);
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
	if (text != null && !text.equals("")) {
            try {
                SpeechSynthesizer synthesizer = PlayerFactory.getSpeechSynthesizer();
                synthesizer.play(text);
            } catch (PlayException ex) {
            }
	}/* else {
            URL soundUrl = getClass().getResource("/org/tramper/aui/sounds/Droplet.aiff");
            try {
    	    	SoundPlayer player = (SoundPlayer)PlayerFactory.getPlayerByExtension("aiff");
    	    	player.play(soundUrl);
            } catch (PlayException e1) {
            }
	}*/
    }
}
