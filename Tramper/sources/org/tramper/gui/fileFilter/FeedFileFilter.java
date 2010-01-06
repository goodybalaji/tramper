package org.tramper.gui.fileFilter;

import java.io.File;
import java.util.ResourceBundle;

import javax.swing.filechooser.FileFilter;

/**
 * @author Paul-Emile
 * 
 */
public class FeedFileFilter extends FileFilter {
    /**
     * All supported feed extensions
     */
    private String[] feedExtension = {".rss", ".RSS", ".atom", ".ATOM", ".xml", ".XML"};

    /**
     * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
     */
    @Override
    public boolean accept(File aFile) {
        if (aFile.isDirectory()) {
            return true;
        }
        String filename = aFile.getName();
        for (int i=0; i<feedExtension.length; i++) {
            if (filename.endsWith(feedExtension[i])) {
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
        return label.getString("feedFilter");
    }

}
