package org.tramper.gui;

import java.awt.Component;
import java.text.DateFormat;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * @author Paul-Emile
 * 
 */
public class DateTableCellRenderer extends JLabel implements TableCellRenderer {
    /** DateTableCellRenderer.java long */
    private static final long serialVersionUID = -8886522281482241862L;
    /** date formater */
    private DateFormat dateFormat;

    /**
     * 
     */
    public DateTableCellRenderer() {
        dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
    }
    
    /**
     * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
     */
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        this.setText("");
	if (value != null) {
            Date dateValue = (Date)value;
            String formatedDate = dateFormat.format(dateValue);
            this.setText(formatedDate);
	}
	
	setOpaque(true);
	
        if (isSelected) {
            this.setBackground(table.getSelectionBackground());
            this.setForeground(table.getSelectionForeground());
        }
        else {
            this.setBackground(table.getBackground());
            this.setForeground(table.getForeground());
        }
	return this;
    }
}
