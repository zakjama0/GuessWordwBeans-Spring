package com.bnta.word_guesser.models;

public class Reply {

    private String wordState;
    private String message;
    private boolean correct;

    public Reply(String wordState, String message, boolean correct) {
        this.wordState = wordState;
        this.message = message;
        this.correct = correct;
    }

    public Reply() {
    }

    public String getWordState() {
        return wordState;
    }

    public void setWordState(String wordState) {
        this.wordState = wordState;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }
}
