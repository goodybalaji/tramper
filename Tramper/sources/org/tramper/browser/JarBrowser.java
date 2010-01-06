package org.tramper.browser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLKeyException;

import org.apache.log4j.Logger;

/**
 * @author Paul-Emile
 * Load documents from a jar connection
 */
public class JarBrowser implements Browser {
    /** logger */
    private Logger logger = Logger.getLogger(JarBrowser.class);
    /** Mime type */
    private String mimeType;
    /** charset */
    private String charset;
    /** File extension */
    private String extension;

    /**
     * 
     */
    public JarBrowser() {
        super();
    }

    /**
     * @see org.tramper.browser.Browser#getExtension()
     */
    public String getExtension() throws BrowseException {
        if (extension == null) {
            throw new BrowseException("no extension");
        }
	return extension;
    }

    /**
     * @see org.tramper.browser.Browser#getMimeType()
     */
    public String getMimeType() throws BrowseException {
        if (mimeType == null) {
            throw new BrowseException("no mime type");
        }
	return mimeType;
    }

    /**
     * @return charset.
     */
    public String getCharset() throws BrowseException {
        if (charset == null) {
            throw new BrowseException("no charset");
        }
        return charset;
    }

    /**
     * @see org.tramper.browser.Browser#openRead(java.net.URL)
     */
    public InputStream openRead(URL url) throws BrowseException {
        
        JarURLConnection aConnection = null;
        try {
            aConnection = (JarURLConnection)url.openConnection();
        }
        catch (IOException e) {
            logger.error("impossible to open a connection to "+url);
            throw new BrowseException("impossible to open a connection to "+url, e);
        }
        aConnection.setDoInput(true);
        aConnection.setDoOutput(false);
        //We want to use the cache if possible
        aConnection.setUseCaches(true);
        //Allow the user to interact during a HTTP request/response. How ?
        aConnection.setAllowUserInteraction(true);
        InputStream inStream = null;
        try {
            aConnection.connect();
            inStream = aConnection.getInputStream();
        }
        catch (SSLHandshakeException e) {
            logger.error("client and server could not negotiate the desired level of security", e);
            throw new BrowseException("client and server could not negotiate the desired level of security", e);
        }
        catch (SSLKeyException e) {
            logger.error("misconfiguration of the server or client SSL certificate and private key", e);
            throw new BrowseException("misconfiguration of the server or client SSL certificate and private key", e);
        }
        catch (IOException e) {
            logger.error("impossible to open a connection to "+url, e);
            throw new BrowseException("impossible to open a connection to "+url, e);
        }
        
        //get the mime type
        mimeType = aConnection.getContentType();
        //If there is charset, get it
        int semiColumnIndex = mimeType.indexOf(";");
        if (semiColumnIndex != -1) {
            int equalIndex = mimeType.indexOf("=");
            if (equalIndex != -1) {
                charset = mimeType.substring(equalIndex+1);
            }
            mimeType = mimeType.substring(0, semiColumnIndex);
        }
        logger.debug("mime type : "+mimeType);
        
        //get the extension of the file if any
        String fileName = url.getPath();
        int pointIndex = fileName.lastIndexOf(".");
        if (pointIndex != -1) {
            extension = fileName.substring(pointIndex+1, fileName.length());
        }
        else {
            extension = null;
        }
        logger.debug("extension : "+extension);
        
        return inStream;
    }

    /**
     * @see org.tramper.browser.Browser#openRead(java.net.URL, java.util.Map)
     */
    public InputStream openRead(URL url, Map<String, String>  parameter) throws BrowseException {
        
        JarURLConnection aConnection = null;
        try {
            aConnection = (JarURLConnection)url.openConnection();
        }
        catch (IOException e) {
            logger.error("impossible to open a connection to "+url);
            throw new BrowseException("impossible to open a connection to "+url, e);
        }
        aConnection.setDoInput(true);
        aConnection.setDoOutput(true);
        //We want to use the cache if possible
        aConnection.setUseCaches(true);
        //Allow the user to interact during a HTTP request/response. How ?
        aConnection.setAllowUserInteraction(true);
        InputStream inStream = null;
        try {
            aConnection.connect();
            
            //send the parameters in the body of the request
            OutputStream outStream = aConnection.getOutputStream();
            Iterator<Map.Entry<String, String>> paramIt = parameter.entrySet().iterator();
            while (paramIt.hasNext()) {
                Map.Entry<String, String> entry = paramIt.next();
                String key = entry.getKey();
                String value = entry.getValue();
                String pair = key+"="+value+"&";
                outStream.write(pair.getBytes());
            }
            outStream.flush();
            outStream.close();
            
            inStream = aConnection.getInputStream();
        }
        catch (SSLHandshakeException e) {
            logger.error("Problème de sécurité avec le serveur", e);
            throw new BrowseException("Problème de sécurité avec le serveur", e);
        }
        catch (IOException e) {
            logger.error("impossible to open a connection to "+url, e);
            throw new BrowseException("impossible to open a connection to "+url, e);
        }
        
        //get the mime type
        mimeType = aConnection.getContentType();
        //If there is charset, get it
        int semiColumnIndex = mimeType.indexOf(";");
        if (semiColumnIndex != -1) {
            int equalIndex = mimeType.indexOf("=");
            if (equalIndex != -1) {
                charset = mimeType.substring(equalIndex+1);
            }
            mimeType = mimeType.substring(0, semiColumnIndex);
        }
        logger.debug("mime type : "+mimeType);
        
        //get the extension of the file if any
        String fileName = url.getPath();
        int pointIndex = fileName.lastIndexOf(".");
        if (pointIndex != -1) {
            extension = fileName.substring(pointIndex+1, fileName.length());
        }
        else {
            extension = null;
        }
        logger.debug("extension : "+extension);

        return inStream;
    }

    /**
     * @see org.tramper.browser.Browser#openWrite(java.net.URL)
     */
    public OutputStream openWrite(URL url) throws BrowseException {
        
        JarURLConnection aConnection = null;
        try {
            aConnection = (JarURLConnection)url.openConnection();
        }
        catch (IOException e) {
            logger.error("impossible to open a connection to "+url);
            throw new BrowseException("impossible to open a connection to "+url, e);
        }
        aConnection.setDoInput(false);
        aConnection.setDoOutput(true);
        //We want to use the cache if possible
        aConnection.setUseCaches(true);
        //Allow the user to interact during a HTTP request/response. How ?
        aConnection.setAllowUserInteraction(true);
        OutputStream outStream = null;
        try {
            aConnection.connect();
            outStream = aConnection.getOutputStream();
        }
        catch (SSLHandshakeException e) {
            logger.error("Problème de sécurité avec le serveur", e);
            throw new BrowseException("Problème de sécurité avec le serveur", e);
        }
        catch (IOException e) {
            logger.error("impossible to open a connection to "+url, e);
            throw new BrowseException("impossible to open a connection to "+url, e);
        }
        
        //get the extension of the file if any
        String fileName = url.getPath();
        int pointIndex = fileName.lastIndexOf(".");
        if (pointIndex != -1) {
            extension = fileName.substring(pointIndex+1, fileName.length());
        }
        else {
            extension = null;
        }
        
        return outStream;
    }

}
