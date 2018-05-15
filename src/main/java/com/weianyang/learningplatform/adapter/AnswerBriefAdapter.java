package com.weianyang.learningplatform.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.weianyang.learningplatform.R;
import com.weianyang.learningplatform.activity.AnswerDetailActivity;
import com.weianyang.learningplatform.entity.AnswerView;
import com.weianyang.learningplatform.entity.QuestionView;
import com.weianyang.learningplatform.tool.AlertUtil;
import com.weianyang.learningplatform.tool.ClickUtil;
import com.weianyang.learningplatform.tool.HttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

/**
 * 问题概要适配器
 * Created by weianyang on 18-2-27.
 */

public class AnswerBriefAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "AnswerBriefAdapter";

    private Context context;
    private QuestionView currQs; //当前问题
    private List<AnswerView> answerViewList;
    private static final int TYPE_ANSWER_QS_CONTENT = 0; //问题内容
    private static final int TYPE_ANSWER_BRIEF = 1; //回答概要

    static class ViewHolderAnswerBrief extends RecyclerView.ViewHolder {
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

    static class ViewHolderAnswerQsContent extends RecyclerView.ViewHolder {
        public static TextView answer_qs_content;//问题的具体内容
        public static Button button_delete_question;

        public ViewHolderAnswerQsContent(View itemView) {
            super(itemView);
            answer_qs_content = itemView.findViewById(R.id.text_answer_list_qs_content);
            button_delete_question = itemView.findViewById(R.id.button_delete_question);
        }
    }

    public AnswerBriefAdapter(Context context, QuestionView currQs, List<AnswerView> answerList) {
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
        if (position == 0)
            return TYPE_ANSWER_QS_CONTENT;
        return TYPE_ANSWER_BRIEF;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return getViewHolderByViewType(parent, viewType);
    }

    private RecyclerView.ViewHolder getViewHolderByViewType(ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case TYPE_ANSWER_QS_CONTENT:
                view = LayoutInflater.from(context).inflate(R.layout.item_answer_qs_content, parent, false);
                viewHolder = new ViewHolderAnswerQsContent(view);
                break;
            case TYPE_ANSWER_BRIEF:
                view = LayoutInflater.from(context).inflate(R.layout.item_answer_brief, parent, false);
                viewHolder = new ViewHolderAnswerBrief(view);
                break;
            default:
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: ");
        if (position == 0) {
            //问题内容
            String qsContent = currQs.getQcontent();
            ((ViewHolderAnswerQsContent) holder).answer_qs_content.setText(qsContent);
            SharedPreferences pref = context.getSharedPreferences("shared", Context.MODE_PRIVATE);
            int uid = pref.getInt("id", -1);
            int ansCnt = currQs.getCount();
            if(uid == currQs.getPublisher_id() && ansCnt == 0){
                Button button_delete_question = ((ViewHolderAnswerQsContent) holder).button_delete_question;
                button_delete_question.setVisibility(View.VISIBLE);
                button_delete_question.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertUtil.confirm(context,"警告", "确定要删除这个回答？", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences pref = context.getSharedPreferences("shared", Context.MODE_PRIVATE);
                                String tel = pref.getString("tel", "");
                                String pwd = pref.getString("passwd", "");
                                ClickUtil.switchButtonClickable(ViewHolderAnswerQsContent.button_delete_question);
                                delQuestionById(tel, pwd, currQs.getId());
                            }
                        }, null);
                    }
                });
            }
        } else {
            //回答列表
            final AnswerView answerView = answerViewList.get(position - 1);
//            String answerInfo = answerView.getVote_p() + "赞同．" + answerView.getVote_n()
//                    + "反对． " + answerView.getAns_date();
            String answerInfo = answerView.getAns_date();
            ((ViewHolderAnswerBrief) holder).answer_respondant.setText(answerView.getRespondant());
            ((ViewHolderAnswerBrief) holder).answer_content.setText(answerView.getAnscontent());
            ((ViewHolderAnswerBrief) holder).thumb_and_date_info.setText(answerInfo);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AnswerDetailActivity.actionStart(context, currQs.getQname(), answerView);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        //返回值大于0时才会执行onBindViewHolder
        return answerViewList.size() + 1;
    }

    //消息处理者,创建一个Handler的子类对象,目的是重写Handler的处理消息的方法(handleMessage())
    private Handler delQuestionHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            JSONObject jsonObject = (JSONObject) msg.obj;//从网络上获取到的回答列表
            boolean delSuccess = false;//是否登录成功
            String message = "";//是否登录成功
            try {
                delSuccess = Boolean.parseBoolean(jsonObject.getString("success"));
                if(!delSuccess){
                    message = jsonObject.getString("message");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            final boolean delSucc = delSuccess;
            final String message_final = message;
            switch (msg.what) {
                //请求成功，则刷新页面
                case HttpUtil.GET_DATA_SUCCESS:
                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(!delSucc){
                                //如果登录失败则弹出提示
                                AlertUtil.alert(context,"错误", message_final, null);
                            }else{
                                AlertUtil.alert(context,"删除成功", "", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ((Activity)context).finish();
                                    }
                                });
                            }
                            ClickUtil.switchButtonClickable(ViewHolderAnswerQsContent.button_delete_question);
                        }
                    });
                    break;
                //请求失败，则给予提示
                case HttpUtil.GET_DATA_FAILURE:
                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertUtil.alert(context,"请求失败", "登录失败，请检查网络", null);
                        }
                    });
                    break;
                default:
                    break;
            }
            return false;
        }
    });


    public void delQuestionById(final String tel, final String passwd, final int question_id) {
        new Thread() {
            @Override
            public void run() {
                // 01. 定义okhttp
                OkHttpClient okHttpClient_get = new OkHttpClient();
                // 02.请求体
                StringBuilder params = new StringBuilder("?tel=");
                //添加查询参数：问题名称、专业
                params.append(tel);
                params.append("&passwd=" + passwd);
                params.append("&question_id=" + question_id);
                Request request = new Request.Builder()
                        .url(HttpUtil.URL_DEL_QUESTION_SERVLET + params.toString())//网址
                        .get()//get请求方式
                        .build();
                Response response = null;
                try {
                    response = okHttpClient_get.newCall(request).execute();
                    Message msg = new Message();
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
                    delQuestionHandler.dispatchMessage(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }.start();

    }

}
