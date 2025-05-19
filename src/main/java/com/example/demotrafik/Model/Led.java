package com.example.demotrafik.Model;

public abstract class Led {
    private Color color;
    private int duration;

    public abstract void changeLight();


    public enum Color {
        RED, YELLOW, GREEN
    }

    // Constructor
    public Led(Color color, int duration) {
        this.color = color;
        this.duration = duration;
    }

    // Getter for color
    public Color getColor() {
        return color;
    }

    // Setter for color
    public void setColor(Color color) {
        this.color = color;
    }

    // Getter for duration
    public int getDuration() {
        return duration;
    }

    // Setter for duration
    public void setDuration(int duration) {
        this.duration = duration;
    }
}