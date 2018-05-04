package com.weianyang.learningplatform.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.weianyang.learningplatform.R;
import com.weianyang.learningplatform.tool.AlertUtil;
import com.weianyang.learningplatform.tool.ClickUtil;
import com.weianyang.learningplatform.tool.HttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "LoginActivity";

    private TextView text_register;
    private EditText edit_name;
    private EditText edit_password;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Button button_login;//登录按钮

    public static void actionStart(Context context){
        Intent intent = new Intent(context,LoginActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar_login = findViewById(R.id.toolbar_login);
        edit_name = findViewById(R.id.edittext_login_name);
        edit_password = findViewById(R.id.edittext_login_password);
        button_login = findViewById(R.id.button_login);
        pref = getSharedPreferences("shared", Context.MODE_PRIVATE);
        setSupportActionBar(toolbar_login);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        text_register = findViewById(R.id.textview_register);
        text_register.setOnClickListener(this);
        button_login.setOnClickListener(this);
    }

    //消息处理者,创建一个Handler的子类对象,目的是重写Handler的处理消息的方法(handleMessage())
    private Handler loginHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            JSONObject jsonObject = (JSONObject) msg.obj;//从网络上获取到的回答列表
            boolean loginSuccess = false;//是否登录成功
            int id = -1;//用户id
            try {
                loginSuccess = Boolean.parseBoolean(jsonObject.getString("success"));
                if(loginSuccess){
                    id = Integer.parseInt(jsonObject.getString("id"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            final boolean login = loginSuccess;
            saveLoginStatus(loginSuccess);
            saveUserId(id);
            switch (msg.what) {
                //请求成功，则刷新页面
                case HttpUtil.GET_DATA_SUCCESS:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(!login){
                                //如果登录失败则弹出提示
                                AlertUtil.alert(LoginActivity.this,"登录失败", "请检查用户名密码", null);
                            }else{
                                AlertUtil.alert(LoginActivity.this,"登录成功", "", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                });
                            }
                        }
                    });
                    break;
                //请求失败，则给予提示
                case HttpUtil.GET_DATA_FAILURE:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertUtil.alert(LoginActivity.this,"请求失败", "登录失败，请检查网络", null);
                        }
                    });
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    /**
     * 登录
     */
    public void login(final String tel, final String passwd) {
        new Thread() {
            @Override
            public void run() {
                // 01. 定义okhttp
                OkHttpClient okHttpClient_get = new OkHttpClient();
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
                    Gson gson = new Gson();
                    Message msg = new Message();
                    //请求不成功
                    Log.d(TAG, "response.code = " + response.code());
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
                    saveTelPasswd(tel, passwd);
                    loginHandler.dispatchMessage(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }.start();

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.textview_register:
                RegisterActivity.actionStart(LoginActivity.this);
                break;
            case R.id.button_login:
//                toastOnUIThread("222");
                if(!ClickUtil.isFastClick()){
                    login(edit_name.getText().toString(), edit_password.getText().toString());
                }
                break;
            default:break;
        }
    }

    /**
     * 保存用户电话号码和密码以及登录状态
     */
    private void saveTelPasswd(String tel, String password){
        editor = pref.edit();
        editor.putString("tel", tel);
        editor.putString("passwd", password);
        editor.apply();
    }

    /**
     * 保存用户id
     */
    private void saveUserId(int id){
        editor = pref.edit();
        editor.putInt("id", id);
        editor.apply();
    }

    /**
     * 保存用户登录状态
     */
    private void saveLoginStatus(boolean login){
        editor = pref.edit();
        editor.putBoolean("login", login);
        editor.apply();
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
}
