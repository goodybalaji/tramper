package org.tramper.gui;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.tramper.synthesizer.VoiceDesc;


/**
 * @author Paul-Emile
 * 
 */
public class VoiceListCellRenderer extends JLabel implements ListCellRenderer {
    /** VoiceListCellRenderer.java long */
    private static final long serialVersionUID = 6069888711938049086L;

    /**
     * 
     */
    public VoiceListCellRenderer() {
        super();
    }

    /**
     * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
     */
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        if (value != null) {
            VoiceDesc voice = (VoiceDesc)value;
            String s = voice.toString();
            setText(s);
            setIcon(IconFactory.getIconByVoice(voice));
        }
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
