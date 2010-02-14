package org.tramper.gui;

import java.awt.Component;
import java.util.Locale;

import javax.speech.EngineModeDesc;
import javax.swing.JLabel;

import javax.swing.JList;
import javax.swing.ListCellRenderer;


/**
 * Display a label and an country icon for a synthesizer 
 * @author Paul-Emile
 */
public class EngineListCellRenderer extends JLabel implements ListCellRenderer {
    /** EngineListCellRenderer.java long */
    private static final long serialVersionUID = -4061292157392645826L;

    /**
     * 
     */
    public EngineListCellRenderer() {
        super();
    }

    /**
     * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
     */
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        if (value != null) {
            EngineModeDesc engine = (EngineModeDesc)value;
            Locale loc = engine.getLocale();
            String name = engine.getEngineName();
            String mode = engine.getModeName();
            if (name.indexOf(mode) != -1) {
                setText(name);
            } else if (mode.indexOf(name) != -1) {
                setText(mode);
            } else {
                setText(name + " " + mode);
            }
            setIcon(IconFactory.getFlagIconByLocale(loc));
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
