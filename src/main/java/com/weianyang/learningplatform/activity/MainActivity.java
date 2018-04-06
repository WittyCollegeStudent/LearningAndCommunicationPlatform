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
import com.weianyang.learningplatform.entity.QuestionView;
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
    private List<QuestionView> questionViewList = new ArrayList<>();//问题列表
    private SearchView searchView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private QsBriefAdapter qsBriefAdapter;//问题适配器
    private String currMajor = "全部";//当前专业

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
        RecyclerView recyclerView_qs_brief = findViewById(R.id.recyclerview_qs_brief);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView_qs_brief.setLayoutManager(linearLayoutManager);
        recyclerView_qs_brief.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));//设置分隔线
        qsBriefAdapter = new QsBriefAdapter(MainActivity.this, questionViewList);
        recyclerView_qs_brief.setAdapter(qsBriefAdapter);
        //刷新问题概要
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        refreshQuestions(null, null);
                    }
                }).start();
            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView) view;
                Log.d(TAG, "onItemSelected: " + ((TextView) view).getText().toString());
                textView.setTextColor(getResources().getColor(R.color.white));//设置标题栏的字体颜色
                //刷新问题概要
                String major = textView.getText().toString();
                if (!major.equals("全部")) {
                    refreshQuestions(null, major);
                } else {
                    refreshQuestions(null, null);
                }
                //标记选择的专业
                currMajor = major;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        //刷新问题概要
        refreshQuestions(null, null);
    }

    //消息处理者,创建一个Handler的子类对象,目的是重写Handler的处理消息的方法(handleMessage())
    private Handler refreshHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                //请求成功，则刷新页面
                case HttpUtil.GET_DATA_SUCCESS:
                    questionViewList.clear();
                    List<QuestionView> questionViews = (List<QuestionView>) msg.obj;//从网络上获取到的问题列表
                    //全部放到将要显示的问题列表中
                    for (QuestionView questionView : questionViews) {
                        questionViewList.add(questionView);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            qsBriefAdapter.notifyDataSetChanged();
                            if (swipeRefreshLayout.isRefreshing()) {
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        }
                    });
                    break;
                //请求失败，则给予提示
                case HttpUtil.GET_DATA_FAILURE:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (swipeRefreshLayout.isRefreshing()) {
                                swipeRefreshLayout.setRefreshing(false);
                            }
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

    /**
     * 向服务器发起请求，刷新问题
     */
    public void refreshQuestions(final String qName, final String major) {
        new Thread() {
            @Override
            public void run() {
                // 01. 定义okhttp
                OkHttpClient okHttpClient_get = new OkHttpClient();
                // 02.请求体
                StringBuilder params = new StringBuilder("?qName=");
                //添加查询参数：问题名称、专业
                if (qName != null) {
                    params.append(qName);
                }
                if (major != null && !major.equals("全部")) {
                    params.append("&major=" + major);
                }
                Request request = new Request.Builder()
                        .url(HttpUtil.URL_GET_QUESTION_VIEW_SERVLET + params.toString())//网址
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
                        Type type = new TypeToken<List<QuestionView>>() {
                        }.getType();
                        List<QuestionView> questionViewBeanList = gson.fromJson(response.body().string(), type);
                        msg.what = HttpUtil.GET_DATA_SUCCESS;
                        msg.obj = questionViewBeanList;
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
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //点击查询按钮
                refreshQuestions(searchView.getQuery().toString(), null);
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

