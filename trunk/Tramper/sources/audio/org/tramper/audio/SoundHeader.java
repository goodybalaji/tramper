package org.tramper.audio;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.DecimalFormat;
import java.util.ResourceBundle;

import javax.swing.JLabel;

import org.tramper.doc.Sound;
import org.tramper.doc.SimpleDocument;
import org.tramper.doc.Target;
import org.tramper.gui.viewer.SimpleHeader;

/**
 * Display a sound media header
 * @author Paul-Emile
 */
public class SoundHeader extends SimpleHeader {
    /** SoundHeader.java long */
    private static final long serialVersionUID = -2894913821029894689L;
    /** duration label */
    private JLabel durationLabel;
    /** duration */
    private JLabel documentDuration;
    /** album label */
    private JLabel albumLabel;
    /** album */
    private JLabel documentAlbum;

    /**
     * 
     */
    public SoundHeader() {
	super();
        ResourceBundle label = ResourceBundle.getBundle("label");

        GridBagLayout layout = (GridBagLayout)this.getLayout();
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
        this.add(durationLabel);
        
        documentDuration = new JLabel();
        constraints.gridwidth = 1;
        constraints.anchor = GridBagConstraints.WEST;
        layout.setConstraints(documentDuration, constraints);
        this.add(documentDuration);

        albumLabel = new JLabel();
        albumLabel.setText(label.getString("javaspeaker.album")+":");
        albumLabel.setForeground(labelColor);
        constraints.gridwidth = 1;
        constraints.weightx = 1;
        constraints.anchor = GridBagConstraints.EAST;
        layout.setConstraints(albumLabel, constraints);
        this.add(albumLabel);
        
        documentAlbum = new JLabel();
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.anchor = GridBagConstraints.WEST;
        layout.setConstraints(documentAlbum, constraints);
        this.add(documentAlbum);
    }
    
    /**
     * @see org.tramper.gui.viewer.SimpleHeader#displayDocument(org.tramper.doc.SimpleDocument)
     */
    public void displayDocument(SimpleDocument document, Target target) {
	super.displayDocument(document, target);
	Sound soundDoc = (Sound)document;
	long duration = soundDoc.getDuration();
	if (duration > 0) {
	    long durationSeconds = duration/1000000;
	    long durationHours = durationSeconds/3600;
	    long durationMinutes = durationSeconds%3600/60;
	    durationSeconds = durationSeconds%3600%60;
	    DecimalFormat formater = new DecimalFormat("00");
	    documentDuration.setText(formater.format(durationHours) + ":" + formater.format(durationMinutes) + ":" + formater.format(durationSeconds));
	}
	String album = soundDoc.getAlbum();
	if (album != null) {
	    documentAlbum.setText(album);
	}
    }

    /**
     * @see org.tramper.gui.viewer.SimpleHeader#relocalize()
     */
    public void relocalize() {
	super.relocalize();
        ResourceBundle label = ResourceBundle.getBundle("label");
        
        durationLabel.setText(label.getString("javaspeaker.duration")+":");
        albumLabel.setText(label.getString("javaspeaker.album")+":");
    }
}
