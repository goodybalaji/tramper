package org.tramper;

/**
 * 
 * @author Paul-Emile
 */
public class JavaSystem {
    /** java version */
    private static double version;
    
    // initialise java version
    static {
        String javaVersion = System.getProperty("java.version");
        javaVersion = javaVersion.substring(0, 3);
        try {
            version = Double.parseDouble(javaVersion);
        } catch (NumberFormatException e) {
            System.err.println(javaVersion+" parsing error "+e.getMessage());
        }
    }
    
    /**
     * Returns true if the java version is equals or higher than 1.5, false otherwise
     * @return
     */
    public static boolean isJava5OrMore() {
        if (version >= 1.5) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Returns true if the java version is equals or higher than 1.4, false otherwise
     * @return
     */
    public static boolean isJava4OrMore() {
        if (version >= 1.4) {
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
}
