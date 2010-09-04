package org.tramper.loader;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


/**
 * return a new loader each time 
 * @author Paul-Emile
 */
public class LoaderFactory implements LoadingListener {
    /** instance */
    private static LoaderFactory instance; 
    /** loader factory listeners */
    private List<LoaderFactoryListener> loaderFactoryListener = new ArrayList<LoaderFactoryListener>();
    /** List of current loaders */
    private List<Loader> currentLoaders = new ArrayList<Loader>();
    /** read write lock for current loaders list */
    private ReadWriteLock lock;
    
    public LoaderFactory() {
	super();
	lock = new ReentrantReadWriteLock();
    }
    
    /**
     * Returns the single instance;
     * @return
     */
    public static LoaderFactory getInstance() {
	if (instance == null) {
	    instance = new LoaderFactory();
	}
	return instance;
    }
    
    /**
     * Instantiates a new loader 
     * @param className
     * @return a loader
     */
    public Loader newLoader() {
        Loader loader = new DocumentLoader();
        loader.addLoadingListener(this);
        LoaderFactoryEvent event = new LoaderFactoryEvent(LoaderFactory.class);
        event.setLoader(loader);
        fireNewLoaderEvent(event);
        return loader;
    }
    
    /**
     * add a loader factory listener
     * @param listener
     */
    public void addLoaderFactoryListener(LoaderFactoryListener listener) {
        if (!loaderFactoryListener.contains(listener)) {
            loaderFactoryListener.add(listener);
        }
    }
    
    /**
     * remove a loader factory listener
     * @param listener
     */
    public void removeLoaderFactoryListener(LoaderFactoryListener listener) {
        loaderFactoryListener.remove(listener);
    }
    
    /**
     * fire a loader created event
     * @param event
     */
    private void fireNewLoaderEvent(LoaderFactoryEvent event) {
        for (LoaderFactoryListener listener : loaderFactoryListener) {
            listener.newLoader(event);
        }
    }

    public void loadingStarted(LoadingEvent event) {
	Loader loader = (Loader)event.getSource();
	lock.writeLock().lock();
        currentLoaders.add(loader);
	lock.writeLock().unlock();
    }

    public void loadingCompleted(LoadingEvent event) {
	Loader loader = (Loader)event.getSource();
	lock.writeLock().lock();
	currentLoaders.remove(loader);
	lock.writeLock().unlock();
    }

    public void loadingFailed(LoadingEvent event) {
	Loader loader = (Loader)event.getSource();
	lock.writeLock().lock();
	currentLoaders.remove(loader);
	lock.writeLock().unlock();
    }

    public void loadingStopped(LoadingEvent event) {
	Loader loader = (Loader)event.getSource();
	lock.writeLock().lock();
	currentLoaders.remove(loader);
	lock.writeLock().unlock();
    }
    
    public void unregister() {
	lock.readLock().lock();
	for (Loader currentLoader : currentLoaders) {
	    currentLoader.stop();
	}
	lock.readLock().unlock();
    }
}
