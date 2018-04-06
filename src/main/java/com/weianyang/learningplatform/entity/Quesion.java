package com.weianyang.learningplatform.entity;

import java.io.Serializable;

public class Quesion implements Serializable {

    private int id;
    private String qname;
    private String qcontent;
    private int publisher;
    private int major;
    private int isvisible;
    private String pubdate;

    public Quesion() {
    }

    public Quesion(int id, String qname, String qcontent, int publisher, int major, int isvisible, String pubdate) {
        this.id = id;
        this.qname = qname;
        this.qcontent = qcontent;
        this.publisher = publisher;
        this.major = major;
        this.isvisible = isvisible;
        this.pubdate = pubdate;
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

    public int getPublisher() {
        return publisher;
    }

    public void setPublisher(int publisher) {
        this.publisher = publisher;
    }

    public int getMajor() {
        return major;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public int getIsvisible() {
        return isvisible;
    }

    public void setIsvisible(int isvisible) {
        this.isvisible = isvisible;
    }

    public String getPubdate() {
        return pubdate;
    }

    public void setPubdate(String pubdate) {
        this.pubdate = pubdate;
    }
}
