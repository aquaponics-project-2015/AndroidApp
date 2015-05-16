package com.example.entity;

/**
 * Created by Dennis on 13/05/2015.
 */
public class Fish {
    private String name;
    private double minph;
    private  double maxph;
    private boolean checked = false;

    public Fish(double maxph, double minph, String name) {
        this.maxph = maxph;
        this.minph = minph;
        this.name = name;
    }

    public double getMaxph() {
        return maxph;
    }

    public double getMinph() {
        return minph;
    }

    public String getName() {
        return name;
    }

    public void setChecked(){
        this.checked = true;
    }

    public void setUnchecked(){
        this.checked = false;
    }
}
