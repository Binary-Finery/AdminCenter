package com.spencerstudios.admincenter.Models;

public class Member {

    public String name;
    public String reason;
    public String author;
    public boolean alreadyWarned;
    public long timeStamp;

    public Member() {
        /*empty constructor*/
    }

    public Member(String name, String reason, String author, boolean alreadyWarned, long timeStamp) {
        this.name = name;
        this.reason = reason;
        this.author = author;
        this.alreadyWarned = alreadyWarned;
        this.timeStamp = timeStamp;
    }

    public String getName() {
        return name;
    }

    public String getReason() {
        return reason;
    }

    public String getAuthor() {
        return author;
    }

    public boolean isAlreadyWarned() {
        return alreadyWarned;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setAlreadyWarned(boolean alreadyWarned){
        this.alreadyWarned = alreadyWarned;
    }
}
