package com.weianyang.learningplatform.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.weianyang.learningplatform.activity.AnswerBriefActivity;
import com.weianyang.learningplatform.entity.Question;
import com.weianyang.learningplatform.R;

import java.util.List;

/**
 * 问题概要适配器
 * Created by weianyang on 18-2-27.
 */

public class QsBriefAdapter extends RecyclerView.Adapter<QsBriefAdapter.ViewHolder> {

    private Context context;
    private List<Question> questionList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView qs_brief_name;//问题名字
        TextView qs_content;//问题内容
        TextView qs_major;//问题专业
        TextView qs_cnt_answers;//问题回答数量

        public ViewHolder(View itemView) {
            super(itemView);
            qs_brief_name = itemView.findViewById(R.id.text_qs_brief_name);
            qs_content = itemView.findViewById(R.id.text_qs_content);
            qs_major = itemView.findViewById(R.id.text_qs_major);
            qs_cnt_answers = itemView.findViewById(R.id.text_cnt_answers);
        }

    }

    public QsBriefAdapter(Context context,List<Question> questionList) {
        this.questionList = questionList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_qs_brief,parent
                ,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Question question = questionList.get(position);
        String str_qs_cnt_answers = question.getCount()+"个回答";
        holder.qs_brief_name.setText(question.getQname());
        holder.qs_content.setText(question.getQcontent());
        holder.qs_major.setText(question.getMajor());
        holder.qs_cnt_answers.setText(str_qs_cnt_answers);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnswerBriefActivity.actionStart(context,questionList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }

}
