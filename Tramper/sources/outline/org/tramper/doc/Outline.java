package org.tramper.doc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;

import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 * An outline.
 * @author Paul-Emile
 */
public class Outline extends MarkupDocument implements TreeModel, TreeExpansionListener, Serializable {
    /** Outline.java long */
    private static final long serialVersionUID = 4086509332353444267L;
    /** modification date */
    private Date modificationDate;
    /** tree model listeners */
    private List<TreeModelListener> treeModelListener;
    /** virtual root item */
    private OutlineItem rootItem;
    /** expanded node indices */
    private NavigableSet<Integer> expandedNodeIndices;
    
    /**
     * 
     */
    public Outline() {
        treeModelListener = new ArrayList<TreeModelListener>();
        links = new ArrayList<Link>();
        items = new ArrayList<DocumentItem>();
        expandedNodeIndices = new TreeSet<Integer>();
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
     * 
     * @param e
     */
    protected void fireTreeNodesInserted(TreeModelEvent e) {
	for (TreeModelListener aListener : treeModelListener) {
	    aListener.treeNodesInserted(e);
	}
    }

    /**
     * 
     * @param e
     */
    protected void fireTreeNodesRemoved(TreeModelEvent e) {
	for (TreeModelListener aListener : treeModelListener) {
	    aListener.treeNodesRemoved(e);
	}
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
     * Adds the index of a node of the first level (children of the root) 
     * to expand in the outline. 
     * @param nodeIndex an index of node, child of the root 
     */
    public void addExpandedNodeIndex(Integer nodeIndex) {
	expandedNodeIndices.add(nodeIndex);
    }
    
    /**
     * Removes the index of a node of the first level (children of the root) 
     * from the list of nodes to expand.
     * @param nodeIndex an index of node, child of the root 
     */
    public void removeExpandedNodeIndex(Integer nodeIndex) {
	expandedNodeIndices.remove(nodeIndex);
    }
    
    /**
     * Returns the node indices to expand in descending order.
     * @return 
     */
    public Iterator<Integer> getDescendingExpandedNodeIndices() {
	return expandedNodeIndices.descendingIterator();
    }

    /**
     * Returns the node indices to expand in ascending order.
     * @return 
     */
    public Iterator<Integer> getAscendingExpandedNodeIndices() {
	return expandedNodeIndices.iterator();
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

    /**
     * 
     * @see javax.swing.event.TreeExpansionListener#treeCollapsed(javax.swing.event.TreeExpansionEvent)
     */
    public void treeCollapsed(TreeExpansionEvent event) {
	TreePath collapsedPath = event.getPath();
	Object[] path = collapsedPath.getPath();
	if (path.length == 2) {
	    int collapsedIndex = rootItem.getIndex((TreeNode)path[1]);
	    removeExpandedNodeIndex(collapsedIndex);
	}
    }

    /**
     * 
     * @see javax.swing.event.TreeExpansionListener#treeExpanded(javax.swing.event.TreeExpansionEvent)
     */
    public void treeExpanded(TreeExpansionEvent event) {
	TreePath expandedPath = event.getPath();
	Object[] path = expandedPath.getPath();
	if (path.length == 2) {
	    int expandedIndex = rootItem.getIndex((TreeNode)path[1]);
	    addExpandedNodeIndex(expandedIndex);
	}
    }
}
