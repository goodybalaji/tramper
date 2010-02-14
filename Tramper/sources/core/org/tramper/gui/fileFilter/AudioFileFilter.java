package org.tramper.gui.fileFilter;

import java.io.File;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.filechooser.FileFilter;

import org.tramper.audio.SoundParser;

/**
 * 
 * @author Paul-Emile
 */
public class AudioFileFilter extends FileFilter {
    /**
     * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
     */
    @Override
    public boolean accept(File aFile) {
        if (aFile.isDirectory()) {
            return true;
        }
        List<String> audioExtension = SoundParser.getSupportedExtensions();
        String filename = aFile.getName();
        for (String extension : audioExtension) {
            if (filename.toLowerCase().endsWith(extension.toLowerCase())) {
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
        String audioLabel = label.getString("audio").concat(" (");
        List<String> audioExtension = SoundParser.getSupportedExtensions();
        for (String extension : audioExtension) {
            audioLabel = audioLabel.concat("*.").concat(extension).concat(", ");
        }
        audioLabel = audioLabel.concat(")");
        return audioLabel;
    }
}
