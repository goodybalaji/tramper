package org.tramper.gui.viewer;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.DateFormat;
import java.util.Date;
import java.util.ResourceBundle;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.tramper.doc.SimpleDocument;
import org.tramper.doc.Target;
import org.tramper.gui.IconFactory;

/**
 * display a simple document's header
 * @author Paul-Emile
 */
public class SimpleHeader extends JPanel {
    /** SimpleHeader.java long */
    private static final long serialVersionUID = -7805604139034099010L;
    /** title label */
    protected JLabel titleLabel;
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
    /** viewed document */
    protected SimpleDocument document;
    /** color of the labels */
    protected Color labelColor = Color.GRAY;
    /** buttons margin */
    protected Insets marginButton;
    
    /**
     * 
     */
    public SimpleHeader() {
        ResourceBundle label = ResourceBundle.getBundle("label");
        
        marginButton = new Insets(0, 0, 0, 0);
        
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(3, 3, 3, 3);
        constraints.weighty = 1;
        this.setLayout(layout);

        iconLabel = new JLabel();
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.gridwidth = 1;
        constraints.weightx = 1;
        constraints.gridheight = 5;
        layout.setConstraints(iconLabel, constraints);
        this.add(iconLabel);

        titleLabel = new JLabel();
        titleLabel.setText(label.getString("javaspeaker.title")+":");
        titleLabel.setForeground(labelColor);
        constraints.gridwidth = 1;
        constraints.weightx = 1;
        constraints.gridheight = 1;
        constraints.anchor = GridBagConstraints.NORTHEAST;
        layout.setConstraints(titleLabel, constraints);
        this.add(titleLabel);
        
        documentTitle = new JLabel();
        constraints.gridx = GridBagConstraints.RELATIVE;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.anchor = GridBagConstraints.WEST;
        layout.setConstraints(documentTitle, constraints);
        this.add(documentTitle);

        descLabel = new JLabel();
        descLabel.setText(label.getString("javaspeaker.description")+":");
        descLabel.setForeground(labelColor);
        constraints.gridwidth = 1;
        constraints.weightx = 1;
        constraints.gridheight = 1;
        constraints.anchor = GridBagConstraints.NORTHEAST;
        layout.setConstraints(descLabel, constraints);
        this.add(descLabel);
        
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
        this.add(documentDescription);

        copyrightLabel = new JLabel();
        copyrightLabel.setText(label.getString("javaspeaker.copyright")+":");
        copyrightLabel.setForeground(labelColor);
        constraints.gridx = 1;
        constraints.gridwidth = 1;
        constraints.weightx = 1;
        constraints.anchor = GridBagConstraints.NORTHEAST;
        constraints.fill = GridBagConstraints.NONE;
        layout.setConstraints(copyrightLabel, constraints);
        this.add(copyrightLabel);
        
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
        this.add(documentCopyright);

        constraints.fill = GridBagConstraints.NONE;

        authorLabel = new JLabel();
        authorLabel.setText(label.getString("javaspeaker.item.author")+":");
        authorLabel.setForeground(labelColor);
        constraints.gridx = 1;
        constraints.gridwidth = 1;
        constraints.weightx = 1;
        constraints.anchor = GridBagConstraints.EAST;
        layout.setConstraints(authorLabel, constraints);
        this.add(authorLabel);
        
        documentAuthor = new JLabel();
        constraints.gridx = GridBagConstraints.RELATIVE;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.anchor = GridBagConstraints.WEST;
        layout.setConstraints(documentAuthor, constraints);
        this.add(documentAuthor);

        mimeTypeLabel = new JLabel();
        mimeTypeLabel.setText(label.getString("javaspeaker.mimeType")+":");
        mimeTypeLabel.setForeground(labelColor);
        constraints.gridx = 1;
        constraints.gridwidth = 1;
        constraints.weightx = 1;
        constraints.anchor = GridBagConstraints.EAST;
        layout.setConstraints(mimeTypeLabel, constraints);
        this.add(mimeTypeLabel);
        
        documentMimeType = new JLabel();
        constraints.gridx = GridBagConstraints.RELATIVE;
        constraints.gridwidth = 1;
        constraints.weightx = 1;
        constraints.anchor = GridBagConstraints.WEST;
        layout.setConstraints(documentMimeType, constraints);
        this.add(documentMimeType);

        creationDateLabel = new JLabel();
        creationDateLabel.setText(label.getString("javaspeaker.item.creation")+":");
        creationDateLabel.setForeground(labelColor);
        constraints.gridwidth = 1;
        constraints.weightx = 1;
        constraints.anchor = GridBagConstraints.EAST;
        layout.setConstraints(creationDateLabel, constraints);
        this.add(creationDateLabel);
        
        documentCreationDate = new JLabel();
        constraints.gridwidth = 1;
        constraints.weightx = 1;
        constraints.anchor = GridBagConstraints.WEST;
        layout.setConstraints(documentCreationDate, constraints);
        this.add(documentCreationDate);

        this.setVisible(false);
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
    }
    
    /**
     * 
     */
    public void relocalize() {
        ResourceBundle label = ResourceBundle.getBundle("label");
        
        creationDateLabel.setText(label.getString("javaspeaker.item.creation")+":");
        authorLabel.setText(label.getString("javaspeaker.item.author")+":");
        copyrightLabel.setText(label.getString("javaspeaker.copyright")+":");
        mimeTypeLabel.setText(label.getString("javaspeaker.mimeType")+":");
        titleLabel.setText(label.getString("javaspeaker.title")+":");
        
        if (document != null) {
            Date creationDate = document.getCreationDate();
            if (creationDate != null) {
        	documentCreationDate.setText(DateFormat.getDateInstance().format(creationDate));
            }
        }
    }
}
