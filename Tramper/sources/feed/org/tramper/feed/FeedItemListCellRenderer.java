package org.tramper.feed;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import org.tramper.doc.FeedItem;
import org.tramper.doc.Link;
import org.tramper.doc.SimpleDocument;
import org.tramper.gui.IconFactory;


/**
 * Display a feed item in a JList
 * @author Paul-Emile
 */
public class FeedItemListCellRenderer extends JPanel implements ListCellRenderer {
    /** FeedItemListCellRenderer.java long */
    private static final long serialVersionUID = 8116072096549573274L;
    /**  */
    private JLabel titleLabel;
    /**  */
    private JLabel categoryLabel;
    /**  */
    private JLabel modificationDateLabel;
    /**  */
    private JLabel creationDateLabel;
    
    
    /**
     * 
     */
    public FeedItemListCellRenderer() {
        super();
        GridBagLayout panelLayout = new GridBagLayout();
        this.setLayout(panelLayout);
        
        GridBagConstraints constraints = new GridBagConstraints();

        titleLabel = new JLabel();
        constraints.anchor = GridBagConstraints.WEST;
        constraints.weightx = 1;
        constraints.gridheight = 1;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        panelLayout.setConstraints(titleLabel, constraints);
        this.add(titleLabel);
        
        categoryLabel = new JLabel();
        categoryLabel.setForeground(new Color(0, 64, 0));
        constraints.anchor = GridBagConstraints.WEST;
        constraints.weightx = 0;
        constraints.gridwidth = 1;
        constraints.insets = new Insets(0, 20, 0, 0);
        panelLayout.setConstraints(categoryLabel, constraints);
        this.add(categoryLabel);

        creationDateLabel = new JLabel();
        creationDateLabel.setForeground(Color.GRAY);
        constraints.anchor = GridBagConstraints.WEST;
        constraints.weightx = 0;
        constraints.insets = new Insets(0, 0, 0, 0);
        panelLayout.setConstraints(creationDateLabel, constraints);
        this.add(creationDateLabel);
        
        modificationDateLabel = new JLabel();
        modificationDateLabel.setForeground(Color.GRAY);
        constraints.anchor = GridBagConstraints.WEST;
        constraints.weightx = 1;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        panelLayout.setConstraints(modificationDateLabel, constraints);
        this.add(modificationDateLabel);
    }

    /**
     * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
     */
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        FeedItem item = (FeedItem)value;
        
        String title = item.getTitle();
        String text = "";
        if (title != null) {
            text = title;
        }
        
        boolean found = false;
        List<Link> links = item.getLinks();
        for (int i=0; i<links.size(); i++) {
            Link link = links.get(i);
            String relation = link.getRelation();
            if ("via".equals(relation) || "enclosure".equals(relation)) {
                found = true;
        	SimpleDocument linkedDoc = link.getLinkedDocument();
        	
        	String mimeType = linkedDoc.getMimeType();
                Icon icon = IconFactory.getIconByMimeType(mimeType);
                titleLabel.setIcon(icon);
                
                if (title == null) {
                    text = linkedDoc.getTitle();
                }
                break;
            }
        }
        
        titleLabel.setText(text);
        
        if (!found) {
            titleLabel.setIcon(IconFactory.getIconByMimeType(null));
        }
        
        categoryLabel.setText(item.getCategory());
        
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
        
        Date pubDate = item.getPublicationDate();
        if (pubDate != null) {
            creationDateLabel.setText(dateFormat.format(pubDate));
        } else {
            creationDateLabel.setText("");
        }
        
        Date updateDate = item.getUpdateDate();
        if (updateDate != null) {
            modificationDateLabel.setText(dateFormat.format(updateDate));
        } else {
            modificationDateLabel.setText("");
        }
        
        if (isSelected) {
            this.setBackground(list.getSelectionBackground());
            this.setForeground(list.getSelectionForeground());
            titleLabel.setBackground(list.getSelectionBackground());
            titleLabel.setForeground(list.getSelectionForeground());
        } else {
            this.setBackground(list.getBackground());
            this.setForeground(list.getForeground());
            titleLabel.setBackground(list.getBackground());
            titleLabel.setForeground(list.getForeground());
        }
        Border border = new EmptyBorder(2, 2, 2, 2);
        this.setBorder(border);
        this.setFont(list.getFont());
        this.setOpaque(list.isOpaque());
        return this;
    }
}