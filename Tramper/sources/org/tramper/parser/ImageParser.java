package org.tramper.parser;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.tramper.doc.ImageDocument;
import org.tramper.doc.SimpleDocument;

/**
 * Image parser. Extracts the metadata.
 * @author Paul-Emile
 */
public class ImageParser implements Parser {
    /** logger */
    private Logger logger = Logger.getLogger(ImageParser.class);
    /** url */
    protected URL url;

    /**
     * @see org.tramper.parser.Parser#setUrl(java.lang.String)
     */
    public void setUrl(URL url) {
	this.url = url;
    }

    /**
     * @see org.tramper.parser.Parser#parse(java.io.InputStream)
     */
    public SimpleDocument parse(InputStream inStream) throws ParsingException {
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
     * @see org.tramper.parser.Parser#unparse(java.io.OutputStream, org.tramper.doc.SimpleDocument)
     */
    public void unparse(OutputStream outStream, SimpleDocument document) throws ParsingException {
    }
}
