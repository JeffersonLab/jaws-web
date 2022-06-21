package org.jlab.jaws.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * WebFilter for forwarding to FrontController servlet unless request is for a css/js/img resource then pass through to
 * default servlet to handle.
 *
 * @author ryans
 */
@WebFilter(filterName = "FrontControllerFilter", urlPatterns = {"/*"}, dispatcherTypes = {DispatcherType.REQUEST}, asyncSupported = true)
public class FrontControllerFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        String path = req.getRequestURI().substring(req.getContextPath().length());
        if (path.startsWith("/resources/") || path.startsWith("/proxy/") || path.startsWith("/manifest.json") || path.startsWith("/worker")) {
            chain.doFilter(request, response); // Goes to default servlet.
        } else {
            request.getRequestDispatcher("/view" + path).forward(request, response); // Goes to View front controller servlet.
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }
}
