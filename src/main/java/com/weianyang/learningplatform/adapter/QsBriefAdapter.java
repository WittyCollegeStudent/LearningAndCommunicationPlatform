package com.weianyang.learningplatform.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.weianyang.learningplatform.activity.AnswerBriefActivity;
import com.weianyang.learningplatform.entity.QuestionView;
import com.weianyang.learningplatform.R;

import java.util.List;

import static com.weianyang.learningplatform.tool.HttpUtil.TAG;

/**
 * 问题概要适配器
 * Created by weianyang on 18-2-27.
 */

public class QsBriefAdapter extends RecyclerView.Adapter<QsBriefAdapter.ViewHolder> {

    private Context context;
    private List<QuestionView> questionViewList;

    static class ViewHolder extends RecyclerView.ViewHolder {
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

    public QsBriefAdapter(Context context, List<QuestionView> questionViewList) {
        this.questionViewList = questionViewList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_qs_brief, parent
                , false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        QuestionView questionView = questionViewList.get(position);
        String str_qs_cnt_answers = questionView.getCount() + "个回答";
        holder.qs_brief_name.setText(questionView.getQname());
        holder.qs_content.setText(questionView.getQcontent());
        holder.qs_major.setText(questionView.getMajor());
        holder.qs_cnt_answers.setText(str_qs_cnt_answers);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnswerBriefActivity.actionStart(context, questionViewList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return questionViewList.size();
    }

}
