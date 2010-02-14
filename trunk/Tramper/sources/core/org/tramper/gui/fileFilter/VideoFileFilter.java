package org.tramper.gui.fileFilter;

import java.io.File;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.filechooser.FileFilter;

import org.tramper.video.VideoParser;

/**
 * 
 * @author Paul-Emile
 */
public class VideoFileFilter extends FileFilter {
    /**
     * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
     */
    @Override
    public boolean accept(File aFile) {
        if (aFile.isDirectory()) {
            return true;
        }

        List<String> videoExtension = VideoParser.getSupportedExtensions();
        String filename = aFile.getName();
        for (String extension : videoExtension) {
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
        String videoLabel = label.getString("video").concat(" (");
        List<String> videoExtension = VideoParser.getSupportedExtensions();
        for (String extension : videoExtension) {
            videoLabel = videoLabel.concat("*.").concat(extension).concat(", ");
        }
        videoLabel = videoLabel.concat(")");
        return videoLabel;
    }
}
