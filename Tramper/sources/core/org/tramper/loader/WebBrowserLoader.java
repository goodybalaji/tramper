package org.tramper.loader;

import java.io.IOException;
import java.lang.reflect.Method;

import org.apache.log4j.Logger;
import org.tramper.doc.MarkupDocument;


/**
 * Load a native web browser following the operating system
 * @author Paul-Emile
 */
public class WebBrowserLoader {
    /** logger */
    private static Logger logger = Logger.getLogger(WebBrowserLoader.class);
    /** operating system type */
    private int operatingSystem;
    /** Windows operating system */
    private static final int WINDOWS_OS = 0;
    /** MacOS operating system */
    private static final int MACINTOSH_OS = 1;
    /** Unix/Linux operating system */
    private static final int UNIX_OS = 2;
    /** OS/2 operating system */
    private static final int OS2_OS = 3;
    /** openVMS operating system */
    private static final int OPENVMS_OS = 4;
    /** launched native process */
    private Process aProcess;
    
    /**
     * Determine the operating system type
     */
    public WebBrowserLoader() {
        super();
        String osName = System.getProperty("os.name");
        if (osName.startsWith("Mac OS")) {
            operatingSystem = MACINTOSH_OS;
        }
        else if (osName.startsWith("Windows")) {
            operatingSystem = WINDOWS_OS;
        }
        else if (osName.equals("OS/2")) {
            operatingSystem = OS2_OS;
        }
        else if (osName.equals("OpenVMS")) {
            operatingSystem = OPENVMS_OS;
        }
        else { //assume Unix or Linux
            operatingSystem = UNIX_OS;
        }
    }
    
    /**
     * Open the url in a web browser
     * @param url
     */
    public void download(String url) {
        Runtime run = Runtime.getRuntime();
        
        if (operatingSystem == MACINTOSH_OS) {
            Class<?> fileMgr = null;
            try {
                fileMgr = Class.forName("com.apple.eio.FileManager");
            }
            catch (ClassNotFoundException e) {
                try {
                    fileMgr = Class.forName("com.apple.mrj.MRJFileUtils");
                }
                catch (ClassNotFoundException e1) {
                    logger.error("unable to launch a web browser on Mac OS");
                }
            }

            try {
                Method openURL = fileMgr.getDeclaredMethod("openURL", new Class[] {String.class});
                openURL.invoke(null, new Object[] {url});
            }
            catch (Exception e) {
                logger.error("unable to launch a web browser on Mac OS", e);
            }
        }
        else if (operatingSystem == WINDOWS_OS) {
            try {
                aProcess = run.exec("rundll32 url.dll,FileProtocolHandler " + url);
            }
            catch (IOException e) {
                logger.error("unable to launch a web browser on Windows");
            }
        }
        else if (operatingSystem == UNIX_OS) {
            String[] browsers = {"firefox", "mozilla", "opera", "netscape", "konqueror", "epiphany"};
            for (int i = 0; i < browsers.length; i++) {
                try {
                    aProcess = run.exec(new String[] {"which", browsers[i]});
                }
                catch (IOException e) {
                    //if unable to call the "which" command, break from the loop
                    break;
                }
                try {
                    if (aProcess.waitFor() == 0) {
                        run.exec(new String[] {browsers[i], url});
                        return;
                    }
                } catch (InterruptedException e) {
                    //error when waiting for aProcess termination, try the next browser
                    continue;
                } catch (IOException e) {
                    //error when launching a browser, try the next browser
                    continue;
                }
            }
            logger.error("unable to launch a web browser on Unix/Linux");
        }
        else {
            logger.error("Unsupported operating system");
        }
    }

    public void addLoadingListener(LoadingListener listener) {
    }

    public void removeLoadingListener(LoadingListener listener) {
    }
    
    /**
     * Kill the native process launched
     * @see org.tramper.loader.Loader#stop()
     */
    public void stop() {
        if (aProcess != null)
            aProcess.destroy();
    }
    
    /**
     * 
     * @see org.tramper.loader.Loader#isRunning()
     */
    public boolean isRunning() {
        if (aProcess == null)
            return false;
        else
            return true;
    }
    
    /**
     * 
     * @see org.tramper.loader.Loader#upload(org.tramper.doc.MarkupDocument, java.lang.String)
     */
    public void upload(MarkupDocument doc, String url) {
    }
}
