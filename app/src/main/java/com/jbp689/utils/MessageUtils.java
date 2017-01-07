package com.jbp689.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.jbp689.JBPApplication;

/**
 *
 * Created by Administrator on 2017/1/7.
 */

public class MessageUtils {

    private static ProgressDialog progressDialog;
    public static MessageUtils messageUtils;
    private MessageUtils(){
    }
    public static MessageUtils getInstance(){
        if(messageUtils==null){
            messageUtils = new MessageUtils();
        }
        return messageUtils;
    }

    public void showSnackbar(View view,String msg){
        closeProgressDialog();
        final Snackbar snackbar = Snackbar.make(view,msg,Snackbar.LENGTH_LONG);
        snackbar.show();
        snackbar.setAction("关闭", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
    }

    public void showProgressDialog(Context context,String title, String msg){
        if(title==null){
            title= "系统提示";
        }
        if (msg==null){
            msg="Loading...";
        }
        progressDialog = ProgressDialog.show(context,title, msg, true, false);
    }

    public void closeProgressDialog(){
        if(progressDialog!=null){
            progressDialog.dismiss();
        }
    }
}
