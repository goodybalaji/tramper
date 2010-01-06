package org.tramper.gui.fileFilter;

import java.io.File;
import java.util.ResourceBundle;

import javax.swing.filechooser.FileFilter;

/**
 * Display the speakable files in the file chooser
 * @author Paul-Emile
 */
public class AllFileFilter extends FileFilter {
    /**
     * All supported file extensions
     */
    private String[] allowedExtension = {".htm", ".HTM", ".html", ".HTML", ".rss", ".RSS", ".rdf", ".RDF", ".atom", ".ATOM", ".xml", ".XML", ".opml", ".OPML", ".mp3", ".MP3", ".ogg", ".OGG", ".ape", ".APE", ".spx", ".SPX", ".wav", ".WAV", ".au", ".AU", ".aif", ".AIF", ".aifc", ".AIFC", ".gif", ".GIF", ".jpg", ".JPG", ".jpeg", ".JPEG", ".png", ".PNG", ".avi", ".AVI", ".mov", ".MOV", ".qt", ".QT", ".mpe", ".MPE", ".mpg", ".MPG", ".mpeg", ".MPEG", ".m4v", ".M4V", ".mp4", ".MP4"};
    
    /**
     * 
     */
    public AllFileFilter() {
        super();
    }
    
    /**
     * 
     * @see java.io.FileFilter#accept(java.io.File)
     */
    public boolean accept(File aFile) {
        if (aFile.isDirectory()) {
            return true;
        }
        String filename = aFile.getName();
        for (int i=0; i<allowedExtension.length; i++) {
            if (filename.endsWith(allowedExtension[i])) {
        	return true;
            }
        }
        return false;
    }
    
    /**
     * 
     * @see javax.swing.filechooser.FileFilter#getDescription()
     */
    public String getDescription() {
        ResourceBundle label = ResourceBundle.getBundle("label");
        return label.getString("allFilter");
    }
}