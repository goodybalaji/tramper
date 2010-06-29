package org.fingon;

import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.JToolTip;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.ToolTipUI;

import org.tramper.player.PlayerFactory;
import org.tramper.synthesizer.SpeechSynthesizer;

/**
 * @author Paul-Emile
 * 
 */
public class FingonToolTipUI extends ToolTipUI implements AncestorListener {
    /** the instance common to every component */
    private static FingonToolTipUI instance;

    /**
     * Returns the instance of UI
     * @param c
     * @return
     */
    public static ComponentUI createUI(JComponent c) {
	if (instance == null) {
	    instance = new FingonToolTipUI();
	}
	return instance;
    }
    
    /**
     * @see javax.swing.plaf.ComponentUI#installUI(javax.swing.JComponent)
     */
    @Override
    public void installUI(JComponent c) {
	JToolTip tooltip = (JToolTip)c;
	tooltip.addAncestorListener(this);
    }

    /**
     * @see javax.swing.plaf.ComponentUI#uninstallUI(javax.swing.JComponent)
     */
    @Override
    public void uninstallUI(JComponent c) {
	JToolTip tooltip = (JToolTip)c;
	tooltip.removeAncestorListener(this);
    }

    /**
     * @see javax.swing.plaf.ComponentUI#update(java.awt.Graphics, javax.swing.JComponent)
     */
    @Override
    public void update(Graphics g, JComponent c) {
    }

    public void ancestorAdded(AncestorEvent event) {
	String tipText = ((JToolTip)event.getComponent()).getTipText();
	if (tipText != null) {
	    SpeechSynthesizer synthesizer = PlayerFactory.getSpeechSynthesizer();
	    synthesizer.stop();
	    synthesizer.play(tipText);
	}
    }

    public void ancestorMoved(AncestorEvent event) {
    }

    public void ancestorRemoved(AncestorEvent event) {
    }
}
