package com.railwaycompany.filter;

import com.railwaycompany.model.interfaces.AuthenticationService;
import org.apache.log4j.Logger;

import javax.ejb.EJB;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class AuthenticationFilter implements Filter {

    private static final Logger LOG = Logger.getLogger(AuthenticationFilter.class);
    private static final String TOKEN_PARAM = "Rest-Token";
    private static final String LOGIN_PAGE_REDIRECT = "/index.xhtml?faces-redirect=true";

    @EJB
    private AuthenticationService authenticationService;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        HttpSession session = httpServletRequest.getSession(false);
        if (session != null) {
            String token = (String) session.getAttribute(TOKEN_PARAM);
            if (token != null && authenticationService.verifyToken(token)) {
                filterChain.doFilter(servletRequest, servletResponse);
            } else {
                httpServletResponse.sendRedirect(httpServletRequest
                        .getContextPath() + LOGIN_PAGE_REDIRECT);
            }
        } else {
            httpServletResponse.sendRedirect(httpServletRequest
                    .getContextPath() + LOGIN_PAGE_REDIRECT);
        }
    }


    @Override
    public void init(FilterConfig config) throws ServletException {
        LOG.info("Authentication filter initialize");
    }

    @Override
    public void destroy() {
        LOG.info("Authentication filter destroy");
    }
}
