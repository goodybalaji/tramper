package org.tramper.webPage;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.cyberneko.html.parsers.DOMParser;
import org.tramper.parser.ParsingException;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * HTML parser.
 * @author Paul-Emile
 */
public class HtmlParser extends AbstractHtmlParser {
    /** logger */
    private Logger logger = Logger.getLogger(HtmlParser.class);
    
    /**
     * 
     */
    public HtmlParser() {
	super();
    }
    
    /**
     * 
     * @see org.tramper.parser.AbstractHtmlParser#makeDocument(java.io.InputStream)
     */
    protected Document makeDocument(InputStream inStream) throws ParsingException {
        InputSource source = new InputSource(inStream);
        DOMParser parser = new DOMParser();
        try {
            parser.parse(source);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ParsingException("Impossible to read the document", e);
        }
        Document htmlDoc = parser.getDocument();
        return htmlDoc;
    }

    /**
     * 
     * @see org.tramper.parser.Parser#isExtensionSupported(java.lang.String)
     */
    public boolean isExtensionSupported(String extension) {
	if (extension.equalsIgnoreCase("htm")) {
	    return true;
	} else if (extension.equalsIgnoreCase("html")) {
	    return true;
	}
	return false;
    }

    /**
     * 
     * @see org.tramper.parser.Parser#parseMimeType(java.lang.String)
     */
    public boolean isMimeTypeSupported(String mimeType) {
	if (mimeType.equalsIgnoreCase("text/html")) {
	    return true;
	}
	return false;
    }

    /**
     * 
     * @return
     */
    public static List<String> getSupportedExtensions() {
	List<String> extensions = new ArrayList<String>();
	extensions.add("htm");
	extensions.add("html");
	return extensions;
    }
}