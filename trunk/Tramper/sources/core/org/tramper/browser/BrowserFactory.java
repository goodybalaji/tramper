package org.tramper.browser;

import java.net.URL;
import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

/**
 * Returns the right browser according to the given protocol.
 * @author Paul-Emile
 */
public class BrowserFactory {
    /** logger */
    private static Logger logger = Logger.getLogger(BrowserFactory.class);
    /** service provider loader for browser interface */
    private static ServiceLoader<Browser> browserLoader;
    /** lock preventing several threads to use the service loader concurrently */
    private static Lock lock;

    static {
	browserLoader = ServiceLoader.load(Browser.class);
	lock = new ReentrantLock();
    }
    
    /**
     * Returns a new browser following the protocol of the URL.
     * @return a new browser
     */
    public static Browser getBrowserByProtocol(URL url) throws BrowseException {
	lock.lock();
	browserLoader.reload();
	Iterator<Browser> browserIterator = browserLoader.iterator();
	while (browserIterator.hasNext()) {
	    try {
		Browser aBrowser = browserIterator.next();
        	if (aBrowser.isProtocolSupported(url)) {
        	    lock.unlock();
        	    return aBrowser;
        	}
	    } catch (ServiceConfigurationError e) {
		logger.error("Error when loading a service provider", e);
	    }
	}
        lock.unlock();
        logger.error("Unknown protocol : "+url);
        throw new BrowseException("Unknown protocol : "+url);
    }
}
