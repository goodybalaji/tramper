package org.fingon;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.net.URL;

import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.TreeUI;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.tramper.audio.SoundPlayer;
import org.tramper.player.PlayException;
import org.tramper.player.PlayerFactory;
import org.tramper.synthesizer.SpeechSynthesizer;

/**
 * @author Paul-Emile
 * 
 */
public class FingonTreeUI extends TreeUI implements TreeSelectionListener, TreeExpansionListener, TreeModelListener {
    /** the instance common to every component */
    private static FingonTreeUI instance;
    /** expanded sound URL */
    private URL expandedSound;
    /** collapsed sound URL */
    private URL collapsedSound;

    /**
     * Returns the instance of UI
     * @param c
     * @return
     */
    public static ComponentUI createUI(JComponent c) {
	if (instance == null) {
	    instance = new FingonTreeUI();
	}
	return instance;
    }
    
    /**
     * @see javax.swing.plaf.ComponentUI#installUI(javax.swing.JComponent)
     */
    @Override
    public void installUI(JComponent c) {
	JTree tree = (JTree)c;
	tree.addTreeSelectionListener(this);
	tree.addTreeExpansionListener(this);
	TreeModel treeModel = tree.getModel();
	if (treeModel != null) {
	    treeModel.addTreeModelListener(this);
	}
	expandedSound = (URL)UIManager.get("Tree.expandedSound");
	collapsedSound = (URL)UIManager.get("Tree.collapsedSound");
    }

    /**
     * @see javax.swing.plaf.ComponentUI#uninstallUI(javax.swing.JComponent)
     */
    @Override
    public void uninstallUI(JComponent c) {
	JTree tree = (JTree)c;
	tree.removeTreeSelectionListener(this);
	tree.removeTreeExpansionListener(this);
	TreeModel treeModel = tree.getModel();
	if (treeModel != null) {
	    treeModel.removeTreeModelListener(this);
	}
    }

    /**
     * @see javax.swing.plaf.ComponentUI#update(java.awt.Graphics, javax.swing.JComponent)
     */
    @Override
    public void update(Graphics g, JComponent c) {
    }

    /**
     * @see javax.swing.plaf.TreeUI#cancelEditing(javax.swing.JTree)
     */
    @Override
    public void cancelEditing(JTree tree) {
    }

    /**
     * @see javax.swing.plaf.TreeUI#getClosestPathForLocation(javax.swing.JTree, int, int)
     */
    @Override
    public TreePath getClosestPathForLocation(JTree tree, int x, int y) {
	return null;
    }

    /**
     * @see javax.swing.plaf.TreeUI#getEditingPath(javax.swing.JTree)
     */
    @Override
    public TreePath getEditingPath(JTree tree) {
	return null;
    }

    /**
     * @see javax.swing.plaf.TreeUI#getPathBounds(javax.swing.JTree, javax.swing.tree.TreePath)
     */
    @Override
    public Rectangle getPathBounds(JTree tree, TreePath path) {
	return null;
    }

    /**
     * @see javax.swing.plaf.TreeUI#getPathForRow(javax.swing.JTree, int)
     */
    @Override
    public TreePath getPathForRow(JTree tree, int row) {
	return null;
    }

    /**
     * @see javax.swing.plaf.TreeUI#getRowCount(javax.swing.JTree)
     */
    @Override
    public int getRowCount(JTree tree) {
	return 0;
    }

    /**
     * @see javax.swing.plaf.TreeUI#getRowForPath(javax.swing.JTree, javax.swing.tree.TreePath)
     */
    @Override
    public int getRowForPath(JTree tree, TreePath path) {
	return 0;
    }

    /**
     * @see javax.swing.plaf.TreeUI#isEditing(javax.swing.JTree)
     */
    @Override
    public boolean isEditing(JTree tree) {
	return false;
    }

    /**
     * @see javax.swing.plaf.TreeUI#startEditingAtPath(javax.swing.JTree, javax.swing.tree.TreePath)
     */
    @Override
    public void startEditingAtPath(JTree tree, TreePath path) {
    }

    /**
     * @see javax.swing.plaf.TreeUI#stopEditing(javax.swing.JTree)
     */
    @Override
    public boolean stopEditing(JTree tree) {
	return false;
    }

    public void valueChanged(TreeSelectionEvent e) {
	TreePath selectedPath = e.getNewLeadSelectionPath();
	Object selectedComponent = selectedPath.getLastPathComponent();
	SpeechSynthesizer synthesizer = PlayerFactory.getSpeechSynthesizer();
	synthesizer.stop();
	synthesizer.play(selectedComponent.toString());
    }

    public void treeCollapsed(TreeExpansionEvent event) {
        try {
            SoundPlayer player = (SoundPlayer)PlayerFactory.getPlayerByExtension("wav");
            player.play(collapsedSound);
        } catch (PlayException e) {
        }
    }

    public void treeExpanded(TreeExpansionEvent event) {
        try {
            SoundPlayer player = (SoundPlayer)PlayerFactory.getPlayerByExtension("wav");
            player.play(expandedSound);
        } catch (PlayException e) {
        }
    }

    public void treeNodesChanged(TreeModelEvent e) {
	Object[] changedChildren = e.getChildren();
	SpeechSynthesizer synthesizer = PlayerFactory.getSpeechSynthesizer();
	synthesizer.stop();
	for (Object changedChild : changedChildren) {
	    synthesizer.play(changedChild.toString() + " changed");
	}
    }

    public void treeNodesInserted(TreeModelEvent e) {
	Object[] insertedChildren = e.getChildren();
	SpeechSynthesizer synthesizer = PlayerFactory.getSpeechSynthesizer();
	synthesizer.stop();
	for (Object insertedChild : insertedChildren) {
	    synthesizer.play(insertedChild.toString() + " inserted");
	}
    }

    public void treeNodesRemoved(TreeModelEvent e) {
	Object[] removedChildren = e.getChildren();
	SpeechSynthesizer synthesizer = PlayerFactory.getSpeechSynthesizer();
	//Locale currentLocale = synthesizer.getEngineLocale();
	synthesizer.stop();
	for (Object removedChild : removedChildren) {
	    synthesizer.play(removedChild.toString() + " removed");
	}
    }

    public void treeStructureChanged(TreeModelEvent e) {
    }
}
