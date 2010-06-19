package org.fingon;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.ListUI;

import org.tramper.player.PlayerFactory;
import org.tramper.synthesizer.SpeechSynthesizer;

/**
 * Speaks the selected item(s) in the list.
 * @author Paul-Emile
 */
public class FingonListUI extends ListUI implements ListSelectionListener {
    /** the instance common to every component */
    private static FingonListUI instance;

    /**
     * Returns the instance of UI
     * @param c
     * @return
     */
    public static ComponentUI createUI(JComponent c) {
	if (instance == null) {
	    instance = new FingonListUI();
	}
	return instance;
    }
    
    /**
     * @see javax.swing.plaf.ComponentUI#installUI(javax.swing.JComponent)
     */
    @Override
    public void installUI(JComponent c) {
	JList list = (JList)c;
	list.addListSelectionListener(this);
    }

    /**
     * @see javax.swing.plaf.ComponentUI#uninstallUI(javax.swing.JComponent)
     */
    @Override
    public void uninstallUI(JComponent c) {
	JList list = (JList)c;
	list.removeListSelectionListener(this);
    }

    /**
     * @see javax.swing.plaf.ComponentUI#update(java.awt.Graphics, javax.swing.JComponent)
     */
    @Override
    public void update(Graphics g, JComponent c) {
    }

    @Override
    public Rectangle getCellBounds(JList list, int index1, int index2) {
	return new Rectangle(0, 0);
    }

    @Override
    public Point indexToLocation(JList list, int index) {
	return null;
    }

    @Override
    public int locationToIndex(JList list, Point location) {
	return 0;
    }

    /**
     * 
     * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
     */
    public void valueChanged(ListSelectionEvent e) {
	JList changedList = (JList)e.getSource();
	if (!e.getValueIsAdjusting()) {
	    Object[] selectedValues = changedList.getSelectedValues();
	    if (selectedValues != null && selectedValues.length > 0) {
		SpeechSynthesizer synthesizer = PlayerFactory.getSpeechSynthesizer();
		synthesizer.stop();
		for (Object value : selectedValues) {
		    synthesizer.play(value.toString());
        	}
	    }
	}
    }
}
