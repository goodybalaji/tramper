package org.tramper.gui.fileFilter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.filechooser.FileFilter;

import org.tramper.webPage.HtmlParser;
import org.tramper.webPage.XhtmlParser;

/**
 * @author Paul-Emile
 * 
 */
public class WebPageFileFilter extends FileFilter {
    /**
     * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
     */
    @Override
    public boolean accept(File aFile) {
        if (aFile.isDirectory()) {
            return true;
        }

        List<String> webExtensions = new ArrayList<String>();
        List<String> htmlExtension = HtmlParser.getSupportedExtensions();
        webExtensions.addAll(htmlExtension);
        List<String> xhtmlExtension = XhtmlParser.getSupportedExtensions();
        webExtensions.addAll(xhtmlExtension);
        
        String filename = aFile.getName();
        for (String extension : webExtensions) {
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
        String webLabel = label.getString("webPage").concat(" (");
        List<String> webExtensions = new ArrayList<String>();
        List<String> htmlExtension = HtmlParser.getSupportedExtensions();
        webExtensions.addAll(htmlExtension);
        List<String> xhtmlExtension = XhtmlParser.getSupportedExtensions();
        webExtensions.addAll(xhtmlExtension);
        for (String extension : webExtensions) {
            webLabel = webLabel.concat("*.").concat(extension).concat(", ");
        }
        webLabel = webLabel.concat(")");
        return webLabel;
    }
}
