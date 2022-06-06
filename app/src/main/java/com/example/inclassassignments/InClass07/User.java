package com.example.inclassassignments.InClass07;

import java.io.Serializable;

public class User implements Serializable {
    private String _id;
    private String name;
    private String email;
    private int __v;

    public User(String _id, String name, String email, int __v) {
        this._id = _id;
        this.name = name;
        this.email = email;
        this.__v = __v;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
