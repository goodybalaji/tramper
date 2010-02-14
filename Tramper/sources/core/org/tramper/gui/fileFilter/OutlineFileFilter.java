package org.tramper.gui.fileFilter;

import java.io.File;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.filechooser.FileFilter;

import org.tramper.outline.OpmlParser;

/**
 * @author Paul-Emile
 * 
 */
public class OutlineFileFilter extends FileFilter {

    /**
     * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
     */
    @Override
    public boolean accept(File aFile) {
        if (aFile.isDirectory()) {
            return true;
        }

        List<String> outlineExtension = OpmlParser.getSupportedExtensions();
        String filename = aFile.getName();
        for (String extension : outlineExtension) {
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
        String outlineLabel = label.getString("outline").concat(" (");
        List<String> outlineExtension = OpmlParser.getSupportedExtensions();
        for (String extension : outlineExtension) {
            outlineLabel = outlineLabel.concat("*.").concat(extension).concat(", ");
        }
        outlineLabel = outlineLabel.concat(")");
        return outlineLabel;
    }
}
