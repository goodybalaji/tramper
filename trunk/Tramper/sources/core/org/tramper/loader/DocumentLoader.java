package org.tramper.loader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.tramper.browser.BrowseException;
import org.tramper.browser.Browser;
import org.tramper.browser.BrowserFactory;
import org.tramper.doc.SimpleDocument;
import org.tramper.doc.Target;
import org.tramper.parser.Parser;
import org.tramper.parser.ParserFactory;
import org.tramper.parser.ParsingException;

/**
 * Document downloader starting a thread
 * @author Paul-Emile
 */
public class DocumentLoader implements Loader, Runnable {
    /** logger */
    private static Logger logger = Logger.getLogger(DocumentLoader.class);
    /** flag to stop the current loading (break from the thread) */
    private boolean loadingStopped = false;
    /** url of the resource to load */
    private String url;
    /** loading listener list */
    private List<LoadingListener> loadingListener;
    /** loading thread */ 
    private Thread aLoading;
    /** loading type */
    private int loadingType;
    /** target where the document should be renderer */
    private Target target;
    /** document to upload */
    private SimpleDocument uploadDocument;
    /** call parameters */
    private Map<String, String> parameter;
    
    /**
     * 
     */
    public DocumentLoader() {
        super();
        loadingListener = new ArrayList<LoadingListener>();
    }

    /**
     * @return url.
     */
    public String getUrl() {
        return this.url;
    }

    /**
     * @see org.tramper.loader.Loader#download(java.lang.String, java.lang.Object)
     */
    public void download(String url, Target target) {
        this.url = url;
        this.target = target;
        //stop the thread if already running
        if (this.isRunning()) {
            this.stop();
        }
        loadingType = DOWNLOAD;
        aLoading = new Thread(this, "loading");
        aLoading.start();
    }
    
    /**
     * 
     * @see org.tramper.loader.Loader#upload(org.tramper.doc.SimpleDocument, java.lang.String)
     */
    public void upload(SimpleDocument doc, String url) {
        this.url = url;
        this.uploadDocument = doc;
        //stop the thread if already running
        if (this.isRunning()) {
            this.stop();
        }
        loadingType = UPLOAD;
        aLoading = new Thread(this);
        aLoading.start();
    }
    
    /**
     * @see org.tramper.loader.Loader#uploadAndWait(org.tramper.doc.SimpleDocument, java.lang.String)
     */
    public void uploadAndWait(SimpleDocument doc, String url) {
        this.upload(doc, url);
        try {
            aLoading.join();
        } catch (InterruptedException e) {
            logger.error("historic saving interrupted before end");
        }
    }

    /**
     * 
     * @see org.tramper.loader.Loader#call(java.lang.String, java.util.Map, java.lang.Object)
     */
    public void call(String url, Map<String, String> parameter, Target target) {
        this.url = url;
        this.parameter = parameter;
        this.target = target;
        //stop the thread if already running
        if (this.isRunning()) {
            this.stop();
        }
        loadingType = CALL;
        aLoading = new Thread(this);
        aLoading.start();
    }
    
    /**
     * try to stop the loading thread.
     * Wait 1 second for I/O process to end, 
     * and interrupt brutally if still alive.
     * @see org.tramper.loader.Loader#stop()
     */
    public void stop() {
        loadingStopped = true;
        try {
            aLoading.join(1000);
            if (aLoading.isAlive()) {
                aLoading.interrupt();
                logger.info("Loading thread interrupted by me");
                LoadingEvent event = new LoadingEvent(this, null, loadingType, url, target);
                fireLoadingStoppedEvent(event);
            }
        }
        catch (InterruptedException e) {
            logger.warn("Loading thread interrupted before I did");
            LoadingEvent event = new LoadingEvent(this, null, loadingType, url, target);
            fireLoadingFailedEvent(event);
        }
    }
    
    /**
     * 
     * @see org.tramper.loader.Loader#isRunning()
     */
    public boolean isRunning() {
        if (aLoading != null && aLoading.isAlive()) {
            return true;
        }
        else {
            return false;
        }
    }
    
    /**
     * Browse and parse a resource to make a speakable document
     */
    public void run() {
        if (loadingType == DOWNLOAD) {
            this.download();
        } else if (loadingType == UPLOAD) {
            this.upload();
        } else if (loadingType == CALL) {
            this.call();
        }
    }
    
    /**
     * 
     */
    protected void download() {
        LoadingEvent startedEvent = new LoadingEvent(this, null, loadingType, url, target);
        fireLoadingStartedEvent(startedEvent);
        
        URL urlObj = null;
        try {
            urlObj = new URL(url);
        } catch (MalformedURLException e) {
            logger.error("bad url", e);
            LoadingEvent failedEvent = new LoadingEvent(this, null, loadingType, url, target);
            fireLoadingFailedEvent(failedEvent);
            return;
        }
        
        //Browse for a stream
        Browser browser = null;
        InputStream inStream = null;
        try {
            browser = BrowserFactory.getBrowserByProtocol(urlObj);
            inStream = browser.openRead(urlObj);
        } catch (BrowseException e) {
            logger.error("browsing error : "+e.getMessage());
            LoadingEvent failedEvent = new LoadingEvent(this, null, loadingType, url, target);
            fireLoadingFailedEvent(failedEvent);
            return;
        }
        if (loadingStopped) {
            loadingStopped = false;
            try {
		inStream.close();
	    } catch (IOException e) {
		logger.warn("download() : inStream close failed"); 
	    }
            LoadingEvent stoppedEvent = new LoadingEvent(this, null, loadingType, url, target);
            fireLoadingStoppedEvent(stoppedEvent);
            return;
        }

        String mimeType = null;
        //get the right parser
        Parser aParser = null;
        try {
            mimeType = browser.getMimeType();
            aParser = ParserFactory.getParserByMimeType(mimeType);
        } catch (Exception e) {
            try {
                String extension = browser.getExtension();
                aParser = ParserFactory.getParserByExtension(extension);
            } catch (Exception ex) {
                logger.error("unknown document format", ex);
                LoadingEvent failedEvent = new LoadingEvent(this, null, loadingType, url, target);
                fireLoadingFailedEvent(failedEvent);
                return;
            }
        }
        if (loadingStopped) {
            loadingStopped = false;
            try {
		inStream.close();
	    } catch (IOException e) {
		logger.warn("download() : inStream closing failed");
	    }
            LoadingEvent stoppedEvent = new LoadingEvent(this, null, loadingType, url, target);
            fireLoadingStoppedEvent(stoppedEvent);
            return;
        }
        
        //read the stream to make a document
        SimpleDocument doc = null;
        try {
            doc = aParser.parse(inStream, urlObj);
        } catch (Exception e) {
            logger.error("impossible to parse the document", e);
            LoadingEvent failedEvent = new LoadingEvent(this, null, loadingType, url, target);
            fireLoadingFailedEvent(failedEvent);
            return;
        } finally {
            try {
        	inStream.close();
	    } catch (IOException e) {
		logger.warn("call() : inStream close failed");
	    }
        }
        if (loadingStopped) {
            loadingStopped = false;
            LoadingEvent stoppedEvent = new LoadingEvent(this, null, loadingType, url, target);
            fireLoadingStoppedEvent(stoppedEvent);
            return;
        }
        
        doc.setUrl(urlObj);
        if (doc.getMimeType() == null) {
            doc.setMimeType(mimeType);
        }
        
        LoadingEvent completedEvent = new LoadingEvent(this, doc, loadingType, url, target);
        fireLoadingCompletedEvent(completedEvent);
    }
    
    /**
     * 
     */
    protected void upload() {
        LoadingEvent startedEvent = new LoadingEvent(this, null, loadingType, url, target);
        fireLoadingStartedEvent(startedEvent);

        URL urlObj = null;
        try {
            urlObj = new URL(url);
        } catch (MalformedURLException e) {
            logger.error("bad url", e);
            LoadingEvent failedEvent = new LoadingEvent(this, null, loadingType, url, target);
            fireLoadingFailedEvent(failedEvent);
            return;
        }
        
        //Browse for a stream
        Browser browser = null;
        OutputStream outStream = null;
        try {
            browser = BrowserFactory.getBrowserByProtocol(urlObj);
            outStream = browser.openWrite(urlObj);
        } catch (BrowseException e) {
            logger.error("browsing error : "+e.getMessage());
            LoadingEvent failedEvent = new LoadingEvent(this, null, loadingType, url, target);
            fireLoadingFailedEvent(failedEvent);
            return;
        }
        if (loadingStopped) {
            loadingStopped = false;
            try {
        	outStream.close();
	    } catch (IOException e) {
		logger.warn("upload() : outStream close failed");
	    }
            LoadingEvent stoppedEvent = new LoadingEvent(this, null, loadingType, url, target);
            fireLoadingStoppedEvent(stoppedEvent);
            return;
        }
        
        //get the right parser
        Parser aParser = null;
        try {
            String mimeType = uploadDocument.getMimeType();
            aParser = ParserFactory.getParserByMimeType(mimeType);
        } catch (Exception e) {
            try {
                String extension = browser.getExtension();
                aParser = ParserFactory.getParserByExtension(extension);
            } catch (Exception ex) {
                logger.error("unknown document format", ex);
                LoadingEvent failedEvent = new LoadingEvent(this, null, loadingType, url, target);
                fireLoadingFailedEvent(failedEvent);
                return;
            }
        }
        if (loadingStopped) {
            loadingStopped = false;
            try {
        	outStream.close();
	    } catch (IOException e) {
		logger.warn("upload() : outStream closing failed");
	    }
            LoadingEvent stoppedEvent = new LoadingEvent(this, null, loadingType, url, target);
            fireLoadingStoppedEvent(stoppedEvent);
            return;
        }
        
        try {
            aParser.unparse(outStream, uploadDocument, null);
        } catch (ParsingException e) {
            logger.error("impossible to unparse the document", e);
            LoadingEvent failedEvent = new LoadingEvent(this, null, loadingType, url, target);
            fireLoadingFailedEvent(failedEvent);
            return;
        } finally {
            try {
        	outStream.close();
	    } catch (IOException e) {
		logger.warn("call() : inStream close failed");
	    }
        }
        if (loadingStopped) {
            loadingStopped = false;
            LoadingEvent stoppedEvent = new LoadingEvent(this, null, loadingType, url, target);
            fireLoadingStoppedEvent(stoppedEvent);
            return;
        }

        LoadingEvent completedEvent = new LoadingEvent(this, uploadDocument, loadingType, url, target);
        fireLoadingCompletedEvent(completedEvent);
    }

    /**
     * 
     */
    protected void call() {
        LoadingEvent startedEvent = new LoadingEvent(this, null, loadingType, url, target);
        fireLoadingStartedEvent(startedEvent);

        URL urlObj = null;
        try {
            urlObj = new URL(url);
        } catch (MalformedURLException e) {
            logger.error("bad url", e);
            LoadingEvent failedEvent = new LoadingEvent(this, null, loadingType, url, target);
            fireLoadingFailedEvent(failedEvent);
            return;
        }
        
        //Browse for a stream
        Browser browser = null;
        InputStream inStream = null;
        try {
            browser = BrowserFactory.getBrowserByProtocol(urlObj);
            inStream = browser.openRead(urlObj, parameter);
        } catch (BrowseException e) {
            logger.error("browsing error : "+e.getMessage());
            LoadingEvent failedEvent = new LoadingEvent(this, null, loadingType, url, target);
            fireLoadingFailedEvent(failedEvent);
            return;
        }
        if (loadingStopped) {
            loadingStopped = false;
            try {
        	inStream.close();
	    } catch (IOException e) {
		logger.warn("call() : inStream close failed");
	    }
            LoadingEvent stoppedEvent = new LoadingEvent(this, null, loadingType, url, target);
            fireLoadingStoppedEvent(stoppedEvent);
            return;
        }
        
        //get the right parser
        String mimeType = null;
        Parser aParser = null;
        try {
            mimeType = browser.getMimeType();
            aParser = ParserFactory.getParserByMimeType(mimeType);
        } catch (Exception e) {
            try {
                String extension = browser.getExtension();
                aParser = ParserFactory.getParserByExtension(extension);
            } catch (Exception ex) {
                logger.error("unknown document format", ex);
                LoadingEvent failedEvent = new LoadingEvent(this, null, loadingType, url, target);
                fireLoadingFailedEvent(failedEvent);
                return;
            }
        }
        if (loadingStopped) {
            loadingStopped = false;
            try {
        	inStream.close();
	    } catch (IOException e) {
		logger.warn("call() : inStream closing failed");
	    }
            LoadingEvent failedEvent = new LoadingEvent(this, null, loadingType, url, target);
            fireLoadingStoppedEvent(failedEvent);
            return;
        }
        
        //read the stream to make a document
        SimpleDocument doc = null;
        try {
            doc = aParser.parse(inStream, urlObj);
        } catch (ParsingException e) {
            logger.error("impossible to parse the document", e);
            LoadingEvent failedEvent = new LoadingEvent(this, null, loadingType, url, target);
            fireLoadingFailedEvent(failedEvent);
            return;
        } finally {
            try {
        	inStream.close();
	    } catch (IOException e) {
		logger.warn("call() : inStream close failed");
	    }
        }
        if (loadingStopped) {
            loadingStopped = false;
            LoadingEvent stoppedEvent = new LoadingEvent(this, null, loadingType, url, target);
            fireLoadingStoppedEvent(stoppedEvent);
            return;
        }
        
        doc.setUrl(urlObj);
        doc.setMimeType(mimeType);

        LoadingEvent completedEvent = new LoadingEvent(this, doc, loadingType, url, target);
        fireLoadingCompletedEvent(completedEvent);
    }
    
    /**
     * @return loadingType.
     */
    public int getLoadingType() {
        return this.loadingType;
    }

    /**
     * @param loadingType loadingType 
     */
    public void setLoadingType(int loadingType) {
        this.loadingType = loadingType;
    }

    /**
     * add a listener
     * @see org.tramper.loader.Loader#addLoadingListener(org.tramper.loader.LoadingListener)
     */
    public void addLoadingListener(LoadingListener listener) {
        if (!loadingListener.contains(listener)) {
            loadingListener.add(listener);
        }
    }
    
    /**
     * remove a listener
     * @see org.tramper.loader.Loader#removeLoadingListener(org.tramper.loader.LoadingListener)
     */
    public void removeLoadingListener(LoadingListener listener) {
        loadingListener.remove(listener);
    }

    /**
     * fire loading started event
     * @param event
     */
    public void fireLoadingStartedEvent(LoadingEvent event) {
        for (int i=0; i<loadingListener.size(); i++) {
            LoadingListener listener = loadingListener.get(i);
            listener.loadingStarted(event);
        }
    }

    /**
     * fire loading ended event
     * @param event
     */
    public void fireLoadingCompletedEvent(LoadingEvent event) {
        for (int i=0; i<loadingListener.size(); i++) {
            LoadingListener listener = loadingListener.get(i);
            listener.loadingCompleted(event);
        }
    }

    /**
     * fire loading stopped event
     * @param event
     */
    public void fireLoadingStoppedEvent(LoadingEvent event) {
        for (int i=0; i<loadingListener.size(); i++) {
            LoadingListener listener = loadingListener.get(i);
            listener.loadingStopped(event);
        }
    }

    /**
     * fire loading failed event
     * @param event
     */
    public void fireLoadingFailedEvent(LoadingEvent event) {
        for (int i=0; i<loadingListener.size(); i++) {
            LoadingListener listener = loadingListener.get(i);
            listener.loadingFailed(event);
        }
    }

    /**
     * @return target.
     */
    public Target getTarget() {
        return this.target;
    }

    /**
     * @param target target 
     */
    public void setTarget(Target target) {
        this.target = target;
    }
}
