package org.tramper.gui;

import java.awt.Component;

import javax.speech.recognition.SpeakerProfile;
import javax.swing.EnhancedIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * SpeechSynthesizer profile renderer in a list
 * @author Paul-Emile
 */
public class SpeakerProfileListCellRenderer extends JLabel implements ListCellRenderer {
    /** SpeakerProfileListCellRenderer.java long */
    private static final long serialVersionUID = 4098462438868258341L;

    /**
     * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
     */
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        if (value != null) {
            SpeakerProfile speakerProfile = (SpeakerProfile)value;
            String name = speakerProfile.getName();
            String variant = speakerProfile.getVariant();
            String s = name;
            if (variant != null && !variant.equals(name))
                s = s + " " + variant;
            setText(s);
        }
        setIcon(new EnhancedIcon(getClass().getResource("images/man.png")));
        
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
