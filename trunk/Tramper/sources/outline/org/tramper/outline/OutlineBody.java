package org.tramper.outline;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.tramper.doc.Library;
import org.tramper.doc.Link;
import org.tramper.doc.Outline;
import org.tramper.doc.OutlineItem;
import org.tramper.doc.SimpleDocument;
import org.tramper.doc.Target;
import org.tramper.gui.viewer.Body;
import org.tramper.loader.Loader;
import org.tramper.loader.LoaderFactory;

/**
 * @author Paul-Emile
 * 
 */
public class OutlineBody extends JScrollPane implements Body, MouseListener, TreeSelectionListener {
    /** OutlineBody.java long */
    private static final long serialVersionUID = 1L;
    /** tree */
    private JTree outlineTree;
    /** target */
    private Target target;
    /** speakable document */
    private Outline document;

    public OutlineBody() {
	super();
        outlineTree = new JTree();
        outlineTree.setRootVisible(false);
        outlineTree.setEditable(false);
        outlineTree.setShowsRootHandles(true);
        outlineTree.setRowHeight(25);
        outlineTree.addMouseListener(this);
        outlineTree.addTreeSelectionListener(this);
        outlineTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        this.setViewportView(outlineTree);
    }
    
    /**
     * @see org.tramper.gui.viewer.Body#displayDocument(org.tramper.doc.SimpleDocument, org.tramper.doc.Target, int)
     */
    public void displayDocument(SimpleDocument doc, Target target, int documentPart) {
	if (!(doc instanceof Outline)) {
	    throw new RuntimeException(doc.getTitle()+" is not an outline");
	}
	document = (Outline)doc;
	this.target = target;
        OutlineItemTreeCellRenderer outlineRenderer = new OutlineItemTreeCellRenderer();
        outlineTree.setCellRenderer(outlineRenderer);
        outlineTree.setModel(document);
        long index = document.getIndex();
        selectItem((int)index);
    }
    
    /**
     * Selects a child in the list of selected node's parent's children.
     * @param index
     */
    public void selectItem(int index) {
        TreePath selectionPath = outlineTree.getSelectionPath();
        if (selectionPath != null) {
            TreePath parentPath = selectionPath.getParentPath();
            OutlineItem parent = (OutlineItem)parentPath.getLastPathComponent();
            Object lastChild = parent.getChildAt(index);
            TreePath lastChildPath = parentPath.pathByAddingChild(lastChild);
            outlineTree.setSelectionPath(lastChildPath);
            outlineTree.scrollPathToVisible(lastChildPath);
        }
    }
    
    /**
     * @see org.tramper.gui.viewer.Body#first()
     */
    public void first() {
        TreePath selectionPath = outlineTree.getSelectionPath();
        if (selectionPath != null) {
            TreePath parentPath = selectionPath.getParentPath();
            OutlineItem parent = (OutlineItem)parentPath.getLastPathComponent();
            Object firstChild = parent.getChildAt(0);
            TreePath firstChildPath = parentPath.pathByAddingChild(firstChild);
            outlineTree.setSelectionPath(firstChildPath);
            outlineTree.scrollPathToVisible(firstChildPath);
        }
    }
    
    /**
     * @see org.tramper.gui.viewer.Body#last()
     */
    public void last() {
        TreePath selectionPath = outlineTree.getSelectionPath();
        if (selectionPath != null) {
            TreePath parentPath = selectionPath.getParentPath();
            OutlineItem parent = (OutlineItem)parentPath.getLastPathComponent();
            int childCount = parent.getChildCount();
            Object lastChild = parent.getChildAt(childCount-1);
            TreePath lastChildPath = parentPath.pathByAddingChild(lastChild);
            outlineTree.setSelectionPath(lastChildPath);
            outlineTree.scrollPathToVisible(lastChildPath);
        }
    }
    
    /**
     * @see org.tramper.gui.viewer.Body#next()
     */
    public void next() {
        TreePath selectionPath = outlineTree.getSelectionPath();
        if (selectionPath != null) {
            OutlineItem selectedNode = (OutlineItem)selectionPath.getLastPathComponent();
            TreePath parentPath = selectionPath.getParentPath();
            OutlineItem parent = (OutlineItem)parentPath.getLastPathComponent();
            int nodeInChildrenIndex = parent.getIndex(selectedNode);
            int childCount = parent.getChildCount();
            if (nodeInChildrenIndex < childCount - 1) {
                Object nextChild = parent.getChildAt(nodeInChildrenIndex+1);
                TreePath nextChildPath = parentPath.pathByAddingChild(nextChild);
                outlineTree.setSelectionPath(nextChildPath);
                outlineTree.scrollPathToVisible(nextChildPath);
            }
        }
    }

    /**
     * @see org.tramper.gui.viewer.Body#previous()
     */
    public void previous() {
        TreePath selectionPath = outlineTree.getSelectionPath();
        if (selectionPath != null) {
            OutlineItem selectedNode = (OutlineItem)selectionPath.getLastPathComponent();
            TreePath parentPath = selectionPath.getParentPath();
            OutlineItem parent = (OutlineItem)parentPath.getLastPathComponent();
            int nodeInChildrenIndex = parent.getIndex(selectedNode);
            if (nodeInChildrenIndex > 0) {
                Object previousChild = parent.getChildAt(nodeInChildrenIndex-1);
                TreePath previousChildPath = parentPath.pathByAddingChild(previousChild);
                outlineTree.setSelectionPath(previousChildPath);
                outlineTree.scrollPathToVisible(previousChildPath);
            }
        }
    }

    /**
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    public void mouseClicked(MouseEvent event) {
    }
    
    /**
     * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
     */
    public void mouseEntered(MouseEvent arg0) {
    }
    
    /**
     * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
     */
    public void mouseExited(MouseEvent arg0) {
    }
    
    /**
     * Start loading the selected link
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    public void mousePressed(MouseEvent event) {
        int button = event.getButton();
        int clickCount = event.getClickCount();
        if (button == MouseEvent.BUTTON1) {
	    Library.getInstance().setActiveDocument(target);
            if (clickCount == 2) {
        	TreePath selectedPath = outlineTree.getSelectionPath();
        	if (selectedPath != null) {
        	    OutlineItem selectedItem = (OutlineItem)selectedPath.getLastPathComponent();
        	    List<Link> links = selectedItem.getLinks();
        	    for (Link link : links) {
        		if ("related".equals(link.getRelation())) {
        		    URL url = link.getLinkedDocument().getUrl();
        		    Loader loader = LoaderFactory.getLoader();
        		    loader.download(url.toString(), new Target(Library.PRIMARY_FRAME, null));
                	}
        	    }
        	}
            }
        }
    }
    
    /**
     * 
     * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    public void mouseReleased(MouseEvent event) {
    }

    /**
     * 
     * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
     */
    public void valueChanged(TreeSelectionEvent e) {
	TreePath leadPath = e.getNewLeadSelectionPath();
	if (leadPath != null) {
            TreePath parentPath = leadPath.getParentPath();
            OutlineItem parent = (OutlineItem)parentPath.getLastPathComponent();
            List<OutlineItem> children = parent.getChildren();
            document.removeItems();
            for (OutlineItem aChild : children) {
        	document.addItem(aChild);
            }
            
            OutlineItem selectedItem = (OutlineItem)leadPath.getLastPathComponent();
            int selectedItemIndex = children.indexOf(selectedItem);
            document.setIndex(selectedItemIndex);
	}
    }

    /**
     * @see org.tramper.gui.viewer.Body#paintMiniature(java.awt.Graphics2D, java.awt.Dimension, boolean)
     */
    public void paintMiniature(Graphics2D g2d, Dimension miniatureSize, boolean mouseOver) {
	double scale = 0.8;
	g2d.scale(scale, scale);
	
	outlineTree.paint(g2d);

	// reset scale
	g2d.scale(1/scale, 1/scale);
    }
}
