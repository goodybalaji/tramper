package org.tramper.audio;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.AudioFileFormat.Type;

import org.apache.log4j.Logger;
import org.tramper.browser.MimeTypeMapper;
import org.tramper.doc.Sound;
import org.tramper.doc.SimpleDocument;
import org.tramper.parser.Parser;
import org.tramper.parser.ParsingException;

/**
 * Sound parser. Extracts the metadata.
 * @author Paul-Emile
 */
public class SoundParser implements Parser {
    /** logger */
    private Logger logger = Logger.getLogger(SoundParser.class);

    /**
     * @see org.tramper.parser.Parser#parse(java.io.InputStream, URL)
     */
    public SimpleDocument parse(InputStream inStream, URL url) throws ParsingException {
        Sound document = new Sound();
        try {
            // we have metadata for local files only
            BufferedInputStream bufferInStream = new BufferedInputStream(inStream);
            AudioFileFormat encodedFileFormat = AudioSystem.getAudioFileFormat(bufferInStream);

            Map<String, Object> metaData = encodedFileFormat.properties();
            logger.debug("metadata="+metaData);
            if (metaData != null) {
                String title = (String) metaData.get("title");
                if (title != null) {
                    document.setTitle(title);
                } else {
                    //if no title, get the file name as title
                    String path = url.getPath();
                    int lastIndexSlash = path.lastIndexOf("/");
                    if (lastIndexSlash != -1) {
                        path = path.substring(lastIndexSlash+1);
                    }
                    document.setTitle(path);
                }
                String comment = (String) metaData.get("comment");
                document.setDescription(comment);
                String creationString = (String) metaData.get("date");
                if (creationString != null) {
                    Date creationDate = null;
                    try {
                        SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy");
                        creationDate = dateFormater.parse(creationString);
                    } catch (ParseException e) {
                        logger.warn(e);
                    }
                    document.setCreationDate(creationDate);
                }
                String album = (String) metaData.get("album");
                document.setAlbum(album);
                String author = (String) metaData.get("author");
                document.setAuthor(author);
                String copyright = (String) metaData.get("copyright");
                document.setCopyright(copyright);
                Long duration = (Long) metaData.get("duration");
                if (duration != null) {
                    document.setDuration(duration.longValue());
                }
            } else {
                //if no metadata, get the file name as title
                String path = url.getPath();
                int lastIndexSlash = path.lastIndexOf("/");
                if (lastIndexSlash != -1) {
                    path = path.substring(lastIndexSlash+1);
                }
                document.setTitle(path);
            }
        } catch (UnsupportedAudioFileException e) {
            logger.info("Not a file audio stream, no metadata to get", e);
            
            //no metadata, get the file name as title, that's all
            String path = url.getPath();
            int lastIndexSlash = path.lastIndexOf("/");
            if (lastIndexSlash != -1) {
                path = path.substring(lastIndexSlash+1);
            }
            document.setTitle(path);
        } catch (IOException e) {
            logger.error("audio input stream unavailable", e);
            throw new ParsingException();
        }
        return document;
    }

    /**
     * @see org.tramper.parser.Parser#unparse(java.io.OutputStream, SimpleDocument, URL)
     */
    public void unparse(OutputStream outStream, SimpleDocument document, URL url) throws ParsingException {
    }

    public boolean isExtensionSupported(String extension) {
	Type[] types = AudioSystem.getAudioFileTypes();
	for (Type type : types) {
	    String anExtension = type.getExtension();
	    if (anExtension.equalsIgnoreCase(extension)) {
		return true;
	    }
	}
	if (extension.equalsIgnoreCase("mpa")) {
	    return true;
	}
	if (extension.equalsIgnoreCase("mp1")) {
	    return true;
	}
	if (extension.equalsIgnoreCase("mp2")) {
	    return true;
	}
	if (extension.equalsIgnoreCase("mp3")) {
	    return true;
	}
	if (extension.equalsIgnoreCase("ogg")) {
	    return true;
	}
	if (extension.equalsIgnoreCase("aiff")) {
	    return true;
	}
	if (extension.equalsIgnoreCase("aifc")) {
	    return true;
	}
	return false;
    }

    public boolean isMimeTypeSupported(String mimeType) {
	Type[] types = AudioSystem.getAudioFileTypes();
	for (Type type : types) {
	    String anExtension = "." + type.getExtension();
	    try {
        	String aMimeType = MimeTypeMapper.getInstance().getMimeType(anExtension.toLowerCase());
        	if (aMimeType.equalsIgnoreCase(mimeType)) {
        	    return true;
        	}
	    } catch (MissingResourceException e) {}
	}
	if (mimeType.equalsIgnoreCase("audio/mp3")) {
	    return true;
	}
	if (mimeType.equalsIgnoreCase("audio/mpg")) {
	    return true;
	}
	if (mimeType.equalsIgnoreCase("audio/mpeg")) {
	    return true;
	}
	if (mimeType.equalsIgnoreCase("audio/mpeg3")) {
	    return true;
	}
	if (mimeType.equalsIgnoreCase("audio/x-mp3")) {
	    return true;
	}
	if (mimeType.equalsIgnoreCase("audio/x-mpg")) {
	    return true;
	}
	if (mimeType.equalsIgnoreCase("audio/x-mpeg")) {
	    return true;
	}
	if (mimeType.equalsIgnoreCase("audio/x-mpeg3")) {
	    return true;
	}
	if (mimeType.equalsIgnoreCase("audio/ogg")) {
	    return true;
	}
	if (mimeType.equalsIgnoreCase("audio/x-ogg")) {
	    return true;
	}
	if (mimeType.equalsIgnoreCase("application/ogg")) {
	    return true;
	}
	if (mimeType.equalsIgnoreCase("application/x-ogg")) {
	    return true;
	}
	if (mimeType.equalsIgnoreCase("application/ogg")) {
	    return true;
	}
	if (mimeType.equalsIgnoreCase("application/ogg")) {
	    return true;
	}
	if (mimeType.equalsIgnoreCase("application/ogg")) {
	    return true;
	}
	return false;
    }
    
    public List<String> getSupportedExtensions() {
	Type[] types = AudioSystem.getAudioFileTypes();
	List<String> extensions = new ArrayList<String>();
	for (Type type : types) {
	    String anExtension = type.getExtension();
	    extensions.add(anExtension);
	}
	extensions.add("mp1");
	extensions.add("mp2");
	extensions.add("mp3");
	extensions.add("mpa");
	extensions.add("ogg");
	extensions.add("aiff");
	extensions.add("aifc");
	return extensions;
    }

    public static List<String> getSupportedEncodingExtensions() {
	Type[] types = AudioSystem.getAudioFileTypes();
	List<String> extensions = new ArrayList<String>();
	for (Type type : types) {
	    String anExtension = type.getExtension();
	    extensions.add(anExtension);
	}
	return extensions;
    }
    
    /**
     * 
     * @return
     */
    public SimpleDocument getSupportedDocument() {
	return new Sound();
    }
}
