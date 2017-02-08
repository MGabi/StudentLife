package com.minimalart.studentlife.models;

/**
 * Created by ytgab on 08.02.2017.
 */

public class FAQ {

    private String question;
    private String answer;

    public FAQ(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

}
