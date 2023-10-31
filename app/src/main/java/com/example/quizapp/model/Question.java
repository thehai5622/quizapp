package com.example.quizapp.model;

public class Question {
    private int id;
    private String content;
    private int answer_id;

    public Question() {}

    public Question(int id, String content, int answer_id) {
        this.id = id;
        this.content = content;
        this.answer_id = answer_id;
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

    public int getAnswer_id() {
        return answer_id;
    }

    public void setAnswer_id(int answer_id) {
        this.answer_id = answer_id;
    }
}
