package org.tramper.loader;

import java.util.ArrayList;
import java.util.List;


/**
 * return a new loader each time 
 * @author Paul-Emile
 */
public class LoaderFactory {
    /** loader factory listeners */
    private static List<LoaderFactoryListener> loaderFactoryListener = new ArrayList<LoaderFactoryListener>();
    
    /**
     * 
     */
    public LoaderFactory() {
        super();
    }

    /**
     * Instanciate a new loader 
     * @param className
     * @return a loader
     */
    public static Loader getLoader() {
        Loader loader = new DocumentLoader();
        LoaderFactoryEvent event = new LoaderFactoryEvent(LoaderFactory.class);
        event.setLoader(loader);
        fireNewLoaderEvent(event);
        return loader;
    }
    
    /**
     * add a loader factory listener
     * @param listener
     */
    public static void addLoaderFactoryListener(LoaderFactoryListener listener) {
        if (!loaderFactoryListener.contains(listener)) {
            loaderFactoryListener.add(listener);
        }
    }
    
    /**
     * remove a loader factory listener
     * @param listener
     */
    public static void removeLoaderFactoryListener(LoaderFactoryListener listener) {
        loaderFactoryListener.remove(listener);
    }
    
    /**
     * fire a loader created event
     * @param event
     */
    private static void fireNewLoaderEvent(LoaderFactoryEvent event) {
        for (int i=0; i<loaderFactoryListener.size(); i++) {
            LoaderFactoryListener listener = loaderFactoryListener.get(i);
            listener.newLoader(event);
        }
    }
}
