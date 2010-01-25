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
        String s = lafi.getName();
        setText(s);
        
        URL url = null;
        if (lafi.getName().equals("Windows")) {
            url = getClass().getResource("images/windows.png");
        } else if (lafi.getName().equals("Windows Classic")) {
            url = getClass().getResource("images/windows.png");
        } else if (lafi.getName().equals("Mac OS")) {
            url = getClass().getResource("images/macos.png");
        } else if (lafi.getName().equals("Quaqua")) {
            url = getClass().getResource("images/macos.png");
        } else if (lafi.getName().equals("CDE/Motif")) {
            url = getClass().getResource("images/motif.png");
        } else if (lafi.getName().equals("GTK+")) {
            url = getClass().getResource("images/linux.png");
        } else if (lafi.getName().equals("KDE/Liquid")) {
            url = getClass().getResource("images/kde.png");
        } else if (lafi.getClassName().startsWith("org.jvnet.substance")) {
            url = getClass().getResource("images/substance.png");
        } else {
            url = getClass().getResource("images/java.png");
        }
        Icon icon = new EnhancedIcon(url);
        setIcon(icon);
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