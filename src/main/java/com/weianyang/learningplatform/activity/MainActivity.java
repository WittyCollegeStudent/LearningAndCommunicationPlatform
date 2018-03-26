package com.weianyang.learningplatform.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.weianyang.learningplatform.R;
import com.weianyang.learningplatform.adapter.QsBriefAdapter;
import com.weianyang.learningplatform.entity.Question;
import com.weianyang.learningplatform.tool.HttpUtil;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    private Toolbar toolbar;
    private AppCompatSpinner spinner;
    private FloatingActionButton fabAddQS;//新增问题
    private ArrayAdapter<CharSequence> majorAdapter;//专业
    private List<Question> questionList = new ArrayList<>();//问题列表
    private SearchView searchView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private QsBriefAdapter qsBriefAdapter;//问题适配器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar_main);
        spinner = findViewById(R.id.spinner_main);
        fabAddQS = findViewById(R.id.fab_add_qs);
        searchView = findViewById(R.id.search);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        fabAddQS.setOnClickListener(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);//隐藏活动标题
        //下拉框
        majorAdapter = ArrayAdapter.createFromResource(MainActivity.this
                , R.array.majors, R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(majorAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView) view;
                textView.setTextColor(getResources().getColor(R.color.white));//设置标题栏的字体颜色
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        RecyclerView recyclerView_qs_brief = findViewById(R.id.recyclerview_qs_brief);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView_qs_brief.setLayoutManager(linearLayoutManager);
        recyclerView_qs_brief.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));//设置分隔线
        qsBriefAdapter = new QsBriefAdapter(MainActivity.this, questionList);
        recyclerView_qs_brief.setAdapter(qsBriefAdapter);
        //刷新问题概要
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        refreshQuestions();
                    }
                }).start();
            }
        });
        //问题概要
        refreshQuestions();
    }

    //消息处理者,创建一个Handler的子类对象,目的是重写Handler的处理消息的方法(handleMessage())
    private Handler refreshHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                //请求成功，则刷新页面
                case HttpUtil.GET_DATA_SUCCESS:
                    questionList.clear();
                    List<Question> questions = (List<Question>) msg.obj;//从网络上获取到的问题列表
                    //全部放到将要显示的问题列表中
                    for (Question question : questions) {
                        questionList.add(question);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            qsBriefAdapter.notifyDataSetChanged();
                            if (swipeRefreshLayout.isRefreshing())
                                swipeRefreshLayout.setRefreshing(false);
                        }
                    });
                    break;
                //请求失败，则给予提示
                case HttpUtil.GET_DATA_FAILURE:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "获取数据失败，请检查网络", Toast.LENGTH_SHORT).show();
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
    * 初始化问题
    * */
    public void refreshQuestions() {
        new Thread() {
            @Override
            public void run() {
                // 01. 定义okhttp
                OkHttpClient okHttpClient_get = new OkHttpClient();
                // 02.请求体
                Request request = new Request.Builder()
                        .get()//get请求方式
                        .url(HttpUtil.URL_QUESTION_SERVLET)//网址
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
                        Type type = new TypeToken<List<Question>>() {
                        }.getType();
                        List<Question> questionBeanList = gson.fromJson(response.body().string(), type);

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(MainActivity.this, "onQuerySubmit", Toast.LENGTH_SHORT).show();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.account:
                LoginActivity.actionStart(MainActivity.this);
                break;
            case R.id.setting:
                SettingsActivity.actionStart(MainActivity.this);
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_add_qs:
                QuestionAddActivity.actionStart(MainActivity.this);
                break;
            default:
                break;
        }
    }
}

