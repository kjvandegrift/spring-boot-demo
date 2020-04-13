package com.bits;

public class ResponseTransfer {
    public ResponseTransfer(Integer answer) {
        this.answer = answer;
    }
    private Integer answer;
    public Integer getAnswer() {
        return answer;
    }
    public void setAnswer(Integer answer) {
        this.answer = answer;
    }
    @Override
    public String toString() {
        return "{\"answer\":" + answer + "}";
    }
}
