package org.tramper.doc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * An outline
 * @author Paul-Emile
 */
public class Outline extends MarkupDocument implements TreeModel, Serializable {
    /** Outline.java long */
    private static final long serialVersionUID = 4086509332353444267L;
    /** modification date */
    private Date modificationDate;
    /** tree model listeners */
    private List<TreeModelListener> treeModelListener;
    /** virtual root item */
    private transient OutlineItem rootItem;
    
    /**
     * 
     */
    public Outline() {
        treeModelListener = new ArrayList<TreeModelListener>();
        links = new ArrayList<Link>();
        items = new ArrayList<DocumentItem>();
    }
    
    /**
     * Removes all items
     */
    public void removeItems() {
	items.clear();
    }
    
    /**
     * add a new item to the list
     * @param newItem
     */
    public void addItem(OutlineItem newItem) {
        items.add(newItem);
    }
    
    /**
     * @return modificationDate.
     */
    public Date getModificationDate() {
        return this.modificationDate;
    }

    /**
     * @param modificationDate modificationDate 
     */
    public void setModificationDate(Date modificationDate) {
        this.modificationDate = modificationDate;
    }

    /**
     * @return rootItem.
     */
    public OutlineItem getRootItem() {
        return this.rootItem;
    }

    /**
     * @param rootItem rootItem 
     */
    public void setRootItem(OutlineItem rootItem) {
        this.rootItem = rootItem;
    }

    public void addTreeModelListener(TreeModelListener listener) {
        if (treeModelListener.contains(listener) == false)
            treeModelListener.add(listener);
    }

    public void removeTreeModelListener(TreeModelListener listener) {
        treeModelListener.remove(listener);
    }

    public Object getChild(Object parent, int index) {
        return ((OutlineItem)parent).getChildAt(index);
    }

    public int getChildCount(Object parent) {
        return ((OutlineItem)parent).getChildCount();
    }

    public int getIndexOfChild(Object parent, Object child) {
        return ((OutlineItem)parent).getIndex((OutlineItem)child);
    }

    public Object getRoot() {
        return rootItem;
    }

    public boolean isLeaf(Object node) {
        return ((OutlineItem)node).isLeaf();
    }
    
    /**
     * 
     * @see javax.swing.tree.TreeModel#valueForPathChanged(javax.swing.tree.TreePath, java.lang.Object)
     */
    public void valueForPathChanged(TreePath path, Object newValue) {
        //not used at this moment
    }

    /**
     * the outline format doesn't specify an icon at this moment, return null
     * @see org.tramper.doc.MarkupDocument#getIcon()
     */
    public String getIcon() {
        return null;
    }
}
