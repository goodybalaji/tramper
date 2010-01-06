package org.tramper.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.log4j.Logger;
import org.tramper.doc.SimpleDocument;
import org.tramper.doc.Video;

import com.sun.media.jmc.MediaCorruptedException;
import com.sun.media.jmc.MediaProvider;
import com.sun.media.jmc.MediaUnavailableException;
import com.sun.media.jmc.MediaUnsupportedException;

/**
 * 
 * @author Paul-Emile
 */
public class VideoParser implements Parser {
    /** logger */
    private Logger logger = Logger.getLogger(VideoParser.class);
    /** document's url */
    protected URL url;

    /**
     * @see org.tramper.parser.Parser#setUrl(java.net.URL)
     */
    public void setUrl(URL url) {
	this.url = url;
    }

    /**
     * @see org.tramper.parser.Parser#parse(java.io.InputStream)
     */
    public SimpleDocument parse(InputStream inStream) throws ParsingException {
	Video video = new Video();
	//Media media = null;
	MediaProvider mediaProvider = null;
	try {
	    //media = new Media(url.toURI());
	    mediaProvider = new MediaProvider(url.toURI());
	} catch (MediaUnavailableException e) {
	    logger.error(e.getMessage(), e);
	    throw new ParsingException(e.getMessage());
	} catch (MediaUnsupportedException e) {
	    logger.error(e.getMessage(), e);
	    throw new ParsingException(e.getMessage());
	} catch (MediaCorruptedException e) {
	    logger.error(e.getMessage(), e);
	    throw new ParsingException(e.getMessage());
	} catch (URISyntaxException e) {
	    logger.error(e.getMessage(), e);
	    throw new ParsingException(e.getMessage());
	}
	
	double duration = mediaProvider.getDuration();
	video.setDuration(duration);
	
	/*Dimension frameSize = media.getFrameSize();
	video.setFrameSize(frameSize);
	
	Map<String, Object> metadata = media.getMetadata();
	if (metadata != null) {
	    Set<Entry<String, Object>> metadataSet = metadata.entrySet();
	    for (Entry<String, Object> metadatum : metadataSet) {
		String key = metadatum.getKey();
		Object value = metadatum.getValue();
		System.out.println("metadata video key="+key+" value="+value);
	    }
	} else {*/
            //if no metadata, get the file name as title
            String path = url.getPath();
            int lastIndexSlash = path.lastIndexOf("/");
            if (lastIndexSlash != -1) {
                path = path.substring(lastIndexSlash+1);
            }
            video.setTitle(path);
	//}
	
	video.setMediaProvider(mediaProvider);
	
	return video;
    }

    /**
     * @see org.tramper.parser.Parser#unparse(java.io.OutputStream, org.tramper.doc.SimpleDocument)
     */
    public void unparse(OutputStream outStream, SimpleDocument document) throws ParsingException {
    }
}
