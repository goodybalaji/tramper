package org.tramper.gui;

import java.awt.Component;
import java.io.File;
import java.util.ResourceBundle;

import javax.swing.Icon;
import javax.swing.EnhancedIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * 
 * @author Paul-Emile
 */
public class OutputListCellRenderer extends JLabel implements ListCellRenderer {
    /**
     * OutputListCellRenderer.java long
     */
    private static final long serialVersionUID = -7392640074300840043L;
    /**
     * file
     */
    private File file;

    /**
     * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
     */
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
	ResourceBundle label = ResourceBundle.getBundle("label");
	
	String output = (String)value;
	StringBuffer text = new StringBuffer();
	Icon icon = null;
	if (output.equals("speaker")) {
	    text.append(label.getString("javaspeaker.speaker"));
	    icon = new EnhancedIcon(this.getClass().getResource("images/speaker.png"));
	} else if (output.equals("file")) {
	    text.append(label.getString("javaspeaker.menu.file"));
	    if (file != null) {
		text.append(" : ");
		text.append(file.getName());
	    }
	    icon = new EnhancedIcon(getClass().getResource("images/File.png"));
	}
	this.setText(text.toString());
	this.setIcon(icon);
	
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

    /**
     * @return file.
     */
    public File getFile() {
        return this.file;
    }

    /**
     * @param file file 
     */
    public void setFile(File file) {
        this.file = file;
    }

}
