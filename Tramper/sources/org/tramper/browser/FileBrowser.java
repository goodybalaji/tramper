package org.tramper.browser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.tramper.browser.BrowseException;


/**
 * Browse the local system to open a stream from a file
 * @author Paul-Emile
 */
public class FileBrowser implements Browser {
    /** logger */
    private Logger logger = Logger.getLogger(FileBrowser.class);
    /** Mime type */
    private String mimeType;
    /** File name */
    private String extension;

    /**
     * 
     */
    public FileBrowser() {
        super();
    }

    /**
     * Find the file on the local system and open a byte stream 
     * @see org.tramper.browser.Browser#openRead(java.lang.String)
     */
    public InputStream openRead(URL url) throws BrowseException {
        
        String file = url.getPath();
        try {
            file = URLDecoder.decode(file, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            //should not occur
            logger.warn("file url decoding failed due to bad character encoding");
        }
        File aFile = new File(file);
        FileInputStream inStream = null;
        if (aFile.exists()) {
            if (aFile.isFile()) {
                if (aFile.canRead()) {
                    try {
                        inStream = new FileInputStream(aFile);
                    }
                    catch (IOException e) {
                        throw new BrowseException("I/O error when opening the file : "+url+" "+e.getMessage(), e);
                    }
                }
                else {
                    throw new BrowseException("Unauthorized to read the file : "+url);
                }
            }
            else {
                throw new BrowseException("This file is a directory : "+url);
            }
        }
        else {
            throw new BrowseException("This file doesn't exist : "+url);
        }
        
        String fileName = aFile.getName();
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex != -1) {
            extension = fileName.substring(dotIndex+1, fileName.length());
        } else {
            extension = null;
        }
        
        if (extension != null) {
            mimeType = getMimeTypeByExtension(extension);
        }
        
        return inStream;
    }
    
    /**
     * 
     * @see org.tramper.browser.Browser#openWrite(java.net.URL)
     */
    public OutputStream openWrite(URL url) throws BrowseException {
        
        String file = url.getPath();
        try {
            file = URLDecoder.decode(file, "UTF-8");
        }
        catch (UnsupportedEncodingException e1) {
            //should not occur
            logger.warn("file url decoding failed due to bad character encoding");
        }
        File aFile = new File(file);
        FileOutputStream outStream = null;
        try {
            outStream = new FileOutputStream(aFile);
        }
        catch (IOException e) {
            throw new BrowseException("I/O error when opening the file : "+url+" "+e.getMessage(), e);
        }
        
        String fileName = aFile.getName();
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex != -1) {
            extension = fileName.substring(dotIndex+1);
        } else {
            extension = null;
        }

        if (extension != null) {
            mimeType = getMimeTypeByExtension(extension);
        }
        
        return outStream;
    }
    
    /**
     * 
     * @see org.tramper.browser.Browser#openRead(java.net.URL, java.util.Map)
     */
    public InputStream openRead(URL url, Map<String, String> parameter) throws BrowseException {
        
        String file = url.getPath();
        try {
            file = URLDecoder.decode(file, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            //should not occur
            logger.warn("file url decoding failed due to bad character encoding");
        }
        File aFile = new File(file);
        InputStream inStream = null;
        if (aFile.exists()) {
            if (aFile.isFile()) {
                Runtime run = Runtime.getRuntime();
                String[] command = new String[parameter.size() + 1];
                command[0] = file;
                Iterator<String> paramIt = parameter.values().iterator();
                int i=1;
                while (paramIt.hasNext()) {
                    command[i] = paramIt.next();
                    i++;
                }
                try {
                    Process process = run.exec(command);
                    inStream = process.getInputStream();
                }
                catch (IOException e) {
                    logger.error("");
                }
            }
            else {
                throw new BrowseException("This file is a directory : "+url);
            }
        }
        else {
            throw new BrowseException("This file doesn't exist : "+url);
        }
        
        return inStream;
    }
    
    /**
     * return the mime type of the current file
     * @return mimetType.
     */
    public String getMimeType() throws BrowseException {
        if (mimeType == null) {
            throw new BrowseException("no mime type");
        }
        return mimeType;
    }

    /**
     * return the extension of the current file
     * @return extension.
     */
    public String getExtension() throws BrowseException {
        if (extension == null) {
            throw new BrowseException("no extension");
        }
        return extension;
    }
    
    /**
     * 
     * @param extension
     * @return
     */
    public String getMimeTypeByExtension(String extension) {
	ResourceBundle bundle = ResourceBundle.getBundle("org.tramper.browser.mimeTypeByExtension");
	return bundle.getString(extension.toLowerCase());
    }
}
