package org.tramper.gui.fileFilter;

import java.io.File;
import java.util.ResourceBundle;

import javax.swing.filechooser.FileFilter;

/**
 * @author Paul-Emile
 * 
 */
public class WebPageFileFilter extends FileFilter {
    /**
     * All supported webpage extensions
     */
    private String[] webPageExtension = {".htm", ".HTM", ".html", ".HTML"};

    /**
     * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
     */
    @Override
    public boolean accept(File aFile) {
        if (aFile.isDirectory()) {
            return true;
        }
        
        String filename = aFile.getName();
        for (int i=0; i<webPageExtension.length; i++) {
            if (filename.endsWith(webPageExtension[i])) {
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
        return label.getString("webPageFilter");
    }

}
