package com.jbp689.utils;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

/**
 * volley网络请求框架封装 Created by aaron on 2017/1/6.
 */

public class VolleyUtils {

//	private ProgressDialog progressDialog;
	private VolleyUtils		instance;
    private Context mContext;
	private RequestQueue	mVolleyQueue;	// 请求队列

	public VolleyUtils(Context context){
        this.mContext = context;
		mVolleyQueue = Volley.newRequestQueue(context);
	}

	private VolleyUtils(){
	}

	public void stringRequest(String url, Response.Listener<String> listener) {
		MyStringRequest stringRequest = new MyStringRequest(Request.Method.GET, url, listener, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
                String err = error.toString();
				/*
				 * TimeoutError -- ConnectionTimeout or SocketTimeout
				 * AuthFailureError -- 401 ( UNAUTHORIZED ) && 403 ( FORBIDDEN )
				 * ServerError -- 5xx ClientError -- 4xx(Created in this demo
				 * for handling all 4xx error which are treated as Client side
				 * errors) NetworkError -- No network found ParseError -- Error
				 * while converting HTTP Response to JSONObject.
				 */
				if (error instanceof NetworkError) {
                    err = "网络异常";
				} else if (error instanceof ServerError) {
                    err = "服务端异常";
				} else if (error instanceof AuthFailureError) {
                    err = "验证失败异常";
				} else if (error instanceof ParseError) {
                    err = "解析异常";
				} else if (error instanceof NoConnectionError) {
                    err = "服务器链接异常";
				} else if (error instanceof TimeoutError) {
                    err = "超时";
				}
//				if (progressDialog!=null){
//					progressDialog.dismiss();
//				}
				MessageUtils.getInstance().closeProgressDialog();
                Toast.makeText(mContext,err+",请重试！",Toast.LENGTH_SHORT).show();
			}
		});
        stringRequest.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        stringRequest.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.85 Safari/537.36");
        mVolleyQueue.add(stringRequest);
	}
}
