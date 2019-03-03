package com.example.myapp;

public class Categories {
    private String catname;
    private long catlimit;
    private long spent;

    public Categories() {
    }

    public Categories(String catname, long catlimit, long spent) {
        this.catname = catname;
        this.catlimit = catlimit;
        this.spent = spent;
    }

    public String getCatname() {
        return catname;
    }

    public void setCatname(String catname) {
        this.catname = catname;
    }

    public long getCatlimit() {
        return catlimit;
    }

    public void setCatlimit(long catlimit) {
        this.catlimit = catlimit;
    }

    public long getSpent() {
        return spent;
    }

    public void setSpent(long spent) {
        this.spent = spent;
    }
}
