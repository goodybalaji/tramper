package org.tramper.webPage;

import java.net.URL;
import java.security.Policy;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.lobobrowser.html.HttpRequest;
import org.lobobrowser.html.test.SimpleUserAgentContext;

/**
 * 
 * @author Paul-Emile
 */
public class SpeakableUserAgentContext extends SimpleUserAgentContext {
    /** cookies */
    protected Map<String, String> cookies;
    
    /**
     * 
     */
    public SpeakableUserAgentContext() {
        cookies = new HashMap<String, String>();
    }

    /**
     * @see org.lobobrowser.html.UserAgentContext#createHttpRequest()
     */
    public HttpRequest createHttpRequest() {
        return super.createHttpRequest();
    }

    /**
     * @see org.lobobrowser.html.UserAgentContext#getAppCodeName()
     */
    public String getAppCodeName() {
        return super.getAppCodeName();
    }

    /**
     * @see org.lobobrowser.html.UserAgentContext#getAppMinorVersion()
     */
    public String getAppMinorVersion() {
        return super.getAppMinorVersion();
    }

    /**
     * @see org.lobobrowser.html.UserAgentContext#getAppName()
     */
    public String getAppName() {
        return super.getAppName();
    }

    /**
     * @see org.lobobrowser.html.UserAgentContext#getAppVersion()
     */
    public String getAppVersion() {
        return super.getAppVersion();
    }

    /**
     * @see org.lobobrowser.html.UserAgentContext#getBrowserLanguage()
     */
    public String getBrowserLanguage() {
        return Locale.getDefault().getLanguage();
    }

    /**
     * @see org.lobobrowser.html.UserAgentContext#getCookie(java.net.URL)
     */
    public String getCookie(URL url) {
        return cookies.get(url.toString());
    }

    /**
     * @see org.lobobrowser.html.UserAgentContext#getPlatform()
     */
    public String getPlatform() {
        return super.getPlatform();
    }

    /**
     * @see org.lobobrowser.html.UserAgentContext#getSecurityPolicy()
     */
    public Policy getSecurityPolicy() {
        return super.getSecurityPolicy();
    }

    /**
     * @see org.lobobrowser.html.UserAgentContext#getUserAgent()
     */
    public String getUserAgent() {
        return super.getUserAgent();
    }

    /**
     * @see org.lobobrowser.html.UserAgentContext#isCookieEnabled()
     */
    public boolean isCookieEnabled() {
        return true;
    }

    /**
     * @see org.lobobrowser.html.UserAgentContext#isMedia(java.lang.String)
     */
    public boolean isMedia(String arg0) {
        return super.isMedia(arg0);
    }

    /**
     * @see org.lobobrowser.html.UserAgentContext#isScriptingEnabled()
     */
    public boolean isScriptingEnabled() {
        return true;
    }

    /**
     * @see org.lobobrowser.html.UserAgentContext#setCookie(java.net.URL, java.lang.String)
     */
    public void setCookie(URL url, String value) {
        cookies.put(url.toString(), value);
    }

}
