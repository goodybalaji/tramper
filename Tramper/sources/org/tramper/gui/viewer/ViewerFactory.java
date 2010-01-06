package org.tramper.gui.viewer;

import org.tramper.doc.Feed;
import org.tramper.doc.ImageDocument;
import org.tramper.doc.Sound;
import org.tramper.doc.Outline;
import org.tramper.doc.SimpleDocument;
import org.tramper.doc.Video;
import org.tramper.doc.WebPage;

/**
 * Returns the right viewer for the document in parameter.
 * @author Paul-Emile
 */
public class ViewerFactory {
    
    /**
     * 
     * @param document
     * @return
     */
    public static Viewer getViewerByDocument(SimpleDocument document) {
        if (document instanceof Feed) {
            return new FeedViewer();
        } else if (document instanceof Outline) {
            return new OutlineViewer();
        } else if (document instanceof WebPage) {
            return new WebPageViewer();
        } else if (document instanceof Sound) {
            return new SoundViewer();
        } else if (document instanceof Video) {
            return new VideoViewer();
        } else if (document instanceof ImageDocument) {
            return new ImageViewer();
        } else {
            throw new RuntimeException("no suitable viewer for this document: "+document);
        }
    }
}
