package com.weianyang.learningplatform.tool;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

public class AlertUtil {

    public static void alert(Context context, String title, String message, DialogInterface.OnClickListener clickListener){
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
        builder.setTitle(title);//设置标题
        builder.setMessage(message); //设置详细内容
        builder.setPositiveButton("确定", clickListener);//设置取消按钮 null为按钮的点击事件
        AlertDialog loadDialog = builder.create();
        loadDialog.show();//显示dialog
    }

    public static void confirm(Context context, String title, String message, DialogInterface.OnClickListener affirmListener
            , DialogInterface.OnClickListener cancelListener){
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
        builder.setTitle(title);//设置标题
        builder.setMessage(message); //设置详细内容
        builder.setPositiveButton("确定", affirmListener);//设置确定按钮 null为按钮的点击事件
        builder.setNegativeButton("取消", cancelListener);//设置取消按钮 null为按钮的点击事件
        AlertDialog loadDialog = builder.create();
        loadDialog.show();//显示dialog
    }

}
