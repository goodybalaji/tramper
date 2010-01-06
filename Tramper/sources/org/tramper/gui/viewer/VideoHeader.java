package org.tramper.gui.viewer;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ResourceBundle;

import javax.swing.JLabel;

import org.tramper.doc.SimpleDocument;
import org.tramper.doc.Target;
import org.tramper.doc.Video;

/**
 * 
 * @author Paul-Emile
 */
public class VideoHeader extends SimpleHeader {
    /**
     * VideoHeader.java long
     */
    private static final long serialVersionUID = 1L;
    /** duration label */
    private JLabel durationLabel;
    /** duration */
    private JLabel documentDuration;
    /** duration label */
    private JLabel frameSizeLabel;
    /** duration */
    private JLabel documentFrameSize;

    /**
     * 
     */
    public VideoHeader() {
	super();
        ResourceBundle label = ResourceBundle.getBundle("label");

        GridBagLayout layout = (GridBagLayout)detailsPanel.getLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(3, 3, 3, 3);
        constraints.weighty = 1;

        durationLabel = new JLabel();
        durationLabel.setText(label.getString("javaspeaker.duration")+":");
        durationLabel.setForeground(labelColor);
        constraints.gridwidth = 1;
        constraints.weightx = 1;
        constraints.anchor = GridBagConstraints.EAST;
        layout.setConstraints(durationLabel, constraints);
        detailsPanel.add(durationLabel);
        
        documentDuration = new JLabel();
        constraints.gridwidth = 1;
        constraints.anchor = GridBagConstraints.WEST;
        layout.setConstraints(documentDuration, constraints);
        detailsPanel.add(documentDuration);

        frameSizeLabel = new JLabel();
        frameSizeLabel.setText(label.getString("dimension")+":");
        frameSizeLabel.setForeground(labelColor);
        constraints.gridwidth = 1;
        constraints.weightx = 1;
        constraints.anchor = GridBagConstraints.EAST;
        layout.setConstraints(frameSizeLabel, constraints);
        detailsPanel.add(frameSizeLabel);
        
        documentFrameSize = new JLabel();
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.anchor = GridBagConstraints.WEST;
        layout.setConstraints(documentFrameSize, constraints);
        detailsPanel.add(documentFrameSize);
    }

    /**
     * @see org.tramper.gui.viewer.SimpleHeader#displayDocument(org.tramper.doc.SimpleDocument)
     */
    public void displayDocument(SimpleDocument document, Target target) {
	super.displayDocument(document, target);

        ResourceBundle label = ResourceBundle.getBundle("label");
        
	double duration = ((Video)document).getDuration();
	if (duration > 0) {
	    documentDuration.setText(duration + label.getString("javaspeaker.unit.second"));
	}
	
	Dimension frameSize = ((Video)document).getFrameSize();
	if (frameSize != null) {
	    documentFrameSize.setText(frameSize.width + "x" + frameSize.height);
	}
    }

    /**
     * @see org.tramper.gui.viewer.SimpleHeader#relocalize()
     */
    public void relocalize() {
	super.relocalize();
        ResourceBundle label = ResourceBundle.getBundle("label");
        
        durationLabel.setText(label.getString("javaspeaker.duration")+":");
        frameSizeLabel.setText(label.getString("dimension")+":");
    }
}
