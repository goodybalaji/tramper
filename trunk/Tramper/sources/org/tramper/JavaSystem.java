package org.tramper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

/**
 * 
 * @author Paul-Emile
 */
public class JavaSystem {
    /** logger */
    private static Logger logger = Logger.getLogger(JavaSystem.class);
    /** java version */
    private static double version;
    
    // initialise java version
    static {
        String javaVersion = System.getProperty("java.version");
        javaVersion = javaVersion.substring(0, 3);
        try {
            version = Double.parseDouble(javaVersion);
        } catch (NumberFormatException e) {
            logger.error(javaVersion+" parsing error "+e.getMessage());
        }
    }
    
    /**
     * Returns true if the java version is equals or higher than 1.5, false otherwise
     * @return
     */
    public static boolean isJava6OrMore() {
        if (version >= 1.6) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * true if run under a Windows operating system, false otherwise
     * @return
     */
    public static boolean isWindows() {
	boolean isWindows = false;
	String osName = System.getProperty("os.name").toLowerCase();
	if (osName.contains("windows")) {
	    isWindows = true;
	}
	return isWindows;
    }
    
    public static String longToShortWindowsPathName(String longPathName) {
	Runtime runtime = Runtime.getRuntime();
	String[] args = {"cscript", "shortPathNameFormat.vbs", "\""+longPathName+"\""};
	BufferedReader inReader = null;
	try {
	    Process executedCommand = runtime.exec(args);
	    executedCommand.waitFor();
	    InputStream in = executedCommand.getInputStream();
	    inReader = new BufferedReader(new InputStreamReader(in));
	    // the first 3 lines have to be skipped
	    inReader.readLine();// Microsoft (R) Windows Script Host Version 5.7
	    inReader.readLine();// Copyright (C) Microsoft Corporation 1996-2001. Tous droits réservés.
	    inReader.readLine();// 
	    String shortPathname = inReader.readLine();// the expected result
	    return shortPathname;
	} catch (IOException e) {
	    logger.error(e.getMessage());
	} catch (InterruptedException e) {
	    logger.error(e.getMessage());
	} finally {
	    if (inReader != null) {
		try {
		    inReader.close();
		} catch (IOException e) {}
	    }
	}
	return null;
    }
}
