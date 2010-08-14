package org.tramper.gui;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.EnhancedIcon;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

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
     * Stop loading button
     */
    private JButton stopButton;
    
    /**
     * 
     */
    public LoadingViewer() {
	BoxLayout layout = new BoxLayout(this, BoxLayout.X_AXIS);
	this.setLayout(layout);
	
	this.setOpaque(false);
	
	loadingProgress = new JProgressBar();
	loadingProgress.setStringPainted(false);
	//loadingProgress.setUI(CompactProgressBarUI.createUI(loadingProgress));
	this.add(loadingProgress);

	stopButton = new JButton();
	stopButton.addActionListener(this);
	stopButton.setActionCommand("stop");
	stopButton.setMargin(new Insets(0, 0, 0, 0));
	Icon stopIcon = new EnhancedIcon(getClass().getResource("images/Error.png"));
	stopButton.setIcon(stopIcon);
	stopButton.setEnabled(false);
	this.add(stopButton);
    }

    /**
     * @see javax.swing.JComponent#getMinimumSize()
     */
    @Override
    public Dimension getMinimumSize() {
	return new Dimension(66, 16);
    }

    /**
     * @see javax.swing.JComponent#getPreferredSize()
     */
    @Override
    public Dimension getPreferredSize() {
	return new Dimension(82, 32);
    }

    /**
     * @see javax.swing.JComponent#getMaximumSize()
     */
    @Override
    public Dimension getMaximumSize() {
	return new Dimension(98, 48);
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
	loadingProgress.setToolTipText(loader.getUrl());
	stopButton.setEnabled(true);
    }

    /**
     * 
     */
    public void stop() {
	loadingProgress.setIndeterminate(false);
	stopButton.setEnabled(false);
    }

    /**
     * localize all the texts of the panel in the selected locale
     */
    public void relocalize() {
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
