package com.weianyang.learningplatform.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.weianyang.learningplatform.R;
import com.weianyang.learningplatform.tool.AlertUtil;
import com.weianyang.learningplatform.tool.HttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class UserInfoActivity extends AppCompatActivity implements View.OnClickListener {

    private Button logout;
    private TextView text_type;
    private TextView text_name;
    private TextView text_sex;
    private TextView text_user_tel;
    private TextView text_major;

    public static void actionStart(Context context){
        Intent intent = new Intent(context,UserInfoActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        logout = findViewById(R.id.btn_logout);
        text_type = findViewById(R.id.text_type);
        text_name = findViewById(R.id.text_name);
        text_sex = findViewById(R.id.text_sex);
        text_user_tel = findViewById(R.id.text_user_tel);
        text_major = findViewById(R.id.text_major);
        logout.setOnClickListener(this);
        Toolbar toolbar_login = findViewById(R.id.toolbar_user_info);
        setSupportActionBar(toolbar_login);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        requestUserInfo();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            default:break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_logout:
                setLogout();
                finish();
                LoginActivity.actionStart(UserInfoActivity.this);
                break;
            default:break;
        }
    }

    //消息处理者,创建一个Handler的子类对象,目的是重写Handler的处理消息的方法(handleMessage())
    private Handler loginHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            JSONObject jsonObject = (JSONObject) msg.obj;//从网络上获取到的回答列表
            boolean loginSuccess = false;//是否登录成功
            final String type, major, name, sex, tel;
            try {
                loginSuccess = Boolean.parseBoolean(jsonObject.getString("success"));
                if(loginSuccess){
                    type = jsonObject.getString("type");
                    major = jsonObject.getString("major");
                    name = jsonObject.getString("name");
                    sex = jsonObject.getString("sex");
                    tel = jsonObject.getString("tel");
                    final boolean login = loginSuccess;
                    switch (msg.what) {
                        //请求成功，则刷新页面
                        case HttpUtil.GET_DATA_SUCCESS:
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(!login){
                                        //如果登录失败则弹出提示
                                        AlertUtil.alert(UserInfoActivity.this,"错误", "获取用户信息失败", null);
                                    }else{
                                        text_type.setText(type);
                                        text_major.setText(major);
                                        text_name.setText(name);
                                        text_sex.setText(sex);
                                        text_user_tel.setText(tel);
                                    }
                                }
                            });
                            break;
                        //请求失败，则给予提示
                        case HttpUtil.GET_DATA_FAILURE:
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AlertUtil.alert(UserInfoActivity.this,"请求失败", "获取用户信息失败，请检查网络", null);
                                }
                            });
                            break;
                        default:
                            break;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return false;
        }
    });

    /**
     * 获取用户数据
     */
    public void requestUserInfo() {
        new Thread() {
            @Override
            public void run() {
                // 01. 定义okhttp
                OkHttpClient okHttpClient_get = new OkHttpClient();
                SharedPreferences pref = getSharedPreferences("shared", Context.MODE_PRIVATE);
                String tel = pref.getString("tel", "");
                String passwd = pref.getString("passwd", "");
                // 02.请求体
                StringBuilder params = new StringBuilder("?tel=");
                //添加查询参数：问题名称、专业
                if (tel != null) {
                    params.append(tel);
                }
                if (passwd != null && !passwd.equals("全部")) {
                    params.append("&passwd=" + passwd);
                }
                Request request = new Request.Builder()
                        .url(HttpUtil.URL_LOGIN_SERVLET + params.toString())//网址
                        .get()//get请求方式
                        .build();
                Response response = null;
                try {
                    response = okHttpClient_get.newCall(request).execute();
                    Message msg = new Message();
                    //请求不成功
                    if (response.code() != 200) {
                        msg.what = HttpUtil.GET_DATA_FAILURE;
                    } else {
                        //成功
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response.body().string());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        msg.what = HttpUtil.GET_DATA_SUCCESS;
                        msg.obj = jsonObject;
                    }
                    loginHandler.dispatchMessage(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }.start();

    }

    /**
     * 将登录状态设置为未登录
     */
    private void setLogout(){
        SharedPreferences pref = getSharedPreferences("shared", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("login", false);
        editor.apply();
    }
}
