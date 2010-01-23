package org.tramper.parser;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.apache.log4j.Logger;
import org.tramper.doc.Sound;
import org.tramper.doc.SimpleDocument;

/**
 * Sound parser. Extracts the metadata.
 * @author Paul-Emile
 */
public class SoundParser implements Parser {
    /** logger */
    private Logger logger = Logger.getLogger(SoundParser.class);
    /** document's url */
    protected URL url;

    /**
     * @see org.tramper.parser.Parser#setUrl(java.lang.String)
     */
    public void setUrl(URL url) {
        this.url = url;
    }

    /**
     * @see org.tramper.parser.Parser#parse(java.io.InputStream)
     */
    public SimpleDocument parse(InputStream inStream) throws ParsingException {
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
     * @see org.tramper.parser.Parser#unparse(java.io.OutputStream, SimpleDocument)
     */
    public void unparse(OutputStream outStream, SimpleDocument document) throws ParsingException {
    }
}
