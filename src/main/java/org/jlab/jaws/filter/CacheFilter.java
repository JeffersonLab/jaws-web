package org.jlab.jaws.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.util.Arrays;

/**
 * WebFilter for setting response cache directives.
 *
 * @author ryans
 */
@WebFilter(filterName = "CacheFilter", urlPatterns = {"/*"}, dispatcherTypes = {DispatcherType.REQUEST, DispatcherType.FORWARD}, asyncSupported = true)
public class CacheFilter implements Filter {

    private static final long EXPIRE_MILLIS = 31536000000L; // 365 days is max expires per spec
    
    private static final String[] CACHEABLE_CONTENT_TYPES = new String[] {
            "text/css", "text/javascript", "application/javascript", "image/png",
            "image/jpeg", "image/jpg", "image/gif", "image/icon", "image/x-icon",
            "image/vnd.microsoft.icon", "image/svg+xml"
    };

    static {
        Arrays.sort(CACHEABLE_CONTENT_TYPES);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        chain.doFilter(request, new CacheControlResponse(httpResponse));
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }

    class CacheControlResponse extends HttpServletResponseWrapper {

        CacheControlResponse(HttpServletResponse response) {
            super(response);
        }

        @Override
        public void setContentType(String type) {
            super.setContentType(type);
            
            if (type != null && Arrays.binarySearch(CACHEABLE_CONTENT_TYPES, type) > -1) {
                super.setDateHeader("Expires", System.currentTimeMillis() + EXPIRE_MILLIS);
                super.setHeader("Cache-Control", null); // Remove header automatically added by SSL/TLS container module
                super.setHeader("Pragma", null); // Remove header automatically added by SSL/TLS container module                 
            } else {
                super.setHeader("Cache-Control", "no-store, no-cache, must-revalidate"); // HTTP 1.1
                super.setHeader("Pragma", "no-cache"); // HTTP 1.0
                super.setDateHeader("Expires", 0); // Proxies
            }
        }
    }
}
