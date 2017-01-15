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
import com.jbp689.JBPApplication;
import com.jbp689.entity.KLine;
import com.jbp689.entity.MessageEvent;
import com.jbp689.entity.TransactionDetail;

import org.greenrobot.eventbus.EventBus;

import java.nio.charset.Charset;
import java.util.Date;

/**
 * volley网络请求框架封装 Created by aaron on 2017/1/6.
 */

public class VolleyUtils {
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
				MessageUtils.getInstance().closeProgressDialog();
                Toast.makeText(mContext,err+",请重试！",Toast.LENGTH_SHORT).show();
			}
		});
        stringRequest.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        stringRequest.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.85 Safari/537.36");
        mVolleyQueue.add(stringRequest);
	}


	/**
	 * 方式一
	 * 获取最新（今日）股票行情明细
	 * @param code
	 */
	public void getTransactionDetail(final String code){
		String url = "http://hq.sinajs.cn/?_="+new Date().getTime()+"&list="+code;
		stringRequest(url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				String temp =response.substring(response.indexOf("\"")+1,response.lastIndexOf("\""));
				if(temp.trim().length()==0){
					Toast.makeText(JBPApplication.getInstance(),"该股票代码不存在！",Toast.LENGTH_LONG).show();
					return;
				}
				String[] arr = temp.split(",");
				TransactionDetail td = new TransactionDetail();
				td.setName(arr[0]);
				td.setOpenPrice(Double.parseDouble(arr[1]));
				td.setClosePrice(Double.parseDouble(arr[2]));
				td.setCurrentPrice(Double.parseDouble(arr[3]));
				td.setHighPrice(Double.parseDouble(arr[4]));
				td.setLowestPrice(Double.parseDouble(arr[5]));
				String date = arr[30];
				if(CommonUtils.dateToFormat(date).getTime()==CommonUtils.dateToFormat(new Date()).getTime()){
					date = "今日"+arr[31];
				}
				td.setDate(date);
				startTransactionDetail(td,code);
			}
		});
	}

	/**
	 * 启动方法一
	 * 获取最新（今日）分价表
	 * @param td
	 * @param code
	 */
	private void startTransactionDetail(final TransactionDetail td, final String code){
		String url = "http://vip.stock.finance.sina.com.cn/quotes_service/view/cn_price.php?symbol="+code;
		stringRequest(url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				byte[] b = response.getBytes(Charset.forName("utf-8"));
				String html = new String(b);
				KLine kLine = new HtmlParseUtils().parseKLine(html,td,new KLine(code));
				MessageEvent event = new MessageEvent(kLine,td);
				EventBus.getDefault().post(event);
			}
		});
	}
}
