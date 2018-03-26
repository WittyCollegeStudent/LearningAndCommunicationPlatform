package com.weianyang.learningplatform.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.weianyang.learningplatform.R;
import com.weianyang.learningplatform.entity.Answer;
import com.weianyang.learningplatform.entity.AnswerView;
import com.weianyang.learningplatform.entity.Question;

public class AnswerDetailActivity extends AppCompatActivity {

    public static void actionStart(Context context, String qsName, AnswerView answerView) {
        Intent intent = new Intent(context, AnswerDetailActivity.class);
        intent.putExtra(Question.FLAG_QUESTION_NAME, qsName);
        intent.putExtra(AnswerView.FLAG_ANSWER_VIEW, answerView);
        context.startActivity(intent);
    }

    private TextView respondant;    //回答者
    private TextView answerContent; //回复内容
    private TextView answerDate;    //回复时间

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_detail);
        Toolbar toolbar = findViewById(R.id.toolbar_answer_detail);
        respondant = findViewById(R.id.text_answer_detail_respondant);
        answerContent = findViewById(R.id.text_answer_detail_content);
        answerDate = findViewById(R.id.answer_detail_date);
        Intent intent = getIntent();
        String qsName = (String) intent.getSerializableExtra(Question.FLAG_QUESTION_NAME);//问题名称
        AnswerView answerView = (AnswerView) intent.getSerializableExtra(AnswerView.FLAG_ANSWER_VIEW);
        toolbar.setTitle(qsName);//工具栏标题设置为问题名称
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        respondant.setText(answerView.getRespondant());         //设置问题回复者
        answerContent.setText(answerView.getAnscontent());   //设置问题内容
        answerDate.setText(answerView.getAns_date());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}
