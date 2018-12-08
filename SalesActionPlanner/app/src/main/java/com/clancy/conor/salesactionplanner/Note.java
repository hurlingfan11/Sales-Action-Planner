package com.clancy.conor.salesactionplanner;

import com.google.firebase.firestore.Exclude;

public class Note {

    private String documentId;
    private String title;
    private String description;
    private String owner;

    public Note() {
        //public no-arg constructor needed
    }

    public Note(String title, String description, int priority) {
        this.title = title;
        this.description = description;
        this.owner = owner;
    }

    @Exclude
    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}

