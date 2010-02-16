package org.tramper.image;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.tramper.doc.ImageDocument;
import org.tramper.doc.SimpleDocument;
import org.tramper.parser.Parser;
import org.tramper.parser.ParsingException;

/**
 * Image parser. Extracts the metadata.
 * @author Paul-Emile
 */
public class ImageParser implements Parser {
    /** logger */
    private Logger logger = Logger.getLogger(ImageParser.class);

    /**
     * @see org.tramper.parser.Parser#parse(java.io.InputStream, URL)
     */
    public SimpleDocument parse(InputStream inStream, URL url) throws ParsingException {
	ImageDocument doc = new ImageDocument();
	try {
	    BufferedImage img = ImageIO.read(inStream);
	    doc.setImage(img);
	    String[] propertyNames = img.getPropertyNames();
	    logger.debug("properties:"+String.valueOf(propertyNames));
	    int width = img.getWidth();
	    doc.setWidth(width);
	    int height = img.getHeight();
	    doc.setHeight(height);
	} catch (IOException e) {
	    logger.error(e.getMessage(), e);
	}
	
        //if no metadata, get the file name as title
        String path = url.getPath();
        int lastIndexSlash = path.lastIndexOf("/");
        if (lastIndexSlash != -1) {
            path = path.substring(lastIndexSlash+1);
        }
        doc.setTitle(path);
	return doc;
    }

    /**
     * @see org.tramper.parser.Parser#unparse(java.io.OutputStream, org.tramper.doc.SimpleDocument, URL)
     */
    public void unparse(OutputStream outStream, SimpleDocument document, URL url) throws ParsingException {
    }

    /**
     * 
     * @see org.tramper.parser.Parser#isExtensionSupported(java.lang.String)
     */
    public boolean isExtensionSupported(String extension) {
	String[] extensions = ImageIO.getReaderFileSuffixes();
	for (String anExtension : extensions) {
	    if (anExtension.equalsIgnoreCase(extension)) {
		return true;
	    }
	}
	return false;
    }

    /**
     * 
     * @see org.tramper.parser.Parser#isMimeTypeSupported(java.lang.String)
     */
    public boolean isMimeTypeSupported(String mimeType) {
	String[] mimeTypes = ImageIO.getReaderMIMETypes();
	for (String anMimeType : mimeTypes) {
	    if (anMimeType.equalsIgnoreCase(mimeType)) {
		return true;
	    }
	}
	return false;
    }
    
    /**
     * 
     * @return
     */
    public List<String> getSupportedExtensions() {
	String[] extensions = ImageIO.getReaderFileSuffixes();
	return Arrays.asList(extensions);
    }
    
    /**
     * 
     * @return
     */
    public SimpleDocument getSupportedDocument() {
	return new ImageDocument();
    }
}
