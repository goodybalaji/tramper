package org.tramper.gui;

import java.awt.Component;

import javax.sound.sampled.Mixer;
import javax.swing.EnhancedIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * @author Paul-Emile
 * 
 */
public class MicrophoneListCellRenderer extends JLabel implements ListCellRenderer {
    /**
     * MicrophoneListCellRenderer.java long
     */
    private static final long serialVersionUID = 8917741424836311008L;

    /**
     * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
     */
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
	Mixer.Info engine = (Mixer.Info)value;
        String name = engine.getName();
        setText(name);
        
        setIcon(new EnhancedIcon(this.getClass().getResource("images/microphone.png")));
        
        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        }
        else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        setEnabled(list.isEnabled());
        setFont(list.getFont());
        setOpaque(true);
        return this;
    }

}
