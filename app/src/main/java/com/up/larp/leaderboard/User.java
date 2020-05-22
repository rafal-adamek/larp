package com.up.larp.leaderboard;

/**
 * Simple class implements info about user
 */
public class User {

    private String name;
    private int points;

    public User(String name, int points) {
        this.name = name;
        this.points = points;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
