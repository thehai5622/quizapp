package com.example.quizapp.model;

public class Answer {
    private int id;
    private String content;
    private int question_id;

    public Answer() {}

    public Answer(int id, String content, int question_id) {
        this.id = id;
        this.content = content;
        this.question_id = question_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(int question_id) {
        this.question_id = question_id;
    }
}
