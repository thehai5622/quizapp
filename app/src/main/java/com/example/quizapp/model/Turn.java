package com.example.quizapp.model;

public class Turn {
    private int id;
    private int correct;
    private String time;

    public Turn() {}

    public Turn(int id, int correct, String time) {
        this.id = id;
        this.correct = correct;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCorrect() {
        return correct;
    }

    public void setCorrect(int correct) {
        this.correct = correct;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
