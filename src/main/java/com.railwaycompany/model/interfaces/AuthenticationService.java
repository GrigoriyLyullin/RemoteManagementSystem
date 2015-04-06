package com.railwaycompany.model.interfaces;

public interface AuthenticationService {

    String authenticate(String login, String password);

    boolean verifyToken(String token);
}
