package org.tramper.doc;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import javax.swing.tree.TreeNode;

/**
 * An outline item
 * @author Paul-Emile
 */
public class OutlineItem extends DocumentItem implements TreeNode {
    /** title */
    private String title;
    /** children items */
    private List<OutlineItem> children;
    /** father item */
    private OutlineItem father;
    
    /**
     * 
     */
    public OutlineItem() {
        children = new ArrayList<OutlineItem>();
        media = new ArrayList<Sound>();
        links = new ArrayList<Link>();
    }

    /**
     * add a new child to the list of children
     * @param newChild
     */
    public void addChild(OutlineItem newChild) {
        children.add(newChild);
    }

    /**
     * remove a child from the list of children
     * @param child
     */
    public void removeChild(OutlineItem child) {
        children.remove(child);
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
        return this.father;
    }
    
    /**
     * @param parent the parent 
     */
    public void setParent(OutlineItem parent) {
        this.father = parent;
    }

    /**
     * 
     * @see javax.swing.tree.TreeNode#children()
     */
    public Enumeration children() {
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
        return (children.size() == 0);
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return title;
    }
}
