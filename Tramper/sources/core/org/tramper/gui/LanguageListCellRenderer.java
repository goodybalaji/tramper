package org.tramper.gui;

import java.awt.Component;
import java.util.Locale;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class LanguageListCellRenderer extends JLabel implements ListCellRenderer {
    /**  */
    private static final long serialVersionUID = 7113737911264603421L;
    
    public LanguageListCellRenderer() {
        super();
    }
    
    /**
     * Display the readable locale description and the flag of the language
     * @param list
     * @param value
     * @param index
     * @param isSelected
     * @param cellHasFocus
     * @return
     */
    public Component getListCellRendererComponent(
          JList list,
          Object value,            // value to display
          int index,               // cell index
          boolean isSelected,      // is the cell selected
          boolean cellHasFocus)    // the list and the cell have the focus
    {
        Locale loc = (Locale)value;
        String s = loc.getDisplayName();
        setText(s);
        
        setIcon(IconFactory.getFlagIconByLocale(loc));
        
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
