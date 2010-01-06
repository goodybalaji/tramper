package org.tramper.parser;

import java.io.InputStream;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Returns the right parser following a mime type or a file name
 * @author Paul-Emile
 */
public class ParserFactory {
    /** logger */
    private static Logger logger = Logger.getLogger(ParserFactory.class);
    /** mimetype/classname pairs */
    private static ResourceBundle parserByMimeType = ResourceBundle.getBundle("org.tramper.parser.parserByMimeType");
    /** filename/classname pairs */
    private static ResourceBundle parserByExtension = ResourceBundle.getBundle("org.tramper.parser.parserByExtension");
    /** documentroot/classname pairs */
    private static ResourceBundle parserByDocumentRoot = ResourceBundle.getBundle("org.tramper.parser.parserByDocumentRoot");
    /** hashtable of loaded parsers */
    //private static HashMap loadedParsers = new HashMap();
    
    /**
     * 
     */
    private ParserFactory() {
        super();
    }
    
    /**
     * returns the right parser following the given mime type
     * @param mimeType
     * @return
     * @throws Exception
     */
    public static Parser getParserByMimeType(String mimeType) throws ParsingException {
        //get the class name corresponding to the mime type
        String className = null;
        try {
            className = parserByMimeType.getString(mimeType);
        }
        catch (MissingResourceException e) {
            logger.info("Unknown mime type : "+mimeType);
            throw new ParsingException("Unknown mime type : "+mimeType);
        }

        Parser parser = getParserByClassName(className);
        
        return parser;
    }
    
    /**
     * returns the right parser following the given file extension
     * @param extension file's extension
     * @return the right parser
     * @throws ParsingException
     */
    public static Parser getParserByExtension(String extension) throws ParsingException {
        //get the class name corresponding to the file name
        String className = null;
        try {
            className = parserByExtension.getString(extension);
        }
        catch (MissingResourceException e) {
            logger.info("Unknown extension : "+extension);
            throw new ParsingException("Unknown extension : "+extension);
        }

        Parser parser = getParserByClassName(className);
        
        return parser;
    }
    
    /**
     * returns the right parser following the given input stream
     * @param inStream input stream from which reading the head of the file
     * @return the right parser
     * @throws ParsingException
     */
    public static Parser getParserByDocumentRoot(InputStream inStream) throws ParsingException {
        DocumentBuilderFactory docBuildFactory = DocumentBuilderFactory.newInstance();
        Document aDoc = null;
        try {
            DocumentBuilder aDocBuilder = docBuildFactory.newDocumentBuilder();
            aDoc = aDocBuilder.parse(inStream);
        }
        catch (Exception e) {
            throw new ParsingException("document unreadable", e);
        }
        Element docRoot = aDoc.getDocumentElement();
        String docRootTagName = docRoot.getTagName();
        String className = null;
        try {
            className = parserByDocumentRoot.getString(docRootTagName);
        }
        catch (MissingResourceException e) {
            logger.info("Unknown document root : "+docRootTagName);
            throw new ParsingException("Unknown document root : "+docRootTagName);
        }

        Parser parser = getParserByClassName(className);

        return parser;
    }
    
    /**
     * Instanciate a Parser from a class name
     * @param className
     * @return a Parser
     * @throws ParsingException if class not found or instanciation forbidden
     */
    protected static Parser getParserByClassName(String className) throws ParsingException {
        //Parser parser = (Parser)loadedParsers.get(className);
	Parser parser = null;
        
        //the parser is not in the hashtable, we instanciate one
        //if (parser == null) {
            //get a class loader and load the class
            ClassLoader classLoader = ClassLoader.getSystemClassLoader();
            Class<?> desiredClass = null;
            try {
                desiredClass = classLoader.loadClass(className);
            }
            catch (ClassNotFoundException e) {
                logger.error("Unknown class : "+className);
                throw new ParsingException("Unknown class : "+className);
            }
            
            //instanciate the class
            try {
                parser = (Parser)desiredClass.newInstance();
            }
            catch (InstantiationException e) {
                logger.error("Instantiation fail : "+className);
                throw new ParsingException("Instantiation class fail : "+className);
            }
            catch (IllegalAccessException e) {
                logger.error("Instantiation fail : "+className);
                throw new ParsingException("Instantiation class fail : "+className);
            }
            
            //put the new parser in the hashtable for reuse later
            //loadedParsers.put(className, parser);
        //}
        
        return parser;
    }
}
