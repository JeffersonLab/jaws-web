package org.jlab.jaws.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

/**
 * WebFilter for setting both request and response character encoding to UTF-8.
 *
 * @author ryans
 */
@WebFilter(filterName = "CharacterEncodingFilter", urlPatterns = {"/*"}, dispatcherTypes = {DispatcherType.REQUEST}, asyncSupported = true)
public class CharacterEncodingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }
}
