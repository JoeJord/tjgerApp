package com.tjger.game.internal;

import java.util.Date;

/***
 * A high score element has a player's name and his score.
 *
 * @author hagru
 */
public class HighScoreElement {

    private String name;
    private int score;
    private Date day;

    public HighScoreElement(String name, int score, Date day) {
        super();
        this.name = name;
        this.score = score;
        this.day = (Date) day.clone();
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public Date getDay() {
        return (Date) day.clone();
    }
}