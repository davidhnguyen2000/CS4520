package com.example.inclassassignments.InClass07;

import java.io.Serializable;

public class Token implements Serializable {
    private final boolean auth;
    private final String token;

    public Token(boolean auth, String token) {
        this.auth = auth;
        this.token = token;
    }

    public boolean isAuth() {
        return auth;
    }

    public String getToken() {
        return token;
    }
}
