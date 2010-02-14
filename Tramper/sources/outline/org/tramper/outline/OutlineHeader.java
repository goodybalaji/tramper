package org.tramper.outline;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.Icon;
import javax.swing.JLabel;

import org.tramper.doc.SimpleDocument;
import org.tramper.doc.MarkupDocument;
import org.tramper.doc.Target;
import org.tramper.gui.IconFactory;
import org.tramper.gui.viewer.SimpleHeader;

/**
 * @author Paul-Emile
 * Panel displaying the header of a speakable document
 */
public class OutlineHeader extends SimpleHeader {
    /** OutlineHeader.java long */
    private static final long serialVersionUID = 3182823495145828989L;
    /** language label */
    private JLabel languageLabel;
    /** language */
    private JLabel documentLanguage;
    
    /**
     * 
     */
    public OutlineHeader() {
	super();

        ResourceBundle label = ResourceBundle.getBundle("label");

        GridBagLayout layout = (GridBagLayout)this.getLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(3, 3, 3, 3);
        constraints.weighty = 1;
        
        languageLabel = new JLabel();
        languageLabel.setText(label.getString("javaspeaker.listEnginesLabel")+":");
        languageLabel.setForeground(labelColor);
        constraints.gridwidth = 1;
        constraints.weightx = 1;
        constraints.anchor = GridBagConstraints.EAST;
        layout.setConstraints(languageLabel, constraints);
        this.add(languageLabel);
        
        documentLanguage = new JLabel();
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.anchor = GridBagConstraints.WEST;
        layout.setConstraints(documentLanguage, constraints);
        this.add(documentLanguage);
    }

    /**
     * @see org.tramper.gui.viewer.SimpleHeader#displayDocument(org.tramper.doc.SimpleDocument)
     */
    public void displayDocument(SimpleDocument document, Target target) {
	super.displayDocument(document, target);

        Icon icon = ((MarkupDocument)document).getIcon();
        iconLabel.setIcon(icon);
        
        Locale language = ((MarkupDocument)document).getLanguage();
        if (language != null) {
            documentLanguage.setText(language.getDisplayName());
            Icon langIcon = IconFactory.getFlagIconByLocale(language);
            documentLanguage.setIcon(langIcon);
        } else {
            documentLanguage.setText(null);
            documentLanguage.setIcon(null);
        }
    }
    
    /**
     * @see org.tramper.gui.viewer.SimpleHeader#relocalize()
     */
    public void relocalize() {
	super.relocalize();
        ResourceBundle label = ResourceBundle.getBundle("label");

        languageLabel.setText(label.getString("javaspeaker.listEnginesLabel")+":");
        if (document != null) {
            Locale language = ((MarkupDocument)document).getLanguage();
            if (language != null) {
                documentLanguage.setText(language.getDisplayName());
            }
        }
    }
}
