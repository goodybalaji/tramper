package org.tramper.gui.viewer;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.EnhancedIcon;
import javax.swing.JButton;
import javax.swing.JLabel;

import org.tramper.action.OpenSynthesizerAction;
import org.tramper.doc.SimpleDocument;
import org.tramper.doc.MarkupDocument;
import org.tramper.doc.Target;
import org.tramper.gui.IconFactory;
import org.tramper.gui.TooltipManager;

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
    /** synthesizer control panel display button */
    private JButton synthesizerButton;
    
    /**
     * 
     */
    public OutlineHeader() {
	super();

        ResourceBundle label = ResourceBundle.getBundle("label");

        synthesizerButton = new JButton();
        EnhancedIcon synthesizerIcon = new EnhancedIcon(getClass().getResource("/org/tramper/gui/images/speaker.png"));
        synthesizerButton.setIcon(synthesizerIcon);
        String tooltip = TooltipManager.createTooltip("synthesizer");
        synthesizerButton.setToolTipText(tooltip);
        synthesizerButton.setMargin(marginButton);
        titlePanel.add(synthesizerButton, titlePanel.getComponentCount()-1);
        
        titlePanel.add(Box.createHorizontalGlue(), titlePanel.getComponentCount()-1);
        
        GridBagLayout layout = (GridBagLayout)detailsPanel.getLayout();
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
        detailsPanel.add(languageLabel);
        
        documentLanguage = new JLabel();
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.anchor = GridBagConstraints.WEST;
        layout.setConstraints(documentLanguage, constraints);
        detailsPanel.add(documentLanguage);
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
        synthesizerButton.setAction(new OpenSynthesizerAction(target));
        String tooltip = TooltipManager.createTooltip("synthesizer");
        synthesizerButton.setToolTipText(tooltip);
    }
    
    /**
     * @see org.tramper.gui.viewer.SimpleHeader#relocalize()
     */
    public void relocalize() {
	super.relocalize();
        ResourceBundle label = ResourceBundle.getBundle("label");

        String formated = TooltipManager.createTooltip("synthesizer");
        synthesizerButton.setToolTipText(formated);
        
        languageLabel.setText(label.getString("javaspeaker.listEnginesLabel")+":");
        if (document != null) {
            Locale language = ((MarkupDocument)document).getLanguage();
            if (language != null) {
                documentLanguage.setText(language.getDisplayName());
            }
        }
    }
}
