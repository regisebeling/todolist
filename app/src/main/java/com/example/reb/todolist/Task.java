package com.example.reb.todolist;

/**
 * Created by reb on 18/10/2017.
 */

public class Task {

    private String title;
    private String description;
    private String dateLimit;
    private int done;

    public String getTitle(){
        return title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public String getDateLimit(){
        return dateLimit;
    }

    public void setDateLimit(String dateLimit){
        this.dateLimit = dateLimit;
    }

    public int getDone(){
        return done;
    }

    public void setDone(int done){
        this.done = done;
    }


}
