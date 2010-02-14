package org.tramper.browser;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Map;

import org.tramper.browser.BrowseException;


/**
 * Browse a system to find a resource and open a stream 
 * @author Paul-Emile
 */
public interface Browser {
    /**
     * Returns an input stream from the URL.
     * @param docRef the URL of the document
     * @return an input stream
     * @throws BrowseException
     */
    public InputStream openRead(URL docRef) throws BrowseException;
    /**
     * Returns an output stream to the URL.
     * @param docRef the URL of the document
     * @return an output stream
     * @throws BrowseException
     */
    public OutputStream openWrite(URL docRef) throws BrowseException;
    /**
     * Simulates a remote procedure call.
     * @param docRef the URL of the document
     * @param parameter parameters of the called procedure
     * @return an input stream
     * @throws BrowseException
     */
    public InputStream openRead(URL docRef, Map<String, String> parameter) throws BrowseException;
    /**
     * Returns the MIME type of the document.
     * @return a MIME type
     * @throws BrowseException
     */
    public String getMimeType() throws BrowseException;
    /**
     * Returns the file extension of the document.
     * @return a file extension
     * @throws BrowseException
     */
    public String getExtension() throws BrowseException;
    /**
     * Indicates if the browser can return a stream from this URL following its protocol.
     * @param URL whose protocol is tested
     * @return true if the browser can open a stream from this URL, false otherwise
     */
    public boolean isProtocolSupported(URL url);
}
