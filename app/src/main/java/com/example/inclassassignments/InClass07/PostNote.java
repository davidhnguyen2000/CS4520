package com.example.inclassassignments.InClass07;

public class PostNote {
    boolean posted;
    Note note;

    public PostNote(boolean posted, Note note) {
        this.posted = posted;
        this.note = note;
    }

    public boolean isPosted() {
        return posted;
    }

    public Note getNote() {
        return note;
    }
}
