package org.tramper.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.filechooser.FileFilter;

public class FileFilterByExtension extends FileFilter {

    private String documentType;
    private List<String> extensions = new ArrayList<String>();
    
    public FileFilterByExtension(String docType, List<String> extensions) {
	this.documentType = docType;
	this.extensions = extensions;
    }
    
    public void addExtensions(List<String> newExtensions) {
	extensions.addAll(newExtensions);
    }
    
    @Override
    public boolean accept(File aFile) {
        if (aFile.isDirectory()) {
            return true;
        }
        String filename = aFile.getName();
        for (String extension : extensions) {
            if (filename.toLowerCase().endsWith(extension.toLowerCase())) {
        	return true;
            }
        }
        return false;
    }

    @Override
    public String getDescription() {
        ResourceBundle label = ResourceBundle.getBundle("label");
        String audioLabel = label.getString(documentType).concat(" (");
        for (String extension : extensions) {
            audioLabel = audioLabel.concat("*.").concat(extension).concat(", ");
        }
        audioLabel = audioLabel.concat(")");
        return audioLabel;
    }
}
