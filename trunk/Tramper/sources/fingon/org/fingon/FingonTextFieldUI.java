package org.fingon;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JComponent;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTextUI;
import javax.swing.text.JTextComponent;

import org.tramper.player.PlayerFactory;
import org.tramper.synthesizer.SpeechSynthesizer;

/**
 * 
 * @author Paul-Emile
 */
public class FingonTextFieldUI extends BasicTextUI implements KeyListener, CaretListener {
    protected String typedString = "";
    
    /**
     * 
     */
    public FingonTextFieldUI() {
	super();
    }

    /**
     * Returns the instance of UI
     * @param c
     * @return
     */
    public static ComponentUI createUI(JComponent c) {
	return new FingonTextFieldUI();
    }
    
    /**
     * @see javax.swing.plaf.ComponentUI#installUI(javax.swing.JComponent)
     */
    @Override
    public void installUI(JComponent c) {
	super.installUI(c);
	JTextComponent textc = (JTextComponent)c;
	textc.addKeyListener(this);
	textc.addCaretListener(this);
    }
    
    /**
     * @see javax.swing.plaf.ComponentUI#uninstallUI(javax.swing.JComponent)
     */
    @Override
    public void uninstallUI(JComponent c) {
	super.uninstallUI(c);
	JTextComponent textc = (JTextComponent)c;
	textc.removeKeyListener(this);
	textc.removeCaretListener(this);
    }

    /**
     * Do not clear the component view if it is opaque.
     * @see javax.swing.plaf.ComponentUI#update(java.awt.Graphics, javax.swing.JComponent)
     */
    @Override
    public void update(Graphics g, JComponent c) {
	
    }

    public void caretUpdate(CaretEvent e) {
	int dot = e.getDot();
	int mark = e.getMark();
	if (mark != dot) {
	    JTextComponent textComponent = (JTextComponent)e.getSource();
	    String selectedText = textComponent.getSelectedText();
    	    SpeechSynthesizer synthesizer = PlayerFactory.getSpeechSynthesizer();
    	    if (selectedText != null) {
        	synthesizer.stop();
                synthesizer.play(selectedText);
    	    }
	}
    }

    public void keyPressed(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
    }

    public void keyTyped(KeyEvent e) {
	char typedChar = e.getKeyChar();
	typedString = typedString + typedChar;
	if (typedChar == ' ') {
	    SpeechSynthesizer synthesizer = PlayerFactory.getSpeechSynthesizer();
	    synthesizer.stop();
	    synthesizer.play(typedString);
	    typedString = "";
	}
    }
    
    @Override
    protected String getPropertyPrefix() {
	return "TextField";
    }
}
