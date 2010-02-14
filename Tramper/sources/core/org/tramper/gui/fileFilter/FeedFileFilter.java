package org.tramper.gui.fileFilter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.filechooser.FileFilter;

import org.tramper.feed.AtomParser;
import org.tramper.feed.RssParser;

/**
 * 
 * @author Paul-Emile
 */
public class FeedFileFilter extends FileFilter {
    /**
     * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
     */
    @Override
    public boolean accept(File aFile) {
        if (aFile.isDirectory()) {
            return true;
        }
        List<String> feedExtensions = new ArrayList<String>();
        List<String> atomExtension = AtomParser.getSupportedExtensions();
        feedExtensions.addAll(atomExtension);
        List<String> rssExtension = RssParser.getSupportedExtensions();
        feedExtensions.addAll(rssExtension);
        
        String filename = aFile.getName();
        for (String extension : feedExtensions) {
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
        String feedLabel = label.getString("feed").concat(" (");
        List<String> feedExtensions = new ArrayList<String>();
        List<String> atomExtension = AtomParser.getSupportedExtensions();
        feedExtensions.addAll(atomExtension);
        List<String> rssExtension = RssParser.getSupportedExtensions();
        feedExtensions.addAll(rssExtension);
        for (String extension : feedExtensions) {
            feedLabel = feedLabel.concat("*.").concat(extension).concat(", ");
        }
        feedLabel = feedLabel.concat(")");
        return feedLabel;
    }
}
