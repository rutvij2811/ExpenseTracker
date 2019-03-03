package com.example.myapp;

public class Record {
    private long amount;
    private int cid;
    private long date;

    public Record() {
    }

    public Record(long amount, int cid, long date) {
        this.amount = amount;
        this.cid = cid;
        this.date = date;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
