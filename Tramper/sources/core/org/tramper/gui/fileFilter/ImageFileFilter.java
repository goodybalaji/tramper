package org.tramper.gui.fileFilter;

import java.io.File;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.filechooser.FileFilter;

import org.tramper.image.ImageParser;

/**
 * @author Paul-Emile
 * 
 */
public class ImageFileFilter extends FileFilter {
    /**
     * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
     */
    @Override
    public boolean accept(File aFile) {
        if (aFile.isDirectory()) {
            return true;
        }

        List<String> imageExtension = ImageParser.getSupportedExtensions();
        String filename = aFile.getName();
        for (String extension : imageExtension) {
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
        String imageLabel = label.getString("image").concat(" (");
        List<String> imageExtension = ImageParser.getSupportedExtensions();
        for (String extension : imageExtension) {
            imageLabel = imageLabel.concat("*.").concat(extension).concat(", ");
        }
        imageLabel = imageLabel.concat(")");
        return imageLabel;
    }
}
