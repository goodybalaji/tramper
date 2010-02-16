package org.tramper.outline;

import java.awt.Component;
import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.tramper.doc.Link;
import org.tramper.doc.OutlineItem;
import org.tramper.doc.SimpleDocument;
import org.tramper.gui.IconFactory;

/**
 * @author Paul-Emile
 */
public class OutlineItemTreeCellRenderer extends DefaultTreeCellRenderer {
    /** OutlineItemTreeCellRenderer.java long */
    private static final long serialVersionUID = 2156353010874503579L;
    
    /**
     * @see javax.swing.tree.TreeCellRenderer#getTreeCellRendererComponent(javax.swing.JTree, java.lang.Object, boolean, boolean, boolean, int, boolean)
     */
    public Component getTreeCellRendererComponent(JTree tree, Object value,
            boolean selected, boolean expanded, boolean leaf, int row,
            boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
        OutlineItem item = (OutlineItem)value;

        List<Link> links = item.getLinks();
        if (links.size() > 0) {
            Link aLink = links.get(0);
            SimpleDocument aDocument = aLink.getLinkedDocument();
            String mimeType = aDocument.getMimeType();
            this.setIcon(IconFactory.getIconByMimeType(mimeType));
            String title = item.getTitle();
            if (title != null && !title.equals("")) {
                this.setText(title);
            }
        }
        
        return this;
    }
}
