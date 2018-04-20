package com.spencerstudios.admincenter.Models;

public class Note {

    private String author;
    private String subject;
    private String note;
    private long timeStamp;
    private String id;

    public Note(){

    }

    public Note(String subject, String note, String author, long timeStamp, String id) {
        this.author = author;
        this.subject = subject;
        this.note = note;
        this.timeStamp = timeStamp;
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public String getSubject() {
        return subject;
    }

    public String getNote() {
        return note;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public String getId(){
        return id;
    }

}
