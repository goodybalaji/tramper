package org.tramper.parser;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import org.tramper.doc.SimpleDocument;


/**
 * Parser of streams to documents
 * @author Paul-Emile
 */
public interface Parser {
    /**
     * Parse a input stream into a speakable document
     * @param inStream input stream
     * @return a document
     * @throws ParsingException if an I/O error occurs
     */
    public SimpleDocument parse(InputStream inStream) throws ParsingException;
    /**
     * Unparse a speakable document into a output stream
     * @param inStream input stream
     * @param document a document
     * @throws ParsingException if an I/O error occurs
     */
    public void unparse(OutputStream outStream, SimpleDocument document) throws ParsingException;
    /**
     * set the document's url
     * @param url document's url
     */
    public void setUrl(URL url);
}
