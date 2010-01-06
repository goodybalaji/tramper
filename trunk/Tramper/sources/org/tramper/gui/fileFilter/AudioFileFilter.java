package org.tramper.gui.fileFilter;

import java.io.File;
import java.util.ResourceBundle;

import javax.swing.filechooser.FileFilter;

/**
 * @author Paul-Emile
 * 
 */
public class AudioFileFilter extends FileFilter {
    /**
     * All supported audio extensions
     */
    private String[] audioExtension = {".wav", ".WAV", ".aif", ".AIF", ".aiff", ".AIFF", ".aifc", ".AIFC", ".au", ".AU", ".mp2", ".MP2", ".mp3", ".MP3", ".ogg", ".OGG", ".ape", ".APE", ".spx", ".SPX"};

    /**
     * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
     */
    @Override
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
    @Override
    public String getDescription() {
        ResourceBundle label = ResourceBundle.getBundle("label");
        return label.getString("audioFilter");
    }
}
