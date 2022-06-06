package com.example.inclassassignments.InClass07;

import java.io.Serializable;
import java.util.ArrayList;

public class UserNotes implements Serializable {
    private ArrayList<Note> notes;

    public UserNotes(ArrayList<Note> notes) {
        this.notes = notes;
    }

    public ArrayList<Note> getNotes() {
        return notes;
    }
}
