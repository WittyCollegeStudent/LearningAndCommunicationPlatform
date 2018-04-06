package com.weianyang.learningplatform.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.weianyang.learningplatform.R;
import com.weianyang.learningplatform.adapter.AnswerBriefAdapter;
import com.weianyang.learningplatform.entity.AnswerView;
import com.weianyang.learningplatform.entity.QuestionView;
import com.weianyang.learningplatform.tool.HttpUtil;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AnswerBriefActivity extends AppCompatActivity {

    private static final String TAG = "AnswerBriefActivity";
    private QuestionView curr_qs;//当前的问题
    private List<AnswerView> answerViewList = new ArrayList<>();//当前问题的回答列表
    private RecyclerView recyclerViewAnswerBrief;
    private AnswerBriefAdapter answerBriefAdapter;//回答概要适配器

    public static void actionStart(Context context, QuestionView questionView) {
        Intent intent = new Intent(context, AnswerBriefActivity.class);
        intent.putExtra(QuestionView.FLAG_QUESTION, questionView);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_brief);
        Intent intent = getIntent();
        curr_qs = (QuestionView) intent.getSerializableExtra(QuestionView.FLAG_QUESTION);
        Toolbar toolbar = findViewById(R.id.toolbar_answer_list);
        toolbar.setTitle(curr_qs.getQname());//设置标题
        toolbar.setTitleMarginEnd(100);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerViewAnswerBrief = findViewById(R.id.recyclerview_answer_brief);
        initAnswerViews(curr_qs.getId());//获取所有问题
        answerBriefAdapter = new AnswerBriefAdapter(AnswerBriefActivity.this
                , curr_qs, answerViewList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(AnswerBriefActivity.this);
        recyclerViewAnswerBrief.setLayoutManager(layoutManager);
        recyclerViewAnswerBrief.setAdapter(answerBriefAdapter);
    }

    //消息处理者,创建一个Handler的子类对象,目的是重写Handler的处理消息的方法(handleMessage())
    private Handler refreshHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                //请求成功，则刷新页面
                case HttpUtil.GET_DATA_SUCCESS:
                    answerViewList.clear();
                    List<AnswerView> answerViews = (List<AnswerView>) msg.obj;//从网络上获取到的回答列表
                    //全部放到将要显示的回答列表中
                    for (AnswerView answerView : answerViews) {
                        answerViewList.add(answerView);
                        Log.d(TAG, "handleMessage: " + answerView);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            answerBriefAdapter.notifyDataSetChanged();
                        }
                    });
                    break;
                //请求失败，则给予提示
                case HttpUtil.GET_DATA_FAILURE:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AnswerBriefActivity.this, "获取数据失败，请检查网络", Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    /*
     * 根据问题id获取其对应的所有回答
     * */
    public void initAnswerViews(final int qid) {
        new Thread() {
            @Override
            public void run() {
                // 01. 定义okhttp
                OkHttpClient okHttpClient_get = new OkHttpClient();
                // 02.请求体
                Request request = new Request.Builder()
                        .get()//get请求方式
                        .url(HttpUtil.URL_GET_ANSWER_VIEW_SERVLET + "?qid=" + qid)//网址
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
                        Type type = new TypeToken<List<AnswerView>>() {
                        }.getType();
                        List<AnswerView> questionBeanList = gson.fromJson(response.body().string(), type);
                        msg.what = HttpUtil.GET_DATA_SUCCESS;
                        msg.obj = questionBeanList;
                    }
                    refreshHandler.dispatchMessage(msg);
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
            default:
                break;
        }
        return true;
    }
}
