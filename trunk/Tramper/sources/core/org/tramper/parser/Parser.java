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
     * Parse a input stream into a document
     * @param inStream input stream
     * @param url URL of the document to parse
     * @return a document
     * @throws ParsingException if an I/O error occurs
     */
    public SimpleDocument parse(InputStream inStream, URL url) throws ParsingException;
    /**
     * Unparse a document into a output stream
     * @param document a document
     * @param url URL where to unparse the document
     * @param inStream input stream
     * @throws ParsingException if an I/O error occurs
     */
    public void unparse(OutputStream outStream, SimpleDocument document, URL url) throws ParsingException;
    /**
     * 
     * @param mimeType
     * @return
     */
    public boolean isMimeTypeSupported(String mimeType);
    /**
     * 
     * @param extension
     * @return
     */
    public boolean isExtensionSupported(String extension);
}
