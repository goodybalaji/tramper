package org.tramper.webPage;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.tramper.parser.ParsingException;
import org.w3c.dom.Document;

/**
 * XHTML parser
 * @author Paul-Emile
 */
public class XhtmlParser extends AbstractHtmlParser {
    /** logger */
    private Logger logger = Logger.getLogger(XhtmlParser.class);

    /**
     * 
     */
    public XhtmlParser() {
	super();
    }

    /**
     * 
     * @see org.tramper.parser.AbstractHtmlParser#makeDocument(java.io.InputStream)
     */
    protected Document makeDocument(InputStream inStream) throws ParsingException {
        DocumentBuilderFactory docBuildFactory = DocumentBuilderFactory.newInstance();
        docBuildFactory.setIgnoringComments(true);
        docBuildFactory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder aDocBuilder = null;
        try {
            aDocBuilder = docBuildFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            logger.error("XML document builder instanciation failed");
            throw new ParsingException("XML document builder instanciation failed", e);
        }
        Document aDoc = null;
        try {
            aDoc = aDocBuilder.parse(inStream);
        } catch (Exception e) {
            throw new ParsingException("Impossible to read the document", e);
        }
        return aDoc;
    }

    /**
     * 
     * @see org.tramper.parser.Parser#isExtensionSupported(java.lang.String)
     */
    public boolean isExtensionSupported(String extension) {
	if (extension.equalsIgnoreCase("xhtml")) {
	    return true;
	}
	return false;
    }

    /**
     * 
     * @see org.tramper.parser.Parser#parseMimeType(java.lang.String)
     */
    public boolean isMimeTypeSupported(String mimeType) {
	if (mimeType.equalsIgnoreCase("text/xhtml")) {
	    return true;
	} else if (mimeType.equalsIgnoreCase("application/xhtml+xml")) {
	    return true;
	}
	return false;
    }

    /**
     * 
     * @return
     */
    public List<String> getSupportedExtensions() {
	List<String> extensions = new ArrayList<String>();
	extensions.add("xhtml");
	return extensions;
    }
}
