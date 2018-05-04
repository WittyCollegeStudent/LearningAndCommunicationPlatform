package com.weianyang.learningplatform.entity;

import java.io.Serializable;

/**
 * Created by weianyang on 18-3-8.
 */

public class UserInfoView implements Serializable {

    public static String FLAG_USERINFO = "USERINFO_VIEW";

    public static final String VIEW_NAME = "UserInfoView";
    public static final String ID = "m_id";
    public static final String TYPE = "v_type";
    public static final String NAME = "v_name";
    public static final String SEX = "v_sex";
    public static final String TEL = "v_tel";
    public static final String MAJOR = "v_major";
    public static final String PASSWD = "v_passwd";

    private int id;                 //回答id
    private String type;           //用户类别
    private String name;            //用户姓名
    private String sex;                //性别
    private String tel;             //电话号码
    private String major;              //专业
    private String passwd;          //密码

    public UserInfoView(int id, String type, String name, String sex, String tel, String major, String passwd) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.sex = sex;
        this.tel = tel;
        this.major = major;
        this.passwd = passwd;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }
}
