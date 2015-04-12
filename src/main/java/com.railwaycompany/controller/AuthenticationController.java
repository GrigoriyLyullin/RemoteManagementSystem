package com.railwaycompany.controller;

import com.railwaycompany.model.interfaces.AuthenticationService;
import org.apache.log4j.Logger;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.persistence.Transient;
import javax.servlet.http.HttpSession;
import java.io.Serializable;

@ManagedBean
@SessionScoped
public class AuthenticationController implements Serializable {

    private static final Logger LOG = Logger.getLogger(AuthenticationController.class);
    private static final String TOKEN_PARAM = "Rest-Token";
    private static final String LOGIN_PAGE_REDIRECT = "/index.xhtml?faces-redirect=true";
    private static final String REPORT_PAGE_REDIRECT = "/private/ticket_report.xhtml?faces-redirect=true";

    private String username;
    @Transient
    private String password;
    private String token;
    private boolean loggedIn;

    @EJB
    private AuthenticationService authenticationService;

    public String login() {
        LOG.debug("Try to log in");
        FacesContext facesContext = FacesContext.getCurrentInstance();
        token = authenticationService.authenticate(username, password);
        if (token != null) {
            ExternalContext externalContext = facesContext.getExternalContext();
            externalContext.getSessionMap().put(TOKEN_PARAM, token);
            setLoggedIn(true);
            LOG.debug("Successfully log in");
            return REPORT_PAGE_REDIRECT;
        } else {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
                    "User with such username and password does not exist", ""));
            LOG.warn("User with such username and password does not exist");
            setLoggedIn(false);
            return null;
        }
    }

    public String logout() {
        LOG.debug("Try to log out");
        if (isLoggedIn()) {
            ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
            try {
                HttpSession httpSession = (HttpSession) externalContext.getSession(false);
                httpSession.invalidate();
                LOG.warn("User has been successfully log out");
            } catch (NullPointerException e) {
                LOG.warn("User try to log out with empty session");
            }
            setLoggedIn(false);
        }
        return LOGIN_PAGE_REDIRECT;
    }

    public boolean isLoggedIn() {
        setLoggedIn(authenticationService.verifyToken(token));
        return loggedIn && token != null;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }
}
