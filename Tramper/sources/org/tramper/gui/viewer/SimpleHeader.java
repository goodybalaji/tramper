package org.tramper.gui.viewer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;
import java.util.ResourceBundle;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.EnhancedIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;

import org.tramper.action.RemoveDocumentAction;
import org.tramper.doc.Favorites;
import org.tramper.doc.SimpleDocument;
import org.tramper.doc.Target;
import org.tramper.gui.IconFactory;
import org.tramper.ui.UserInterfaceFactory;

/**
 * display a simple document's header
 * @author Paul-Emile
 */
public class SimpleHeader extends JPanel implements ItemListener {
    /** SimpleHeader.java long */
    private static final long serialVersionUID = -7805604139034099010L;
    /** title panel */
    protected JPanel titlePanel;
    /** details panel */
    protected JPanel detailsPanel;
    /** title */
    protected JLabel documentTitle;
    /** document's icon */
    protected JLabel iconLabel;
    /** description label */
    protected JLabel descLabel;
    /** document's description */
    protected JTextArea documentDescription;
    /** mime type label */
    private JLabel mimeTypeLabel;
    /** mime type */
    private JLabel documentMimeType;
    /** author label */
    private JLabel authorLabel;
    /** author */
    private JLabel documentAuthor;
    /** copyright label */
    private JLabel copyrightLabel;
    /** copyright */
    private JTextArea documentCopyright;
    /** creation date label */
    private JLabel creationDateLabel;
    /** creation date */
    private JLabel documentCreationDate;
    /** favorites button */
    private JToggleButton favoriteButton;
    /** details button */
    private JToggleButton detailButton;
    /** viewed document */
    protected SimpleDocument document;
    /** close control panel button */
    private JButton closeButton;
    /** color of the labels */
    protected Color labelColor = Color.GRAY;
    /** buttons margin */
    protected Insets marginButton;
    
    /**
     * 
     */
    public SimpleHeader() {
        ResourceBundle label = ResourceBundle.getBundle("label");
        this.setLayout(new BorderLayout());
        
        titlePanel = new JPanel();
        BoxLayout titleLayout = new BoxLayout(titlePanel, BoxLayout.X_AXIS);
        titlePanel.setLayout(titleLayout);
        
        marginButton = new Insets(0, 0, 0, 0);
        
        detailButton = new JToggleButton();
        // do not use "../" in the resource's path because it won't work inside of a jar
        URL iconUrl = getClass().getResource("/org/tramper/gui/images/remove.png");
        Icon selectedDetailIcon = new EnhancedIcon(iconUrl);
	detailButton.setSelectedIcon(selectedDetailIcon);
        Icon detailIcon = new EnhancedIcon(getClass().getResource("/org/tramper/gui/images/add.png"));
        detailButton.setIcon(detailIcon);
        detailButton.addItemListener(this);
        detailButton.setMargin(marginButton);
        detailButton.setActionCommand("detail");
	detailButton.setToolTipText(label.getString("javaspeaker.showDetails"));
        titlePanel.add(detailButton);
        
        documentTitle = new JLabel();
        titlePanel.add(documentTitle);

        favoriteButton = new JToggleButton();
        Icon selectedFavIcon = new EnhancedIcon(getClass().getResource("/org/tramper/gui/images/Favorites.png"));
        favoriteButton.setSelectedIcon(selectedFavIcon);
        Icon favIcon = new EnhancedIcon(getClass().getResource("/org/tramper/gui/images/Favorites_inverted.png"));
        favoriteButton.setIcon(favIcon);
        favoriteButton.addItemListener(this);
        favoriteButton.setMargin(marginButton);
        favoriteButton.setActionCommand("addFavorite");
        titlePanel.add(favoriteButton);

        titlePanel.add(Box.createHorizontalGlue());

        Icon closeIcon = new EnhancedIcon(getClass().getResource("/org/tramper/gui/images/Error.png"));
        closeButton = new JButton(closeIcon);
        closeButton.setMargin(marginButton);
        titlePanel.add(closeButton);
        
        this.add(titlePanel, BorderLayout.NORTH);
        
        
        detailsPanel = new JPanel();
        
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(3, 3, 3, 3);
        constraints.weighty = 1;
        detailsPanel.setLayout(layout);
        
        iconLabel = new JLabel();
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.gridwidth = 1;
        constraints.weightx = 1;
        constraints.gridheight = 4;
        layout.setConstraints(iconLabel, constraints);
        detailsPanel.add(iconLabel);
        
        descLabel = new JLabel();
        descLabel.setText(label.getString("javaspeaker.description")+":");
        descLabel.setForeground(labelColor);
        constraints.gridwidth = 1;
        constraints.weightx = 1;
        constraints.gridheight = 1;
        constraints.anchor = GridBagConstraints.NORTHEAST;
        layout.setConstraints(descLabel, constraints);
        detailsPanel.add(descLabel);
        
        documentDescription = new JTextArea();
        documentDescription.setWrapStyleWord(true);
        documentDescription.setLineWrap(true);
        documentDescription.setColumns(200);
        documentDescription.setEditable(false);
        documentDescription.setBackground(this.getBackground());
        documentDescription.setOpaque(false);
        documentDescription.setBorder(null);
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.fill = GridBagConstraints.BOTH;
        layout.setConstraints(documentDescription, constraints);
        detailsPanel.add(documentDescription);

        copyrightLabel = new JLabel();
        copyrightLabel.setText(label.getString("javaspeaker.copyright")+":");
        copyrightLabel.setForeground(labelColor);
        constraints.gridx = 1;
        constraints.gridwidth = 1;
        constraints.weightx = 1;
        constraints.anchor = GridBagConstraints.NORTHEAST;
        constraints.fill = GridBagConstraints.NONE;
        layout.setConstraints(copyrightLabel, constraints);
        detailsPanel.add(copyrightLabel);
        
        documentCopyright = new JTextArea();
        documentCopyright.setWrapStyleWord(true);
        documentCopyright.setLineWrap(true);
        documentCopyright.setColumns(200);
        documentCopyright.setEditable(false);
        documentCopyright.setBackground(this.getBackground());
        documentCopyright.setOpaque(false);
        documentCopyright.setBorder(null);
        constraints.gridx = GridBagConstraints.RELATIVE;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.BOTH;
        layout.setConstraints(documentCopyright, constraints);
        detailsPanel.add(documentCopyright);

        constraints.fill = GridBagConstraints.NONE;

        authorLabel = new JLabel();
        authorLabel.setText(label.getString("javaspeaker.item.author")+":");
        authorLabel.setForeground(labelColor);
        constraints.gridx = 1;
        constraints.gridwidth = 1;
        constraints.weightx = 1;
        constraints.anchor = GridBagConstraints.EAST;
        layout.setConstraints(authorLabel, constraints);
        detailsPanel.add(authorLabel);
        
        documentAuthor = new JLabel();
        constraints.gridx = GridBagConstraints.RELATIVE;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.anchor = GridBagConstraints.WEST;
        layout.setConstraints(documentAuthor, constraints);
        detailsPanel.add(documentAuthor);

        mimeTypeLabel = new JLabel();
        mimeTypeLabel.setText(label.getString("javaspeaker.mimeType")+":");
        mimeTypeLabel.setForeground(labelColor);
        constraints.gridx = 1;
        constraints.gridwidth = 1;
        constraints.weightx = 1;
        constraints.anchor = GridBagConstraints.EAST;
        layout.setConstraints(mimeTypeLabel, constraints);
        detailsPanel.add(mimeTypeLabel);
        
        documentMimeType = new JLabel();
        constraints.gridx = GridBagConstraints.RELATIVE;
        constraints.gridwidth = 1;
        constraints.weightx = 1;
        constraints.anchor = GridBagConstraints.WEST;
        layout.setConstraints(documentMimeType, constraints);
        detailsPanel.add(documentMimeType);

        creationDateLabel = new JLabel();
        creationDateLabel.setText(label.getString("javaspeaker.item.creation")+":");
        creationDateLabel.setForeground(labelColor);
        constraints.gridwidth = 1;
        constraints.weightx = 1;
        constraints.anchor = GridBagConstraints.EAST;
        layout.setConstraints(creationDateLabel, constraints);
        detailsPanel.add(creationDateLabel);
        
        documentCreationDate = new JLabel();
        constraints.gridwidth = 1;
        constraints.weightx = 1;
        constraints.anchor = GridBagConstraints.WEST;
        layout.setConstraints(documentCreationDate, constraints);
        detailsPanel.add(documentCreationDate);

        detailsPanel.setVisible(false);
        this.add(detailsPanel, BorderLayout.CENTER);
    }
    
    /**
     * display the document's header 
     * @param document
     */
    public void displayDocument(SimpleDocument document, Target target) {
        this.document = document;
        
        String title = document.getTitle();
        documentTitle.setText(title);
        
        String author = document.getAuthor();
        documentAuthor.setText(author);

        String copyright = document.getCopyright();
        documentCopyright.setText(copyright);

        Date creationDate = document.getCreationDate();
        if (creationDate != null) {
            documentCreationDate.setText(DateFormat.getDateInstance().format(creationDate));
        }
        
        String description = document.getDescription();
        documentDescription.setText(description);
        
        String mimeType = document.getMimeType();
        if (mimeType != null) {
            documentMimeType.setText(mimeType);
            Icon mimeTypeIcon = IconFactory.getIconByMimeType(mimeType);
            documentMimeType.setIcon(mimeTypeIcon);
        } else {
            documentMimeType.setText(null);
            documentMimeType.setIcon(null);
        }

        ResourceBundle label = ResourceBundle.getBundle("label");
        Favorites fav = Favorites.getInstance();
        if (fav.isFavorite(document)) {
            favoriteButton.setSelected(true);
            favoriteButton.setToolTipText(label.getString("javaspeaker.removeFavorite"));
        } else {
            favoriteButton.setSelected(false);
            favoriteButton.setToolTipText(label.getString("javaspeaker.addFavorite"));
        }
        
        closeButton.setAction(new RemoveDocumentAction(target));
    }
    
    /**
     * 
     */
    public void relocalize() {
        ResourceBundle label = ResourceBundle.getBundle("label");
        
        if (favoriteButton.isSelected()) {
            favoriteButton.setToolTipText(label.getString("javaspeaker.removeFavorite"));
        } else {
            favoriteButton.setToolTipText(label.getString("javaspeaker.addFavorite"));
        }

        if (detailButton.isSelected()) {
            detailButton.setToolTipText(label.getString("javaspeaker.hideDetails"));
        } else {
            detailButton.setToolTipText(label.getString("javaspeaker.showDetails"));
        }
	
        creationDateLabel.setText(label.getString("javaspeaker.item.creation")+":");
        authorLabel.setText(label.getString("javaspeaker.item.author")+":");
        copyrightLabel.setText(label.getString("javaspeaker.copyright")+":");
        mimeTypeLabel.setText(label.getString("javaspeaker.mimeType")+":");
        
        if (document != null) {
            Date creationDate = document.getCreationDate();
            if (creationDate != null) {
        	documentCreationDate.setText(DateFormat.getDateInstance().format(creationDate));
            }
        }
    }
    
    /**
     * manage the favorites button
     * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
     */
    public void itemStateChanged(ItemEvent e) {
        Object source = e.getSource();
        int stateChange = e.getStateChange();
        if (source.equals(favoriteButton)) {
            if (document != null) {
                Favorites fav = Favorites.getInstance();
                ResourceBundle label = ResourceBundle.getBundle("label");
                
                if (stateChange == ItemEvent.SELECTED) {
                    fav.addFavorite(document);
                    favoriteButton.setToolTipText(label.getString("javaspeaker.removeFavorite"));
                }
                else {
                    fav.removeFavorite(document);
                    favoriteButton.setToolTipText(label.getString("javaspeaker.addFavorite"));
                }
            }
        } else if (source.equals(detailButton)) {
            ResourceBundle label = ResourceBundle.getBundle("label");
            
            if (stateChange == ItemEvent.SELECTED) {
        	detailsPanel.setVisible(true);
                detailButton.setToolTipText(label.getString("javaspeaker.hideDetails"));
        	UserInterfaceFactory.getGraphicalUserInterface().validate();
            }
            else {
        	detailsPanel.setVisible(false);
        	detailButton.setToolTipText(label.getString("javaspeaker.showDetails"));
        	UserInterfaceFactory.getGraphicalUserInterface().validate();
            }
        }
    }
}
