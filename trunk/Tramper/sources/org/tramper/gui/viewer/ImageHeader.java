package org.tramper.gui.viewer;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ResourceBundle;

import javax.swing.JLabel;

import org.tramper.doc.ImageDocument;
import org.tramper.doc.SimpleDocument;
import org.tramper.doc.Target;

/**
 * @author Paul-Emile
 * Display a image header
 */
public class ImageHeader extends SimpleHeader {
    /** ImageHeader.java long */
    private static final long serialVersionUID = -2894913821029894689L;
    /** duration label */
    private JLabel dimensionLabel;
    /** duration */
    private JLabel documentDimension;

    /**
     * 
     */
    public ImageHeader() {
	super();
        ResourceBundle label = ResourceBundle.getBundle("label");

        GridBagLayout layout = (GridBagLayout)detailsPanel.getLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(3, 3, 3, 3);
        constraints.weighty = 1;

        dimensionLabel = new JLabel();
        dimensionLabel.setText(label.getString("dimension")+":");
        dimensionLabel.setForeground(labelColor);
        constraints.gridwidth = 1;
        constraints.weightx = 1;
        constraints.anchor = GridBagConstraints.EAST;
        layout.setConstraints(dimensionLabel, constraints);
        detailsPanel.add(dimensionLabel);
        
        documentDimension = new JLabel();
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.anchor = GridBagConstraints.WEST;
        layout.setConstraints(documentDimension, constraints);
        detailsPanel.add(documentDimension);
    }
    
    /**
     * @see org.tramper.gui.viewer.SimpleHeader#displayDocument(org.tramper.doc.SimpleDocument)
     */
    public void displayDocument(SimpleDocument document, Target target) {
	super.displayDocument(document, target);

	int width = ((ImageDocument)document).getWidth();
	int height = ((ImageDocument)document).getHeight();
	documentDimension.setText(width + "x" + height);
    }

    /**
     * @see org.tramper.gui.viewer.SimpleHeader#relocalize()
     */
    public void relocalize() {
	super.relocalize();
        ResourceBundle label = ResourceBundle.getBundle("label");
        
        dimensionLabel.setText(label.getString("dimension")+":");
    }

}
