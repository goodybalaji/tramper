package org.tramper.gui.fileFilter;

import java.io.File;
import java.util.ResourceBundle;

import javax.swing.filechooser.FileFilter;

/**
 * Display the available audio output format in the file chooser
 * @author Paul-Emile
 */
public class SaveAudioFileFilter extends FileFilter {
    /**
     * All supported audio extensions
     */
    private String[] audioExtension = {".wav", ".WAV", ".aif", ".AIF", ".aiff", ".AIFF", ".au", ".AU"};

    /**
     * 
     */
    public SaveAudioFileFilter() {
        super();
    }

    /**
     * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
     */
    public boolean accept(File aFile) {
        if (aFile.isDirectory()) {
            return true;
        }
        
        String filename = aFile.getName();
        for (int i=0; i<audioExtension.length; i++) {
            if (filename.endsWith(audioExtension[i])) {
        	return true;
            }
        }
        return false;
    }

    /**
     * @see javax.swing.filechooser.FileFilter#getDescription()
     */
    public String getDescription() {
        ResourceBundle label = ResourceBundle.getBundle("label");
        return label.getString("saveAudioFilter");
    }
}