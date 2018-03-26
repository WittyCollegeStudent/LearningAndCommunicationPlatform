package com.weianyang.learningplatform.tool;

/**
 * Created by witty on 2018/3/24.
 */

public class HttpUtil {
    //    用来测试的本地服务器
    public static final String HOST = "http://idj6nd.natappfree.cc";
    //  部署用服务器
//    public static final String HOST = "http://wittycollegestudent.top:8080/LcpServer";
    public static final String TAG = "HttpUtil";
    public static final String URL_QUESTION_SERVLET = HOST + "/questionServlet";
    public static final String URL_ANSWER_SERVLET = HOST + "/answerServlet";
    public static final int GET_DATA_SUCCESS = 0x1;//从网络上获取数据成功
    public static final int GET_DATA_FAILURE = 0x2;//从网络上获取数据失败

}
