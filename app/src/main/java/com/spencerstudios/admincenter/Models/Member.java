package com.spencerstudios.admincenter.Models;

public class Member {

    public String name;
    private String reason;
    private String author;
    private long timeStamp;
    private boolean isBanned;
    private String id;

    public Member() {
        /*empty constructor*/
    }

    public Member(String name, String reason, String author, long timeStamp, boolean isBanned, String id) {
        this.name = name;
        this.reason = reason;
        this.author = author;
        this.timeStamp = timeStamp;
        this.isBanned = isBanned;
        this.id = id;
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

    public long getTimeStamp() {
        return timeStamp;
    }

    public boolean isBanned() {
        return isBanned;
    }

    public void setBanned(boolean banned) {
        isBanned = banned;
    }

    public String getId(){
        return id;
    }

    public void setId(String id){
        this.id = id;
    }
}
