package org.tramper.gui;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import org.tramper.doc.Link;
import org.tramper.doc.Sound;
import org.tramper.doc.SimpleDocument;

/**
 * Render a link in a label with icon for JList
 * @author Paul-Emile
 */
public class LinkListCellRenderer extends JLabel implements ListCellRenderer {
    /** LinkListCellRenderer.java long */
    private static final long serialVersionUID = 2615808504089209953L;

    /**
     * 
     */
    public LinkListCellRenderer() {
        super();
    }

    /**
     * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
     */
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        String text = "";
        String mimeType = null;
        if (value instanceof Link) {
            Link link = (Link)value;
            SimpleDocument aDocument = link.getLinkedDocument();
            mimeType = aDocument.getMimeType();
            String title = aDocument.getTitle();
            //text = "<html>" + title + "<sup style='color: red'>("+link.getNumber()+")</sup></html>";
            text = title;
        } else if (value instanceof Sound) {
            Sound media = (Sound)value;
            mimeType = media.getMimeType();
            text = media.getTitle();
        }
        Icon docIcon = IconFactory.getIconByMimeType(mimeType);
        this.setIcon(docIcon);
        this.setText(text);
        
        if (isSelected) {
            this.setBackground(list.getSelectionBackground());
            this.setForeground(list.getSelectionForeground());
        }
        else {
            this.setBackground(list.getBackground());
            this.setForeground(list.getForeground());
        }
        Border border = new EmptyBorder(2, 2, 2, 2);
        this.setBorder(border);
        this.setFont(list.getFont());
        this.setOpaque(list.isOpaque());
        
        return this;
    }
}
