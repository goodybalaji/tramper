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
    /** get an input stream on the document */
    public InputStream openRead(URL docRef) throws BrowseException;
    /** get an output stream on the document */
    public OutputStream openWrite(URL docRef) throws BrowseException;
    /** get an input stream on the remote procedure */
    public InputStream openRead(URL docRef, Map<String, String> parameter) throws BrowseException;
    /** get the mime type of the document */
    public String getMimeType() throws BrowseException;
    /** get the file extension of the document */
    public String getExtension() throws BrowseException;
}
