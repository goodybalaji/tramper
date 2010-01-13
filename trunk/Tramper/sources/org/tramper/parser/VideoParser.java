package org.tramper.parser;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.List;

import org.apache.log4j.Logger;
import org.tramper.JavaSystem;
import org.tramper.doc.SimpleDocument;
import org.tramper.doc.Video;

import com.sun.media.jmc.MediaCorruptedException;
import com.sun.media.jmc.MediaProvider;
import com.sun.media.jmc.MediaUnavailableException;
import com.sun.media.jmc.MediaUnsupportedException;
import com.sun.media.jmc.type.ContainerType;

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
	
	// the JMC video player doesn't read the Windows long pathname
	if (JavaSystem.isWindows()) {
	    String protocol = url.getProtocol();
	    if (protocol.equals("file")) {
		// so translate the long pathname into short pathname
		try {
		    String longPathName = URLDecoder.decode(url.getPath(), "utf-8");
		    if (longPathName.startsWith("/")) {
			longPathName = longPathName.substring(1);
		    }
		    String shortPathName = JavaSystem.longToShortWindowsPathName(longPathName);
		    if (shortPathName != null) {
			shortPathName = shortPathName.replace('\\', '/');
			url = new URL("file", null, shortPathName);
		    }
		} catch (UnsupportedEncodingException e) {
		    
		} catch (MalformedURLException e) {
		    
		}
	    }
	}
	
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
	List<ContainerType> types = MediaProvider.getSupportedContainerTypes();
	for (ContainerType type : types) {
	    logger.debug(type.getDescription());
	    for (String s : type.getExtensions()) {
		logger.debug(" ext: "+s);
	    }
	    for (String s : type.getMimeTypes()) {
		logger.debug(" mime: "+s);
	    }
	}
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
