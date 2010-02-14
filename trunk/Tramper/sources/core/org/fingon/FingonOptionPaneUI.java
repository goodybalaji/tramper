package org.fingon;

import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.net.URL;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicOptionPaneUI;

import org.tramper.player.PlayException;
import org.tramper.player.PlayerFactory;
import org.tramper.audio.SoundPlayer;
import org.tramper.synthesizer.SpeechSynthesizer;

/**
 * 
 * @author Paul-Emile
 */
public class FingonOptionPaneUI extends BasicOptionPaneUI implements ComponentListener {
    /** the instance common to every component */
    private static FingonOptionPaneUI instance;

    /**
     * @see javax.swing.plaf.ComponentUI#installUI(javax.swing.JComponent)
     */
    @Override
    public void installUI(JComponent c) {
	JOptionPane option = (JOptionPane)c;
	option.addComponentListener(this);
    }

    /**
     * @see javax.swing.plaf.ComponentUI#uninstallUI(javax.swing.JComponent)
     */
    @Override
    public void uninstallUI(JComponent c) {
	JOptionPane option = (JOptionPane)c;
	option.removeComponentListener(this);
    }

    /**
     * Returns the instance of UI
     * @param c
     * @return
     */
    public static ComponentUI createUI(JComponent c) {
	if (instance == null) {
	    instance = new FingonOptionPaneUI();
	}
	return instance;
    }
    
    /**
     * @see javax.swing.plaf.ComponentUI#update(java.awt.Graphics, javax.swing.JComponent)
     */
    @Override
    public void update(Graphics g, JComponent c) {
    }

    public void componentHidden(ComponentEvent e) {
    }

    public void componentMoved(ComponentEvent e) {
    }

    public void componentResized(ComponentEvent e) {
    }

    public void componentShown(ComponentEvent e) {
	JOptionPane optionPane = (JOptionPane)e.getSource();

	String resourcePath = null;
        int messageType = optionPane.getMessageType();
        if (messageType == JOptionPane.ERROR_MESSAGE) {
            resourcePath = "/org/tramper/aui/sounds/Indigo.aiff";
        } else if (messageType == JOptionPane.INFORMATION_MESSAGE) {
            resourcePath = "/org/tramper/aui/sounds/Droplet.aiff";
        } else if (messageType == JOptionPane.PLAIN_MESSAGE) {
            resourcePath = "/org/tramper/aui/sounds/Droplet.aiff";
        } else if (messageType == JOptionPane.QUESTION_MESSAGE) {
            resourcePath = "/org/tramper/aui/sounds/Droplet.aiff";
        } else if (messageType == JOptionPane.WARNING_MESSAGE) {
            resourcePath = "/org/tramper/aui/sounds/Indigo.aiff";
        }
        URL soundUrl = getClass().getResource(resourcePath);
        try {
	    SoundPlayer player = (SoundPlayer)PlayerFactory.getPlayerByExtension("aiff");
	    player.play(soundUrl);
        } catch (PlayException e1) {}
        
	Object message = optionPane.getMessage();
        try {
            SpeechSynthesizer synthesizer = PlayerFactory.getSpeechSynthesizer();
            synthesizer.play(message.toString());
        } catch (PlayException ex) {}
    }
}
