package org.tramper.gui;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.EnhancedIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.tramper.browser.SearchEngine;


/**
 * render a search engine in a combobox
 * @author Paul-Emile
 */
public class SearchEngineListCellRenderer extends JLabel implements ListCellRenderer {
    /** SearchEngineListCellRenderer.java long */
    private static final long serialVersionUID = 7336056200020668947L;

    /**
     * 
     */
    public SearchEngineListCellRenderer() {
        super();
    }

    /**
     * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
     */
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        SearchEngine engine = (SearchEngine)value;
        String logo = engine.getLogo();
        Icon icon = new EnhancedIcon(getClass().getResource(logo));
        String text = engine.getName();
        setIcon(icon);
        setText(text);

        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        setEnabled(list.isEnabled());
        setFont(list.getFont());
        setOpaque(true);
        return this;
    }

}
