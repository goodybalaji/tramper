package org.tramper.gui.viewer;

import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;
import org.tramper.doc.SimpleDocument;
import org.tramper.ui.RenderingException;

/**
 * Returns the right viewer for the document in parameter.
 * @author Paul-Emile
 */
public class ViewerFactory {
    /** logger */
    private static Logger logger = Logger.getLogger(ViewerFactory.class);
    /** service provider loader for viewer interface */
    private static ServiceLoader<Viewer> viewerLoader;
    /** lock preventing several threads to use the service loader concurrently */
    private static Lock lock;

    static {
	viewerLoader = ServiceLoader.load(Viewer.class);
	lock = new ReentrantLock();
    }
    
    /**
     * 
     * @param document
     * @return
     */
    public static Viewer getViewerByDocument(SimpleDocument document) throws RenderingException {
	lock.lock();
	viewerLoader.reload();
	Iterator<Viewer> viewerIterator = viewerLoader.iterator();
	while (viewerIterator.hasNext()) {
	    try {
		Viewer aViewer = viewerIterator.next();
		if (aViewer.isDocumentSupported(document)) {
		    aViewer.initializeViewer();
        	    lock.unlock();
		    return aViewer;
		}
	    } catch (ServiceConfigurationError e) {
		logger.error("Error when loading a service provider", e);
	    }
	}
	lock.unlock();
        logger.error("Unknown document: "+document);
        throw new RenderingException("Unknown document: "+document);
    }
}
