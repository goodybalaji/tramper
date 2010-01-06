package org.tramper.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.EnhancedIcon;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

import org.tramper.loader.Loader;

/**
 * Display and control a loading.
 * @author Paul-Emile
 */
public class LoadingViewer extends JPanel implements ActionListener {
    /** LoadingViewer.java long */
    private static final long serialVersionUID = -3872579012730949112L;
    /**
     * Managed loader
     */
    private Loader loader;
    /**
     * Progress bar linked to the loader
     */
    private JProgressBar loadingProgress;
    /**
     * Loading icon
     */
    private EnhancedIcon loadingIcon;
    /**
     * Stop loading button
     */
    private JButton stopButton;
    /**
     * Loading label
     */
    private JLabel loadLabel;
    
    /**
     * 
     */
    public LoadingViewer() {
	ResourceBundle label = ResourceBundle.getBundle("label");

        GridBagLayout addressLayout = new GridBagLayout();
        this.setLayout(addressLayout);
        GridBagConstraints constraint = new GridBagConstraints();
	
	loadLabel = new JLabel(label.getString("loading"));
	loadingIcon = new EnhancedIcon(getClass().getResource("images/Webpage.png"));
	loadLabel.setIcon(loadingIcon);
        constraint.anchor = GridBagConstraints.WEST;
        constraint.weightx = 0;
        constraint.insets = new Insets(2, 2, 2, 2);
        addressLayout.setConstraints(loadLabel, constraint);
	this.add(loadLabel);
	
	loadingProgress = new JProgressBar(JProgressBar.HORIZONTAL);
	loadingProgress.setStringPainted(true);
	loadingProgress.setMaximumSize(loadingProgress.getPreferredSize());
        constraint.fill = GridBagConstraints.HORIZONTAL;
        constraint.anchor = GridBagConstraints.CENTER;
        constraint.weightx = 1;
        constraint.insets = new Insets(2, 2, 2, 2);
        addressLayout.setConstraints(loadingProgress, constraint);
	this.add(loadingProgress);

	stopButton = new JButton();
	stopButton.addActionListener(this);
	stopButton.setActionCommand("stop");
	stopButton.setMargin(new Insets(0, 0, 0, 0));
	Icon stopIcon = new EnhancedIcon(getClass().getResource("images/Error.png"));
	stopButton.setIcon(stopIcon);
	stopButton.setEnabled(false);
        constraint.anchor = GridBagConstraints.EAST;
        constraint.weightx = 0;
        constraint.insets = new Insets(2, 2, 2, 2);
        addressLayout.setConstraints(stopButton, constraint);
	this.add(stopButton);
    }

    /**
     * @return loader.
     */
    public Loader getLoader() {
        return this.loader;
    }

    /**
     * @param loader loader 
     */
    public void setLoader(Loader loader) {
        this.loader = loader;
    }

    /**
     * 
     */
    public void start() {
	loadingProgress.setIndeterminate(true);
	loadingProgress.setString(loader.getUrl());
	loadingIcon.removeAllDecorationIcons();
	int loadingType = loader.getLoadingType();
	if (loadingType == Loader.UPLOAD) {
	    ImageIcon upIcon = new EnhancedIcon(getClass().getResource("images/Arrow Up.png"));
	    loadingIcon.addDecorationIcon(upIcon, SwingConstants.SOUTH_EAST);
    	} else {
    	    ImageIcon downIcon = new EnhancedIcon(getClass().getResource("images/Arrow Down.png"));
    	    loadingIcon.addDecorationIcon(downIcon, SwingConstants.NORTH_EAST);
    	}
	stopButton.setEnabled(true);
    }

    /**
     * 
     */
    public void stop() {
	loadingProgress.setIndeterminate(false);
	loadingIcon.removeAllDecorationIcons();
	stopButton.setEnabled(false);
    }

    /**
     * localize all the texts of the panel in the selected locale
     */
    public void relocalize() {
        ResourceBundle label = ResourceBundle.getBundle("label");
        loadLabel.setText(label.getString("loading"));
    }
    
    /**
     * 
     * @param e
     */
    public void actionPerformed(ActionEvent e) {
	String cmd = e.getActionCommand();
	if (loader != null) {
	    if (cmd.equals("stop")) {
                loader.stop();
            }
	}
    }
}
