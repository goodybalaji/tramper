package org.tramper.gui.fileFilter;

import java.io.File;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.filechooser.FileFilter;

import org.tramper.audio.SoundParser;

/**
 * Display the available audio encoding format in the file chooser
 * @author Paul-Emile
 */
public class SaveAudioFileFilter extends FileFilter {
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
        List<String> audioExtension = SoundParser.getSupportedEncodingExtensions();
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
    public String getDescription() {
        ResourceBundle label = ResourceBundle.getBundle("label");
        String audioLabel = label.getString("saveAudioFilter");
        List<String> audioExtension = SoundParser.getSupportedEncodingExtensions();
        for (String extension : audioExtension) {
            audioLabel = audioLabel.concat("*.").concat(extension).concat(", ");
        }
        audioLabel = audioLabel.concat(")");
        return audioLabel;
    }
}