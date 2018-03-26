package com.weianyang.learningplatform.entity;

import java.io.Serializable;

/**
 * Created by weianyang on 18-3-8.
 */

public class Answer implements Serializable{

    public static String FLAG_ANSWER = "ANSWER";

    private int id;                 //回答id
    private int qid;                //问题id
    private String answerContent;   //回答内容
    private String answerDate;      //回答日期
    private String respondant;      //回答者
    private int cntThumbUp;         //点赞数量
    private int cntThumbDown;       //点踩数量

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQid() {
        return qid;
    }

    public void setQid(int qid) {
        this.qid = qid;
    }

    public String getAnswerContent() {
        return answerContent;
    }

    public void setAnswerContent(String answerContent) {
        this.answerContent = answerContent;
    }

    public String getAnswerDate() {
        return answerDate;
    }

    public void setAnswerDate(String answerDate) {
        this.answerDate = answerDate;
    }

    public String getRespondant() {
        return respondant;
    }

    public void setRespondant(String respondant) {
        this.respondant = respondant;
    }

    public int getCntThumbUp() {
        return cntThumbUp;
    }

    public void setCntThumbUp(int cntThumbUp) {
        this.cntThumbUp = cntThumbUp;
    }

    public int getCntThumbDown() {
        return cntThumbDown;
    }

    public void setCntThumbDown(int cntThumbDown) {
        this.cntThumbDown = cntThumbDown;
    }

    public Answer(int id, int qid, String answerContent, String answerDate, String respondant, int cntThumbUp, int cntThumbDown) {

        this.id = id;
        this.qid = qid;
        this.answerContent = answerContent;
        this.answerDate = answerDate;
        this.respondant = respondant;
        this.cntThumbUp = cntThumbUp;
        this.cntThumbDown = cntThumbDown;
    }

    public Answer() {

    }
}
