package org.tramper.conductor;

import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;
import org.tramper.doc.SimpleDocument;

/**
 * A factory providing a new conductor for a given document.
 * @author Paul-Emile
 */
public class ConductorFactory {
    /** logger */
    private static Logger logger = Logger.getLogger(ConductorFactory.class);
    /** service provider loader for conductor interface */
    private static ServiceLoader<Conductor> conductorLoader;
    /** lock preventing several threads to use the service loader concurrently */
    private static Lock lock;

    static {
	conductorLoader = ServiceLoader.load(Conductor.class);
	lock = new ReentrantLock();
    }
    
    public static Conductor getConductorByDocument(SimpleDocument document) throws ConductException {
	lock.lock();
	conductorLoader.reload();
	Iterator<Conductor> conductorIterator = conductorLoader.iterator();
	while (conductorIterator.hasNext()) {
	    try {
		Conductor aConductor = conductorIterator.next();
        	if (aConductor.isDocumentSupported(document)) {
        	    lock.unlock();
        	    return aConductor;
        	}
	    } catch (ServiceConfigurationError e) {
		logger.error("Error when loading a service provider", e);
	    }
	}
        lock.unlock();
        logger.error("Unknown document: "+document);
        throw new ConductException("Unknown document: "+document);
    }
}
