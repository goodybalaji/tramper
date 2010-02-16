package org.tramper.doc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 * A feed
 * @author Paul-Emile
 */
public class Feed extends MarkupDocument implements Serializable, TableModel, ListModel {
    /** Feed.java long */
    private static final long serialVersionUID = -8203372989217187432L;
    /** category */
    private String category;
    /** Last build date */
    private Date lastBuildDate;
    /** table model listeners */
    private List<TableModelListener> tableModelListener;
    /** list data model listener */
    private List<ListDataListener> listModelListener;

    /**
     * 
     */
    public Feed() {
        super();
        links = new ArrayList<Link>();
        items = new ArrayList<DocumentItem>();
        tableModelListener = new ArrayList<TableModelListener>();
        listModelListener = new ArrayList<ListDataListener>();
    }

    /**
     * add a new item to the list
     * @param newItem
     */
    public void addItem(FeedItem newItem) {
        items.add(newItem);
        ListDataEvent listEvent = new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, items.size(), items.size());
        fireIntervalAddedEvent(listEvent);
        TableModelEvent tableEvent = new TableModelEvent(this, items.size(), items.size(), TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT);
        fireTableChangedEvent(tableEvent);
    }

    /**
     * add a new item to the list at the first place
     * @param newItem
     * @param index 
     */
    public void addItem(FeedItem newItem, int index) {
        items.add(index, newItem);
        ListDataEvent listEvent = new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, index, index);
        fireIntervalAddedEvent(listEvent);
        TableModelEvent tableEvent = new TableModelEvent(this, index, index, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT);
        fireTableChangedEvent(tableEvent);
    }

    /**
     * 
     * @param event
     */
    private void fireIntervalAddedEvent(ListDataEvent event) {
	for (ListDataListener listener : listModelListener) {
	    listener.intervalAdded(event);
	}
    }

    /**
     * remove an item from the list
     * @param i the index of the item
     */
    public void removeItem(int i) {
        items.remove(i);
        ListDataEvent event = new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, i, i);
        fireIntervalRemovedEvent(event);
        TableModelEvent tableEvent = new TableModelEvent(this, i, i, TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE);
        fireTableChangedEvent(tableEvent);
    }
    
    /**
     * 
     * @param tableEvent
     */
    private void fireTableChangedEvent(TableModelEvent tableEvent) {
	for (TableModelListener listener : tableModelListener) {
	    listener.tableChanged(tableEvent);
	}
    }

    /**
     * 
     * @param event
     */
    private void fireIntervalRemovedEvent(ListDataEvent event) {
	for (ListDataListener listener : listModelListener) {
	    listener.intervalRemoved(event);
	}
    }

    /**
     * return an item
     * @param i index of the item
     * @return
     */
    public FeedItem getItem(int i) {
        return (FeedItem)items.get(i);
    }
    
    /**
     * @return category.
     */
    public String getCategory() {
        return this.category;
    }

    /**
     * @param category category 
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * @return lastBuildDate.
     */
    public Date getLastBuildDate() {
        return this.lastBuildDate;
    }

    /**
     * @param lastBuildDate lastBuildDate 
     */
    public void setLastBuildDate(Date lastBuildDate) {
        this.lastBuildDate = lastBuildDate;
    }

    /**
     * parse a string to instantiate a locale
     * @param lang
     */
    public void parseLanguage(String lang) {
        if (lang.length() == 2) {
            language = new Locale(lang);
        } else if (lang.length() == 5) {
            String[] localePart = new String[0];
            if (lang.indexOf("-") != -1) {
                localePart = lang.split("-");
            } else if (lang.indexOf("_") != -1) {
                localePart = lang.split("_");
            }
            
            if (localePart.length == 2) {
                language = new Locale(localePart[0], localePart[1]);
            }
        }
    }
    
    /**
     * 
     * @see javax.swing.table.TableModel#addTableModelListener(javax.swing.event.TableModelListener)
     */
    public void addTableModelListener(TableModelListener l) {
        if (!tableModelListener.contains(l)) {
            tableModelListener.add(l);
        }
    }

    /**
     * 
     * @see javax.swing.table.TableModel#removeTableModelListener(javax.swing.event.TableModelListener)
     */
    public void removeTableModelListener(TableModelListener l) {
        tableModelListener.remove(l);
    }

    /**
     * 
     * @see javax.swing.table.TableModel#getColumnClass(int)
     */
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 0)
            return String.class;
        else if (columnIndex == 1)
            return String.class;
        else if (columnIndex == 2)
            return List.class;
        else if (columnIndex == 3)
            return List.class;
        else if (columnIndex == 4)
            return Date.class;
        else if (columnIndex == 5)
            return Date.class;
        else
            return null;
    }

    /**
     * 
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    public int getColumnCount() {
        return 6;
    }

    /**
     * 
     * @see javax.swing.table.TableModel#getColumnName(int)
     */
    public String getColumnName(int columnIndex) {
        ResourceBundle label = ResourceBundle.getBundle("label");
        if (columnIndex == 0)
            return label.getString("javaspeaker.title");
        else if (columnIndex == 1)
            return label.getString("javaspeaker.category");
        else if (columnIndex == 2)
            return label.getString("javaspeaker.links");
        else if (columnIndex == 3)
            return label.getString("javaspeaker.media");
        else if (columnIndex == 4)
            return label.getString("javaspeaker.item.publication");
        else if (columnIndex == 5)
            return label.getString("javaspeaker.item.modification");
        else
            return "";
    }

    /**
     * 
     * @see javax.swing.table.TableModel#getRowCount()
     */
    public int getRowCount() {
        return items.size();
    }

    /**
     * 
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
        FeedItem item = (FeedItem)items.get(rowIndex);
        if (columnIndex == 0)
            return item.getTitle();
        else if (columnIndex == 1)
            return item.getCategory();
        else if (columnIndex == 2)
            return item.getLinks();
        else if (columnIndex == 3)
            return item.getMedia();
        else if (columnIndex == 4)
            return item.getPublicationDate();
        else if (columnIndex == 5)
            return item.getUpdateDate();
        else
            return null;
    }

    /**
     * Columns 3 and 4 (links and media) are editable in JTable
     * @see javax.swing.table.TableModel#isCellEditable(int, int)
     */
    public boolean isCellEditable(int rowIndex, int columnIndex) {
	if (columnIndex == 2 || columnIndex == 3) {
	    return true;
	} else {
	    return false;
	}
    }

    /**
     * 
     * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
     */
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        FeedItem item = (FeedItem)items.get(rowIndex);
        if (columnIndex == 0)
            item.setTitle((String)aValue);
        else if (columnIndex == 1)
            item.setCategory((String)aValue);
        else if (columnIndex == 2)
            item.setLink((List<Link>)aValue);
        else if (columnIndex == 3)
            item.setMedia((List<Sound>)aValue);
        else if (columnIndex == 4)
            item.setPublicationDate((Date)aValue);
        else if (columnIndex == 5)
            item.setUpdateDate((Date)aValue);
    }

    /**
     * 
     * @see javax.swing.ListModel#addListDataListener(javax.swing.event.ListDataListener)
     */
    public void addListDataListener(ListDataListener l) {
        if (!listModelListener.contains(l)) {
            listModelListener.add(l);
        }
    }

    /**
     * 
     * @see javax.swing.ListModel#getElementAt(int)
     */
    public Object getElementAt(int index) {
	return items.get(index);
    }

    /**
     * 
     * @see javax.swing.ListModel#getSize()
     */
    public int getSize() {
	return items.size();
    }

    /**
     * 
     * @see javax.swing.ListModel#removeListDataListener(javax.swing.event.ListDataListener)
     */
    public void removeListDataListener(ListDataListener l) {
        listModelListener.remove(l);
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return "feed";
    }
}
