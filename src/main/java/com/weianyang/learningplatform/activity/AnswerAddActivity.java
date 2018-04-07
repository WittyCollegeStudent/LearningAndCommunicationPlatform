package com.weianyang.learningplatform.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.weianyang.learningplatform.R;
import com.weianyang.learningplatform.entity.AnswerView;
import com.weianyang.learningplatform.entity.QuestionView;
import com.weianyang.learningplatform.tool.ClickUtil;
import com.weianyang.learningplatform.tool.HttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;

public class AnswerAddActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "AnswerAddActivity";
    private Toolbar toolbar;
    private TextView qsName;
    private TextInputEditText inputAddAnscontent;
    private Button button_add_answer;
    private QuestionView currQs = null;

    /**
     * 启动活动
     *
     * @param context
     * @param questionView 传入的问题，用于显示问题名称
     */
    public static void actionStart(Context context, QuestionView questionView) {
        Intent intent = new Intent(context, AnswerAddActivity.class);
        intent.putExtra(QuestionView.FLAG_QUESTION, questionView);
        context.startActivity(intent);
    }

    /**
     * 消息处理者,创建一个Handler的子类对象,目的是重写Handler的处理消息的方法(handleMessage())
     */
    private Handler answerAddHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case HttpUtil.GET_DATA_SUCCESS:
                    JSONObject jsonObject = (JSONObject) msg.obj;//从网络上获取到的回答列表
                    AnswerView answerView = null;
                    Log.d(TAG, "handleMessage: " + msg.obj);
                    //是否成功插入新回答
                    boolean insertSuccess = false;
                    try {
                        insertSuccess = Boolean.parseBoolean(jsonObject.getString("success"));
                        toastOnUIThread(jsonObject.getString("message"));
                        if (insertSuccess) {
                            Gson gson = new Gson();
                            Type type = new TypeToken<AnswerView>() {
                            }.getType();
                            answerView = gson.fromJson((String) (jsonObject.get(AnswerView.VIEW_NAME)), type);
                            Log.d(TAG, "handleMessage: " + answerView);
                        }
                    } catch (JSONException e) {
                        insertSuccess = false;
                        e.printStackTrace();
                    }
                    final AnswerView asView = answerView;//表示不能再指向另外的对象
                    //如果插入成功，则跳转到问题详情页面，也就是回答概要界面
                    if (insertSuccess) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AnswerDetailActivity.actionStart(AnswerAddActivity.this, currQs.getQname(), asView);
                                finish();//跳转页面后，关闭新增页面
                            }
                        });
                    }
                    break;
                case HttpUtil.GET_DATA_FAILURE:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AnswerAddActivity.this, "获取网络数据失败", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    });
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_answer);
        toolbar = findViewById(R.id.toolbar_add_answer);
        qsName = findViewById(R.id.text_qs_name);
        button_add_answer = findViewById(R.id.button_add_answer);
        inputAddAnscontent = findViewById(R.id.input_add_anscontent);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        currQs = (QuestionView) (getIntent().getSerializableExtra(QuestionView.FLAG_QUESTION));
        qsName.setText(currQs.getQname());
        button_add_answer.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return true;
    }

    public void toastOnUIThread(final String str) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(AnswerAddActivity.this, str, Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_add_answer:
                //防止点击过快，重复执行逻辑
                if (!ClickUtil.isFastClick()) {
                    new Thread() {
                        @Override
                        public void run() {
                            String anscontent = inputAddAnscontent.getText().toString();
                            if (anscontent.isEmpty() || anscontent.length() > 20000) {
                                toastOnUIThread("问题内容不能为空，且不能超过20000");
                            } else {
                                //输入合法，则提交问题
                                //TODO:获取当前用户id
                                int respondant = 1;
                                //提交的参数
                                String params = "qid=" + currQs.getId() + "&anscontent=" + anscontent + "&respondant="
                                        + respondant;
                                // 01.定义okhttp
                                OkHttpClient okHttpClient_get = new OkHttpClient();
                                // 02.请求体
                                Request request = new Request.Builder()
                                        .url(HttpUtil.URL_ADD_ANSWER_SERVLET)//网址
                                        .post(RequestBody.create(HttpUtil.CONTENT_TYPE_UTF8, params))//get请求方式
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
                                    answerAddHandler.dispatchMessage(msg);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }.start();
                }
                break;
        }
    }
}