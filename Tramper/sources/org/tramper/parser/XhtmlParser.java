package org.tramper.parser;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

/**
 * XHTML parser
 * @author Paul-Emile
 */
public class XhtmlParser extends AbstractHtmlParser {
    /** logger */
    private Logger logger = Logger.getLogger(XhtmlParser.class);
    /** XML document builder */
    private DocumentBuilder aDocBuilder;

    /**
     * Initialize the XML document builder 
     */
    public XhtmlParser() {
        DocumentBuilderFactory docBuildFactory = DocumentBuilderFactory.newInstance();
        docBuildFactory.setIgnoringComments(true);
        docBuildFactory.setValidating(false);
        docBuildFactory.setNamespaceAware(false);
        docBuildFactory.setIgnoringElementContentWhitespace(true);
        try {
            aDocBuilder = docBuildFactory.newDocumentBuilder();
        }
        catch (ParserConfigurationException e) {
            logger.error("XML document builder instanciation failed", e);
        }
    }

    protected Document makeDocument(InputStream inStream) throws ParsingException {
        Document aDoc = null;
        try {
            //We can launch several loadings at the same time,
            //and the DocumentBuilder class is not thread-safe
            synchronized (this) {
                aDoc = aDocBuilder.parse(inStream);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ParsingException("Impossible to read the document", e);
        }
        return aDoc;
    }
}
