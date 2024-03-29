package org.tramper.doc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import javax.swing.event.TreeModelEvent;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 * An outline item
 * @author Paul-Emile
 */
public class OutlineItem extends DocumentItem implements TreeNode, Comparable<OutlineItem> {
    /** title */
    private String title;
    /** the children nodes */
    private List<OutlineItem> children;
    /** the parent node */
    private OutlineItem parent;
    /** tree to notify when a node has been added or removed */
    private Outline tree;
    
    /**
     * 
     */
    public OutlineItem(Outline tree) {
	this.tree = tree;
        children = new ArrayList<OutlineItem>();
        links = new ArrayList<Link>();
    }

    /**
     * @return tree.
     */
    public Outline getTree() {
        return this.tree;
    }

    /**
     * @param tree tree 
     */
    public void setTree(Outline tree) {
        this.tree = tree;
    }

    /**
     * Adds a new child to the list of children.
     * @param newChild a node to add as child of this node
     */
    public void addChild(OutlineItem newChild) {
	if (newChild != null && !children.contains(newChild)) {
	    children.add(newChild);
	    newChild.setParent(this);
	    Collections.sort(children);
	    
	    TreePath treePath = buildTreePath();
	    int[] addedIndices = {this.getIndex(newChild)};
	    Object[] addedChildren = {newChild};
	    TreeModelEvent treeEvent = new TreeModelEvent(tree, treePath, addedIndices, addedChildren);
	    tree.fireTreeNodesInserted(treeEvent);
	}
    }

    /**
     * Removes a child from the list of children.
     * @param oldChild a node to remove from the children of this node
     */
    public void removeChild(OutlineItem oldChild) {
	if (oldChild != null && children.contains(oldChild)) {
	    int oldChildIndex = this.getIndex(oldChild);
            children.remove(oldChild);
            oldChild.setParent(null);
            
            TreePath treePath = buildTreePath();
	    int[] removedIndices = {oldChildIndex};
	    Object[] removedChildren = {oldChild};
            TreeModelEvent treeEvent = new TreeModelEvent(tree, treePath, removedIndices, removedChildren);
            tree.fireTreeNodesRemoved(treeEvent);
	}
    }

    /**
     * 
     * @return
     */
    private TreePath buildTreePath() {
        List<TreeNode> listPath = new ArrayList<TreeNode>();
        listPath.add(0, this);
        TreeNode aParent = parent;
        while (aParent != null) {
            listPath.add(0, aParent);
            aParent = aParent.getParent();
        }
        TreeNode[] path = listPath.toArray(new TreeNode[listPath.size()]);
        return new TreePath(path);
    }
    
    /**
     * 
     * @return
     */
    public List<OutlineItem> getChildren() {
        return children;
    }

    /**
     * 
     * @see org.tramper.doc.SpeakableItem#getText(java.util.Locale)
     */
    public String getText(Locale locale) {
	StringBuffer text = new StringBuffer();
	text.append(title);
	text.append(".\n");

        for (int i=0; i<links.size(); i++) {
            Link aLink = links.get(i);
            String linkText = aLink.getText(locale);
            text.append(linkText);
            text.append(".\n");
        }

        return text.toString();
    }

    /**
     * @return title.
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * @param title title 
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 
     * @see javax.swing.tree.TreeNode#getParent()
     */
    public TreeNode getParent() {
        return this.parent;
    }
    
    /**
     * @param parent the parent 
     */
    public void setParent(OutlineItem parent) {
	if (parent != this.parent) {
	    if (this.parent != null) {
		this.parent.removeChild(this);
	    }
	    this.parent = parent;
	    if (parent != null) {
		parent.addChild(this);
	    }
	}
    }

    /**
     * 
     * @see javax.swing.tree.TreeNode#children()
     */
    public Enumeration<OutlineItem> children() {
        Vector<OutlineItem> vector = new Vector<OutlineItem>(children);
        return vector.elements();
    }

    /**
     * 
     * @see javax.swing.tree.TreeNode#getAllowsChildren()
     */
    public boolean getAllowsChildren() {
        return true;
    }

    /**
     * 
     * @see javax.swing.tree.TreeNode#getChildAt(int)
     */
    public TreeNode getChildAt(int childIndex) {
        return (TreeNode)children.get(childIndex);
    }

    /**
     * 
     * @see javax.swing.tree.TreeNode#getChildCount()
     */
    public int getChildCount() {
        return children.size();
    }

    /**
     * 
     * @see javax.swing.tree.TreeNode#getIndex(javax.swing.tree.TreeNode)
     */
    public int getIndex(TreeNode node) {
        return children.indexOf(node);
    }
    
    /**
     * 
     * @see javax.swing.tree.TreeNode#isLeaf()
     */
    public boolean isLeaf() {
        return (children.isEmpty());
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return title;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
	if (title != null) {
	    return title.hashCode();
	} else {
	    return super.hashCode();
	}
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
	if (!(obj instanceof OutlineItem)) {
	    return false;
	}
	String titleToCompare = ((OutlineItem)obj).getTitle();
	if (title == null) {
	    return false;
	}
	if (titleToCompare == null) {
	    return false;
	}
	return title.equals(titleToCompare);
    }

    /**
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(OutlineItem o) {
	if (o == null) {
	    return -1;
	}
	String titleToCompare = o.getTitle();
	if (titleToCompare == null) {
	    return -1;
	}
	if (title == null) {
	    return 1;
	}
	return title.compareTo(titleToCompare);
    }
}
