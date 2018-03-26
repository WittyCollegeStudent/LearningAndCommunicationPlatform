package com.weianyang.learningplatform.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.weianyang.learningplatform.R;
import com.weianyang.learningplatform.activity.AnswerDetailActivity;
import com.weianyang.learningplatform.entity.Answer;
import com.weianyang.learningplatform.entity.AnswerView;
import com.weianyang.learningplatform.entity.Question;

import java.util.List;

/**
 * 问题概要适配器
 * Created by weianyang on 18-2-27.
 */

public class AnswerBriefAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "AnswerBriefAdapter";
    
    private Context context;
    private Question currQs; //当前问题
    private List<AnswerView> answerViewList;
    private final int TYPE_ANSWER_QS_CONTENT = 0; //问题内容
    private final int TYPE_ANSWER_BRIEF = 1; //回答概要

    static class ViewHolderAnswerBrief extends RecyclerView.ViewHolder{
        TextView answer_respondant;//回答者
        TextView answer_content;//回答内容
        TextView thumb_and_date_info;//点赞点踩及日期信息

        public ViewHolderAnswerBrief(View itemView) {
            super(itemView);
            answer_respondant = itemView.findViewById(R.id.text_answer_respondant);
            answer_content = itemView.findViewById(R.id.text_answer_content);
            thumb_and_date_info = itemView.findViewById(R.id.text_thumb_and_date_info);
        }

    }

    static class ViewHolderAnswerQsContent extends RecyclerView.ViewHolder{
        TextView answer_qs_content;//问题的具体内容

        public ViewHolderAnswerQsContent(View itemView) {
            super(itemView);
            answer_qs_content = itemView.findViewById(R.id.text_answer_list_qs_content);
        }
    }

    public AnswerBriefAdapter(Context context, Question currQs, List<AnswerView> answerList) {
        this.context = context;
        this.currQs = currQs;
        this.answerViewList = answerList;
    }

    /*
    * 第一项view的ViewHolder,用于显示问题的具体内容
    * */
    @Override
    public int getItemViewType(int position) {
        //只有第一项是问题内容,其他项是回答的概要
        if(position == 0)
            return TYPE_ANSWER_QS_CONTENT;
        return TYPE_ANSWER_BRIEF;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return  getViewHolderByViewType(parent,viewType);
    }

    private RecyclerView.ViewHolder getViewHolderByViewType(ViewGroup parent,int viewType){
        View view;
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType){
            case TYPE_ANSWER_QS_CONTENT:
                view = LayoutInflater.from(context).inflate(R.layout.item_answer_qs_content,parent,false);
                viewHolder = new ViewHolderAnswerQsContent(view);
                break;
            case TYPE_ANSWER_BRIEF:
                view = LayoutInflater.from(context).inflate(R.layout.item_answer_brief,parent,false);
                viewHolder = new ViewHolderAnswerBrief(view);
                break;
            default:break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: ");
        if(position == 0) {
            //问题内容
            String qsContent = currQs.getQcontent();
            ((ViewHolderAnswerQsContent) holder).answer_qs_content.setText(qsContent);
        }else {
            //回答列表
            final AnswerView answerView = answerViewList.get(position - 1);
            String answerInfo = answerView.getVote_p() + "赞同．" + answerView.getVote_n()
                    + "反对． " + answerView.getAns_date();
            ((ViewHolderAnswerBrief) holder).answer_respondant.setText(answerView.getRespondant());
            ((ViewHolderAnswerBrief) holder).answer_content.setText(answerView.getAnscontent());
            ((ViewHolderAnswerBrief) holder).thumb_and_date_info.setText(answerInfo);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AnswerDetailActivity.actionStart(context,currQs.getQname(),answerView);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        //返回值大于0时才会执行onBindViewHolder
        return answerViewList.size() + 1;
    }

}
