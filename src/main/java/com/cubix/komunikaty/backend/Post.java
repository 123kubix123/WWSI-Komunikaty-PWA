package com.cubix.komunikaty.backend;

public class Post {

    private String title = "";
    private String date = "";
    private String content = "";

    public Post(String title, String date, String content){
        super();
        this.title = title;
        this.date = date;
        this.content = content;
    }

    public Post(){

    }

    public String getContent() {
        return content;
    }

    public String getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public boolean equals (Object other) {

        if (null == other) return false;

        if (! (other instanceof Post)) return false;

        if (!title.equals(((Post) other).title) ) return false;

        if (!date.equals(((Post) other).date)) return false;

        if (!content.equals(((Post) other).content)) return false;

        return true;

    }
}
