package com.weianyang.learningplatform.tool;

import com.squareup.okhttp.MediaType;

/**
 * Created by witty on 2018/3/24.
 */

public class HttpUtil {
//      用来测试的本地服务器
    public static final String HOST = "http://ieuzaz.natappfree.cc";
    //  部署用服务器
//    public static final String HOST = "http://wittycollegestudent.top:8080/LcpServer";
    public static final String TAG = "HttpUtil";
    public static final String URL_GET_QUESTION_VIEW_SERVLET = HOST + "/getQuestionViewServlet";
    public static final String URL_GET_ANSWER_VIEW_SERVLET = HOST + "/getAnswerViewServlet";
    public static final String URL_ADD_QUESTION_SERVLET = HOST + "/addQuestionServlet";
    public static final int GET_DATA_SUCCESS = 0x1;//从网络上获取数据成功
    public static final int GET_DATA_FAILURE = 0x2;//从网络上获取数据失败

    public static final MediaType CONTENT_TYPE_UTF8 = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");

}
