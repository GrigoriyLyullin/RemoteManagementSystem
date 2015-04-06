package com.railwaycompany.model.implementation;

import com.railwaycompany.model.interfaces.AuthenticationService;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;

import javax.ejb.Stateless;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class AuthenticationServiceImpl implements AuthenticationService {

    private static final Logger LOG = Logger.getLogger(AuthenticationServiceImpl.class);
    private static final String TOKEN_PARAM = "Rest-Token";
    private static final String LOGIN_PARAM = "login";
    private static final String PASSWORD_PARAM = "password";
    private static final String URL_AUTH = "http://localhost:8080/InformationSystem/authenticate";

    @Override
    public String authenticate(String login, String password) {
        return getToken(login, password);
    }

    @Override
    public boolean verifyToken(String token) {
        return isValidToken(token);
    }

    private boolean isValidToken(String token) {
        return token != null && !token.isEmpty();
    }

    private String getToken(String login, String password) {
        CloseableHttpClient httpClient = null;
        String token = null;
        try {
            HttpPost httpPost = new HttpPost(URL_AUTH);
            List<NameValuePair> postParameters = new ArrayList<>();
            postParameters.add(new BasicNameValuePair(LOGIN_PARAM, login));
            postParameters.add(new BasicNameValuePair(PASSWORD_PARAM, password));
            httpPost.setEntity(new UrlEncodedFormEntity(postParameters));
            httpClient = HttpClientBuilder.create().build();
            HttpResponse response = httpClient.execute(httpPost);

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                token = response.getFirstHeader(TOKEN_PARAM).getValue();
            } else {
                LOG.warn("Response status code: " + statusCode + " instead of 200");
            }
        } catch (IOException e) {
            LOG.warn(e);
        } finally {
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    LOG.warn("Cannot close HttpClient", e);
                }
            }
        }
        return token;
    }
}
