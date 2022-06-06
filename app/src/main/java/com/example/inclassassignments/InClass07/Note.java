package com.example.inclassassignments.InClass07;

import java.io.Serializable;

public class Note implements Serializable {
    private final String _id;
    private final String userId;
    private final String text;
    private final int __v;

    public Note(String _id, String userId, String text, int __v) {
        this._id = _id;
        this.userId = userId;
        this.text = text;
        this.__v = __v;
    }

    public String getUserId() {
        return userId;
    }

    public String getText() {
        return text;
    }

    public String get_id() {
        return _id;
    }

    @Override
    public String toString() {
        return "Note{" +
                "text='" + text + '\'' +
                ", _id='" + _id + '\'' +
                '}';
    }
}
