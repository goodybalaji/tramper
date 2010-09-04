package org.tramper.loader;

import java.util.Map;

import org.tramper.doc.SimpleDocument;
import org.tramper.doc.Target;


/**
 * Load a speakable document from a source
 * @author Paul-Emile
 */
public interface Loader {
    /** download */
    public static final int DOWNLOAD = 1;
    /** upload */
    public static final int UPLOAD = 2;
    /** call */
    public static final int CALL = 3;
    /**
     * download a document from a source designated by an url
     * @param url url of the document to download
     * @param target target where the document whould be rendered
     */
    public void download(String url, Target target);

    /**
     * Used for uploading favorites and history at shutdown
     * @param doc document to upload
     * @param url URL where to upload the document
     */
    public void initUpload(SimpleDocument doc, String url);
    
    /**
     * upload a document to a source designated by an url
     * @param doc document to upload
     * @param url url where upload the document
     */
    public void upload(SimpleDocument doc, String url);

    /**
     * upload a document to a source designated by an url and wait for the end of the upload
     * @param doc document to upload
     * @param url url where upload the document
     */
    public void uploadAndWait(SimpleDocument doc, String url);
    
    /**
     * remote procedure call 
     * @param url url of the procedure to call
     * @param parameter optional parameters when calling the procedure
     * @param target target where the document whould be rendered
     */
    public void call(String url,  Map<String, String> parameter, Target target);
    
    /**
     * stop the loading
     */
    public void stop();
    
    /**
     * return true if the loading is curently running
     * @return
     */
    public boolean isRunning();
    
    /**
     * add a loading listener
     * @param listener
     */
    public void addLoadingListener(LoadingListener listener);
    
    /**
     * remove a loading listener
     * @param listener
     */
    public void removeLoadingListener(LoadingListener listener);
    
    /**
     * 
     * @param target
     */
    public void setTarget(Target target);
    
    /**
     * 
     * @return
     */
    public String getUrl();
    
    /**
     * 
     * @return
     */
    public int getLoadingType();
    /**
     * 
     * @return
     */
    public Target getTarget();
}
