package com.example.inclassassignments.InClass08;

import com.google.firebase.Timestamp;

public class Message {
    private final Timestamp timestamp;
    private final String message;

    public Message(Timestamp timestamp, String message) {
        this.timestamp = timestamp;
        this.message = message;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }
}
