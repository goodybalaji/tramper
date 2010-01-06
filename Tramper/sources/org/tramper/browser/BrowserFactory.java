package org.tramper.browser;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

/**
 * Return the right browser according to the protocol
 * @author Paul-Emile
 */
public class BrowserFactory {
    /** logger */
    private static Logger logger = Logger.getLogger(BrowserFactory.class);
    /** protocol/classname pairs */
    private static ResourceBundle browserByProtocol = ResourceBundle.getBundle("org.tramper.browser.browserByProtocol");
    /** hashtable of loaded browsers */
    private static Map<String, Browser> loadedBrowsers = new HashMap<String, Browser>();
    
    /**
     * 
     */
    private BrowserFactory() {
        super();
    }
    
    /**
     * instanciate and return the right browser
     * @return
     */
    public static Browser getBrowserByProtocol(URL url) throws BrowseException {
        
        String protocol = url.getProtocol();

        //get the class name corresponding to the protocol
        String className = null;
        try {
            className = browserByProtocol.getString(protocol);
        }
        catch (MissingResourceException e) {
            logger.error("Unknown protocol : "+protocol);
            throw new BrowseException("Unknown protocol : "+protocol);
        }

        Browser browser = loadedBrowsers.get(className);
        
        //the browser is not in the hashtable, we instanciate one
        if (browser == null) {
            //get a class loader and load the class
            ClassLoader classLoader = ClassLoader.getSystemClassLoader();
            Class<?> desiredClass = null;
            try {
                desiredClass = classLoader.loadClass(className);
            }
            catch (ClassNotFoundException e) {
                logger.error("Unknown class : "+className);
                throw new BrowseException("Unknown class : "+className);
            }
            
            //instanciate the class
            try {
                browser = (Browser)desiredClass.newInstance();
            }
            catch (InstantiationException e) {
                logger.error("Instantiation fail : "+className);
                throw new BrowseException("Instantiation class fail : "+className);
            }
            catch (IllegalAccessException e) {
                logger.error("Instantiation fail : "+className);
                throw new BrowseException("Instantiation class fail : "+className);
            }
            
            //put the new browser in the hashtable to reuse later
            loadedBrowsers.put(className, browser);
        }
        
        return browser;
    }
}
