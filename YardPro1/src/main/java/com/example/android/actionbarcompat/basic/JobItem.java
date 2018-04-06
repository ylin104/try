package com.example.android.actionbarcompat.basic;
import java.lang.String;

/**
 * Created by joanne on 4/6/15.
 * Holds data for one job
 */
public class JobItem {
    private String where;
    private String when;
    private String who;
    private String notes;
    private float cost;
    private short paid;

    JobItem(String where, String when, String who, String notes, float cost, short paid) {
        this.where = where;
        this.when = when;
        this.who = who;
        this.notes = notes;
        this.cost = cost;
        this.paid = paid;
    }

    public String getWhere() { return where; }
    public String getWhen() { return when; }
    public String getWho() { return who; }
    public String getNotes() { return notes; }
    public float getCost() { return cost; }
    public short getPaid() { return paid; }
}
