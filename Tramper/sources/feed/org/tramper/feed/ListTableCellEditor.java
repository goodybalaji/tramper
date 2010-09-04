package org.tramper.feed;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellEditor;

import org.tramper.doc.Library;
import org.tramper.doc.Link;
import org.tramper.doc.Sound;
import org.tramper.doc.SimpleDocument;
import org.tramper.doc.Target;
import org.tramper.loader.Loader;
import org.tramper.loader.LoaderFactory;

/**
 * Table cell editor permiting to cilck on a link/media to launch it.
 * @author Paul-Emile
 */
public class ListTableCellEditor extends JList implements TableCellEditor, MouseListener {
    /** ListTableCellEditor.java long */
    private static final long serialVersionUID = 2584856621102771772L;
    /** cell editor listeners */
    protected List<CellEditorListener> cellEditorListener;
    /** original value */
    protected List<Object> links;
    
    /**
     * 
     */
    public ListTableCellEditor() {
	cellEditorListener = new ArrayList<CellEditorListener>();
        ListCellRenderer cellRenderer = new LinkListCellRenderer();
        this.setCellRenderer(cellRenderer);
        this.addMouseListener(this);
    }

    /**
     * @see javax.swing.table.TableCellEditor#getTableCellEditorComponent(javax.swing.JTable, java.lang.Object, boolean, int, int)
     */
    @SuppressWarnings("unchecked")
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
	links = (List<Object>)value;
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

    /**
     * @see javax.swing.CellEditor#cancelCellEditing()
     */
    public void cancelCellEditing() {
	fireEditingCanceled();
    }

    /**
     * @see javax.swing.CellEditor#getCellEditorValue()
     */
    public Object getCellEditorValue() {
	return links;
    }

    /**
     * @see javax.swing.CellEditor#isCellEditable(java.util.EventObject)
     */
    public boolean isCellEditable(EventObject anEvent) {
	return true;
    }

    /**
     * @see javax.swing.CellEditor#addCellEditorListener(javax.swing.event.CellEditorListener)
     */
    public void addCellEditorListener(CellEditorListener l) {
	if (!cellEditorListener.contains(l)) {
	    cellEditorListener.add(l);
	}
    }

    /**
     * @see javax.swing.CellEditor#removeCellEditorListener(javax.swing.event.CellEditorListener)
     */
    public void removeCellEditorListener(CellEditorListener l) {
	cellEditorListener.remove(l);
    }

    /**
     * @see javax.swing.CellEditor#shouldSelectCell(java.util.EventObject)
     */
    public boolean shouldSelectCell(EventObject anEvent) {
	return true;
    }

    /**
     * @see javax.swing.CellEditor#stopCellEditing()
     */
    public boolean stopCellEditing() {
	fireEditingStopped();
	return true;
    }
    
    /**
     * Fire the editing cancel event
     */
    protected void fireEditingCanceled() {
        ChangeEvent ce = new ChangeEvent(this);
        for (int i = cellEditorListener.size() - 1; i >= 0; i--) {
            cellEditorListener.get(i).editingCanceled(ce);
        }
    }
    
    /**
     * Fire the editing stopped event
     */
    protected void fireEditingStopped() {
        ChangeEvent ce = new ChangeEvent(this);
        for (int i = cellEditorListener.size() - 1; i >= 0; i--) {
            cellEditorListener.get(i).editingStopped(ce);
        }
    }
    
    /**
     * When mouse clicked twice or more, load the document linked or media
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    public void mouseClicked(MouseEvent e) {
	int clickCount = e.getClickCount();
	int button = e.getButton();
	if (clickCount >= 2 && button == MouseEvent.BUTTON1) {
	    Object selected = getSelectedValue();
	    if (selected != null) {
                URL url = null;
                if (selected instanceof Link) {
                    SimpleDocument linkedDoc = ((Link)selected).getLinkedDocument();
                    url = linkedDoc.getUrl();
                } else {
                    Sound media = (Sound)selected;
                    url = media.getUrl();
                }
                Loader loader = LoaderFactory.getInstance().newLoader();
                loader.download(url.toString(), new Target(Library.PRIMARY_FRAME, null));
	    }
	}
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }
}
