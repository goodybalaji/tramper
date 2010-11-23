package org.tramper.gui;

import java.awt.Component;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.EnhancedIcon;
import javax.swing.UIManager.LookAndFeelInfo;

/**
 * 
 * @author Paul-Emile
 */
public class AppearenceListCellRenderer extends JLabel implements ListCellRenderer {
    /**  */
    private static final long serialVersionUID = -8997621181777077398L;

    /**
     * 
     */
    public AppearenceListCellRenderer() {
        super();
    }
    
    /**
     * 
     * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
     */
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        LookAndFeelInfo lafi = (LookAndFeelInfo)value;
        String lafName = lafi.getName();
        setText(lafName);
        
        URL url = null;
        if (lafName.equals("Windows")) {
            url = getClass().getResource("images/windows.png");
        } else if (lafName.equals("Windows Classic")) {
            url = getClass().getResource("images/windows.png");
        } else if (lafName.equals("Mac OS")) {
            url = getClass().getResource("images/macos.png");
        } else if (lafName.equals("Quaqua")) {
            url = getClass().getResource("images/macos.png");
        } else if (lafName.equals("CDE/Motif")) {
            url = getClass().getResource("images/motif.png");
        } else if (lafName.equals("GTK+")) {
            url = getClass().getResource("images/linux.png");
        } else if (lafName.equals("KDE/Liquid")) {
            url = getClass().getResource("images/kde.png");
        } else {
            url = getClass().getResource("images/java.png");
        }
        if (url != null) {
            Icon icon = new EnhancedIcon(url);
            setIcon(icon);
        } else {
            setIcon(null);
        }
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
