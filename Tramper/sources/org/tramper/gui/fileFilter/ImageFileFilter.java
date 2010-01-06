package org.tramper.gui.fileFilter;

import java.io.File;
import java.util.ResourceBundle;

import javax.swing.filechooser.FileFilter;

/**
 * @author Paul-Emile
 * 
 */
public class ImageFileFilter extends FileFilter {
    /**
     * All supported image extensions
     */
    private String[] imageExtension = {".gif", ".GIF", ".jpg", ".JPG", ".jpeg", ".JPEG", ".png", ".PNG"};

    /**
     * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
     */
    @Override
    public boolean accept(File aFile) {
        if (aFile.isDirectory()) {
            return true;
        }
        
        String filename = aFile.getName();
        for (int i=0; i<imageExtension.length; i++) {
            if (filename.endsWith(imageExtension[i])) {
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
        return label.getString("imageFilter");
    }

}
