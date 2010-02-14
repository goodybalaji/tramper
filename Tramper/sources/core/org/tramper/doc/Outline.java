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
     * @param root the tree root 
     */
    public void setRoot(OutlineItem root) {
        this.rootItem = root;
    }

    /**
     * 
     * @see javax.swing.tree.TreeModel#addTreeModelListener(javax.swing.event.TreeModelListener)
     */
    public void addTreeModelListener(TreeModelListener listener) {
        if (treeModelListener.contains(listener) == false) {
            treeModelListener.add(listener);
        }
    }

    /**
     * 
     * @see javax.swing.tree.TreeModel#removeTreeModelListener(javax.swing.event.TreeModelListener)
     */
    public void removeTreeModelListener(TreeModelListener listener) {
        treeModelListener.remove(listener);
    }

    /**
     * 
     * @see javax.swing.tree.TreeModel#getChild(java.lang.Object, int)
     */
    public Object getChild(Object parent, int index) {
        return ((OutlineItem)parent).getChildAt(index);
    }

    /**
     * 
     * @see javax.swing.tree.TreeModel#getChildCount(java.lang.Object)
     */
    public int getChildCount(Object parent) {
        return ((OutlineItem)parent).getChildCount();
    }

    /**
     * 
     * @see javax.swing.tree.TreeModel#getIndexOfChild(java.lang.Object, java.lang.Object)
     */
    public int getIndexOfChild(Object parent, Object child) {
        return ((OutlineItem)parent).getIndex((OutlineItem)child);
    }

    /**
     * 
     * @see javax.swing.tree.TreeModel#getRoot()
     */
    public Object getRoot() {
        return rootItem;
    }

    /**
     * 
     * @see javax.swing.tree.TreeModel#isLeaf(java.lang.Object)
     */
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
}
