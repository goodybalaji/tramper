package org.tramper.feed;

import java.awt.Component;
import java.util.List;

import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.table.TableCellRenderer;

/**
 * 
 * @author Paul-Emile
 */
public class ListTableCellRenderer extends JList implements TableCellRenderer {
    /** ListTableCellRenderer.java long */
    private static final long serialVersionUID = 3681475932833484797L;
    
    /**
     * 
     */
    public ListTableCellRenderer() {
        super();
        ListCellRenderer cellRenderer = new LinkListCellRenderer();
        this.setCellRenderer(cellRenderer);
    }
    
    /**
     * 
     * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
     */
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        List<Object> links = (List<Object>)value;
        if (links != null) {
            Object[] model = links.toArray(new Object[links.size()]);
            this.setVisibleRowCount(model.length);
            this.setListData(model);
        }
        
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
