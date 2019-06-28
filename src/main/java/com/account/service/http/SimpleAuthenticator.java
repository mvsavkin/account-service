package com.account.service.http;

import com.sun.net.httpserver.Authenticator;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpPrincipal;

public class SimpleAuthenticator extends Authenticator {
    @Override
    public Result authenticate(HttpExchange httpExchange) {
        //TODO: Add security rules
        return new Success(new HttpPrincipal("user", "realm"));
    }
}
