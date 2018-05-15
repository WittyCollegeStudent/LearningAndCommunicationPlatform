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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.weianyang.learningplatform.R;
import com.weianyang.learningplatform.entity.AnswerView;
import com.weianyang.learningplatform.entity.QuestionView;
import com.weianyang.learningplatform.tool.AlertUtil;
import com.weianyang.learningplatform.tool.ClickUtil;
import com.weianyang.learningplatform.tool.HttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class AnswerDetailActivity extends AppCompatActivity implements View.OnClickListener{

    public static void actionStart(Context context, String qsName, AnswerView answerView) {
        Intent intent = new Intent(context, AnswerDetailActivity.class);
        intent.putExtra(QuestionView.FLAG_QUESTION_NAME, qsName);
        intent.putExtra(AnswerView.FLAG_ANSWER_VIEW, answerView);
        context.startActivity(intent);
    }

    private TextView respondant;    //回答者
    private TextView answerContent; //回复内容
    private TextView answerDate;    //回复时间
    private Button button_delete_answer;//删除回答按钮
    private int aid; //回答id

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_detail);
        Toolbar toolbar = findViewById(R.id.toolbar_answer_detail);
        respondant = findViewById(R.id.text_answer_detail_respondant);
        answerContent = findViewById(R.id.text_answer_detail_content);
        answerDate = findViewById(R.id.answer_detail_date);
        button_delete_answer = findViewById(R.id.button_delete_question);
        Intent intent = getIntent();
        String qsName = (String) intent.getSerializableExtra(QuestionView.FLAG_QUESTION_NAME);//问题名称
        AnswerView answerView = (AnswerView) intent.getSerializableExtra(AnswerView.FLAG_ANSWER_VIEW);
        toolbar.setTitle(qsName);//工具栏标题设置为问题名称
        toolbar.setTitleMarginEnd(100);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        respondant.setText(answerView.getRespondant());         //设置问题回复者
        answerContent.setText(answerView.getAnscontent());   //设置问题内容
        answerDate.setText(answerView.getAns_date());
        aid = answerView.getId();
        SharedPreferences pref = getSharedPreferences("shared", Context.MODE_PRIVATE);
        boolean login = pref.getBoolean("login", false);
        if(login){
            int userId = pref.getInt("id", -1);
            if(userId == answerView.getRespondant_id()){
                button_delete_answer.setVisibility(View.VISIBLE);
            }
        }
        button_delete_answer.setOnClickListener(this);
    }

    //消息处理者,创建一个Handler的子类对象,目的是重写Handler的处理消息的方法(handleMessage())
    private Handler delAnswerHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            JSONObject jsonObject = (JSONObject) msg.obj;//从网络上获取到的回答列表
            boolean delSuccess = false;//是否登录成功
            String message = "";//是否登录成功
            try {
                delSuccess = Boolean.parseBoolean(jsonObject.getString("success"));
                if(!delSuccess){
                    message = jsonObject.getString("message");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            final boolean delSucc = delSuccess;
            final String message_final = message;
            switch (msg.what) {
                //请求成功，则刷新页面
                case HttpUtil.GET_DATA_SUCCESS:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(!delSucc){
                                //如果登录失败则弹出提示
                                AlertUtil.alert(AnswerDetailActivity.this,"错误", message_final, null);
                            }else{
                                AlertUtil.alert(AnswerDetailActivity.this,"删除成功", "", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                });
                            }
                            ClickUtil.switchButtonClickable(button_delete_answer);
                        }
                    });
                    break;
                //请求失败，则给予提示
                case HttpUtil.GET_DATA_FAILURE:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertUtil.alert(AnswerDetailActivity.this,"请求失败", "登录失败，请检查网络", null);
                        }
                    });
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    public void delAnswerById(final String tel, final String passwd, final int answer_id) {
        new Thread() {
            @Override
            public void run() {
                // 01. 定义okhttp
                OkHttpClient okHttpClient_get = new OkHttpClient();
                // 02.请求体
                StringBuilder params = new StringBuilder("?tel=");
                //添加查询参数：问题名称、专业
                params.append(tel);
                params.append("&passwd=" + passwd);
                params.append("&answer_id=" + answer_id);
                Request request = new Request.Builder()
                        .url(HttpUtil.URL_DEL_ANSWER_SERVLET + params.toString())//网址
                        .get()//get请求方式
                        .build();
                Response response = null;
                try {
                    response = okHttpClient_get.newCall(request).execute();
                    Message msg = new Message();
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
                    delAnswerHandler.dispatchMessage(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }.start();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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
            case R.id.button_delete_question:
                AlertUtil.confirm(AnswerDetailActivity.this,"警告", "确定要删除这个回答？", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences pref = getSharedPreferences("shared", Context.MODE_PRIVATE);
                        String tel = pref.getString("tel", "");
                        String pwd = pref.getString("passwd", "");
                        ClickUtil.switchButtonClickable(button_delete_answer);
                        delAnswerById(tel, pwd, aid);
                    }
                }, null);
                break;
            default:break;
        }
    }
}
