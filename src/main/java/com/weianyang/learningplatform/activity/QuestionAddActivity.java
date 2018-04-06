package com.weianyang.learningplatform.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.weianyang.learningplatform.R;
import com.weianyang.learningplatform.entity.QuestionView;
import com.weianyang.learningplatform.tool.ClickUtil;
import com.weianyang.learningplatform.tool.HttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * Created by weianyang on 18-2-28.
 */

public class QuestionAddActivity extends AppCompatActivity implements View.OnClickListener {

    private ArrayAdapter<CharSequence> adapter_major;
    private AppCompatSpinner spinner_add_qs;
    private Button btn_add_qs;//提交按钮
    private int majorId = 0;// 默认是0(全部)
    private TextInputEditText input_qs_desc;
    private TextInputEditText input_qs_content;

    private static final String TAG = "QuestionAddActivity";

    /**
     * 启动活动
     */
    public static void actionStart(Context context) {
        Intent intent = new Intent(context, QuestionAddActivity.class);
        context.startActivity(intent);
    }

    /**
     * 消息处理者,创建一个Handler的子类对象,目的是重写Handler的处理消息的方法(handleMessage())
     */
    private Handler questionAddHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case HttpUtil.GET_DATA_SUCCESS:
                    JSONObject jsonObject = (JSONObject) msg.obj;//从网络上获取到的回答列表
                    QuestionView questionView = null;
                    Log.d(TAG, "handleMessage: " + msg.obj);
                    //是否成功插入新问题
                    boolean insertSuccess = false;
                    try {
                        insertSuccess = Boolean.parseBoolean(jsonObject.getString("success"));
                        toastOnUIThread(jsonObject.getString("message"));
                        if (insertSuccess) {
                            Gson gson = new Gson();
                            Type type = new TypeToken<QuestionView>() {
                            }.getType();
                            questionView = gson.fromJson((String) (jsonObject.get(QuestionView.VIEW_NAME)), type);
                            Log.d(TAG, "handleMessage: " + questionView);
                        }
                    } catch (JSONException e) {
                        insertSuccess = false;
                        e.printStackTrace();
                    }
                    final QuestionView qsView = questionView;//表示不能再指向另外的对象
                    //如果插入成功，则跳转到问题详情页面，也就是回答概要界面
                    if (insertSuccess) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AnswerBriefActivity.actionStart(QuestionAddActivity.this, qsView);
                                finish();//跳转页面后，关闭新增页面
                            }
                        });
                    }
                    break;
                case HttpUtil.GET_DATA_FAILURE:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(QuestionAddActivity.this, "获取网络数据失败", Toast.LENGTH_SHORT)
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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question);
        Toolbar toolbar_add_qs = findViewById(R.id.toolbar_add_qs);
        btn_add_qs = findViewById(R.id.button_add_qs);
        input_qs_desc = findViewById(R.id.input_qs_name);
        input_qs_content = findViewById(R.id.input_qs_content);
        btn_add_qs.setOnClickListener(this);
        setSupportActionBar(toolbar_add_qs);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        spinner_add_qs = findViewById(R.id.spinner_add_qs);
        //下拉框
        adapter_major = ArrayAdapter.createFromResource(QuestionAddActivity.this
                , R.array.majors, R.layout.support_simple_spinner_dropdown_item);
        spinner_add_qs.setAdapter(adapter_major);
        spinner_add_qs.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                majorId = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
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
                Toast.makeText(QuestionAddActivity.this, str, Toast.LENGTH_SHORT)
                        .show();
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_add_qs:
                //防止点击过快，重复执行逻辑
                if (!ClickUtil.isFastClick()) {
                    new Thread() {
                        @Override
                        public void run() {
                            String qs_name = input_qs_desc.getText().toString(); //问题名称
                            String qs_content = input_qs_content.getText().toString();
                            if (majorId == 0) {
                                toastOnUIThread("请选择专业");
                            } else if (qs_name.isEmpty() || qs_name.length() > 30) {
                                toastOnUIThread("问题名称不能为空，且不能超过30");
                            } else if (qs_content.isEmpty() || qs_content.length() > 9000) {
                                toastOnUIThread("问题内容不能为空，且不能超过9000");
                            } else {
                                //输入合法，则提交问题
                                //TODO:这里默认提交者是1号，实际上应该从本地取值
                                int publisher = 1;
                                //提交的参数
                                String params = "qname=" + qs_name + "&qcontent=" + qs_content + "&publisher=" + publisher
                                        + "&major=" + majorId + "&isvisible=" + "1";
                                // 01. 定义okhttp
                                OkHttpClient okHttpClient_get = new OkHttpClient();
                                // 02.请求体
                                Request request = new Request.Builder()
                                        .url(HttpUtil.URL_ADD_QUESTION_SERVLET)//网址
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
                                    questionAddHandler.dispatchMessage(msg);
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
