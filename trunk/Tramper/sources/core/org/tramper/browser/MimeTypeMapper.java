package org.tramper.browser;

import javax.activation.MimetypesFileTypeMap;

/**
 * Gives the MIME type matching an extension.
 * Uses the class MimetypesFileTypeMap from java activation.
 * @author Paul-Emile
 */
public class MimeTypeMapper {
    /** the singleton */
    private static MimeTypeMapper instance;
    /** MIME type extension map */
    private MimetypesFileTypeMap mimeTypeMap;
    
    /**
     * Initializes the MimetypesFileTypeMap object just once on demand.
     */
    private MimeTypeMapper() {
        mimeTypeMap = new MimetypesFileTypeMap();
    }
    
    /**
     * Allows to load the different MIME type - extensions files only once.
     * @return the singleton
     */
    public static MimeTypeMapper getInstance() {
	if (instance == null) {
	    instance = new MimeTypeMapper();
	}
	return instance;
    }
    
    /**
     * Returns the MIME type
     * @param url an URL, or path, or filename, or extension
     * @return the MIME type
     */
    public String getMimeType(String url) {
	return mimeTypeMap.getContentType(url);
    }
}
