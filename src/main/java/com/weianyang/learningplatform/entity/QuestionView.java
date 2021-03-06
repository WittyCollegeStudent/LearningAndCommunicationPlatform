package com.weianyang.learningplatform.entity;

import java.io.Serializable;

/**
 * Created by weianyang on 18-2-27.
 */

public class QuestionView implements Serializable{

    public static final String FLAG_QUESTION = "QUESTION";
    public static final String FLAG_QUESTION_NAME = "QUESTION_NAME";
    public static final String VIEW_NAME = "QuestionView";

    private int id;//编号
    private String qname;//问题名称
    private String qcontent;//问题内容
    private String publisher;//提出者
    private String major;//专业
    private String isvisible;
    private String pubdate;//提出时间
    private int count;//问题总数
    private int publisher_id;

    public int getPublisher_id() {
        return publisher_id;
    }

    public void setPublisher_id(int publisher_id) {
        this.publisher_id = publisher_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQname() {
        return qname;
    }

    public void setQname(String qname) {
        this.qname = qname;
    }

    public String getQcontent() {
        return qcontent;
    }

    public void setQcontent(String qcontent) {
        this.qcontent = qcontent;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getPubdate() {
        return pubdate;
    }

    public void setPubdate(String pubdate) {
        this.pubdate = pubdate;
    }

    public String getIsvisible() {
        return isvisible;
    }

    public void setIsvisible(String isvisible) {
        this.isvisible = isvisible;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public QuestionView(int id, String qname, String qcontent, String publisher, String major, String pubdate, String isvisible, int count) {

        this.id = id;
        this.qname = qname;
        this.qcontent = qcontent;
        this.publisher = publisher;
        this.major = major;
        this.pubdate = pubdate;
        this.isvisible = isvisible;
        this.count = count;
    }

    public QuestionView() {

    }

    @Override
    public String toString() {
        return "id = " + id
                +",qname = " + qname
                +",qcontent = " + qcontent
                +",publisher = " + publisher
                +",major = " + major
                +",isVisible = " + isvisible
                +",pubdate = " + pubdate
                +",pubdate = " + pubdate;
    }
}
