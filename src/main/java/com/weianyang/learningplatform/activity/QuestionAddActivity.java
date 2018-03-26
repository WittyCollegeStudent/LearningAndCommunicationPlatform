package com.weianyang.learningplatform.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.weianyang.learningplatform.R;

/**
 * Created by weianyang on 18-2-28.
 */

public class QuestionAddActivity extends AppCompatActivity {

    private ArrayAdapter<CharSequence> adapter_major;
    private AppCompatSpinner spinner_add_qs;


    /*
    * 启动活动
    * */
    public static void actionStart(Context context){
        Intent intent = new Intent(context, QuestionAddActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question);
        Toolbar toolbar_add_qs = findViewById(R.id.toolbar_add_qs);
        setSupportActionBar(toolbar_add_qs);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        spinner_add_qs = findViewById(R.id.spinner_add_qs);
        //下拉框
        adapter_major = ArrayAdapter.createFromResource(QuestionAddActivity.this
                ,R.array.majors,R.layout.support_simple_spinner_dropdown_item);
        spinner_add_qs.setAdapter(adapter_major);
        spinner_add_qs.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();break;
            default:break;
        }
        return true;
    }
}
