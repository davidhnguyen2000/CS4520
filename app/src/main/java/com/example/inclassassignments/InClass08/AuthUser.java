package com.example.inclassassignments.InClass08;

public class AuthUser {
    private String firstName;
    private String lastName;
    private String userName;

    public AuthUser(String firstName, String lastName, String userName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUserName() {
        return userName;
    }
}
