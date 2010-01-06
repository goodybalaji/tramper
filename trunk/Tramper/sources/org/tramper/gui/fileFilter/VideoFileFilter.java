package org.tramper.gui.fileFilter;

import java.io.File;
import java.util.ResourceBundle;

import javax.swing.filechooser.FileFilter;

/**
 * 
 * @author Paul-Emile
 */
public class VideoFileFilter extends FileFilter {
    /**
     * All supported video extensions
     */
    private String[] videoExtension = {".avi", ".AVI", ".mov", ".MOV", ".qt", ".QT", ".mpe", ".MPE", ".mpg", ".MPG", ".mpeg", ".MPEG", ".m4v", ".M4V", ".mp4", ".MP4"};
    
    /**
     * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
     */
    @Override
    public boolean accept(File aFile) {
        if (aFile.isDirectory()) {
            return true;
        }
        
        String filename = aFile.getName();
        for (int i=0; i<videoExtension.length; i++) {
            if (filename.endsWith(videoExtension[i])) {
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
        return label.getString("videoFilter");
    }
}
