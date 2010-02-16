package org.tramper.parser;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;
import org.tramper.gui.FileFilterByExtension;

/**
 * Returns the right parser following a MIME type or a file extension.
 * @author Paul-Emile
 */
public class ParserFactory {
    /** logger */
    private static Logger logger = Logger.getLogger(ParserFactory.class);
    /** service provider loader for parser interface */
    private static ServiceLoader<Parser> parserLoader;
    /** lock preventing several threads to use the service loader concurrently */
    private static Lock lock;
    
    static {
	parserLoader = ServiceLoader.load(Parser.class);
	lock = new ReentrantLock();
    }
    
    /**
     * returns the right parser following the given MIME type
     * @param mimeType
     * @return
     * @throws Exception
     */
    public static Parser getParserByMimeType(String mimeType) throws ParsingException {
	lock.lock();
	Iterator<Parser> parserIterator = parserLoader.iterator();
	while (parserIterator.hasNext()) {
	    try {
        	Parser aParser = parserIterator.next();
        	if (aParser.isMimeTypeSupported(mimeType)) {
        	    lock.unlock();
        	    return aParser;
        	}
	    } catch (ServiceConfigurationError e) {
		logger.error("Error when loading a service provider", e);
	    }
	}
	lock.unlock();
        logger.error("Unknown mime type : "+mimeType);
        throw new ParsingException("Unknown mime type : "+mimeType);
    }
    
    /**
     * returns the right parser following the given file extension
     * @param extension file's extension
     * @return the right parser
     * @throws ParsingException
     */
    public static Parser getParserByExtension(String extension) throws ParsingException {
	lock.lock();
	Iterator<Parser> parserIterator = parserLoader.iterator();
	while (parserIterator.hasNext()) {
	    try {
		Parser aParser = parserIterator.next();
		if (aParser.isExtensionSupported(extension)) {
        	    lock.unlock();
		    return aParser;
		}
	    } catch (ServiceConfigurationError e) {
		logger.error("Error when loading a service provider", e);
	    }
	}
	lock.unlock();
        logger.error("Unknown extension: "+extension);
        throw new ParsingException("Unknown extension: "+extension);
    }
    
    public static Iterator<FileFilterByExtension> getFileFiltersByExtension() {
	Map<String, FileFilterByExtension> fileFilterMap = new HashMap<String, FileFilterByExtension>();
	lock.lock();
	Iterator<Parser> parserIterator = parserLoader.iterator();
	while (parserIterator.hasNext()) {
	    try {
		Parser aParser = parserIterator.next();
		List<String> extensions = aParser.getSupportedExtensions();
		String documentType = aParser.getSupportedDocument().toString();
		if (fileFilterMap.containsKey(documentType)) {
		    fileFilterMap.get(documentType).addExtensions(extensions);
		} else {
		    FileFilterByExtension aFileFilter = new FileFilterByExtension(documentType, extensions);
		    fileFilterMap.put(documentType, aFileFilter);
		}
	    } catch (ServiceConfigurationError e) {
		logger.error("Error when loading a service provider", e);
	    }
	}
	lock.unlock();
	return fileFilterMap.values().iterator();
    }
}
