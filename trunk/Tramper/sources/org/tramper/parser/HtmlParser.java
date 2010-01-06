package org.tramper.parser;

import java.io.InputStream;

import org.apache.log4j.Logger;
import org.cyberneko.html.parsers.DOMParser;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * HTML parser
 * @author Paul-Emile
 */
public class HtmlParser extends AbstractHtmlParser {
    /** document builder */
    private DOMParser parser;
    /** logger */
    private Logger logger = Logger.getLogger(HtmlParser.class);
    
    /**
     * 
     */
    public HtmlParser() {
        parser = new DOMParser();
    }
    
    protected Document makeDocument(InputStream inStream) throws ParsingException {
        InputSource source = new InputSource(inStream);
        try {
            //We can launch several loadings at the same time,
            //and the DocumentBuilder class is not thread-safe
            synchronized (this) {
                parser.parse(source);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ParsingException("Impossible to read the document", e);
        }
        Document htmlDoc = parser.getDocument();
        return htmlDoc;
    }
}