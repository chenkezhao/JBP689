package com.jbp689.utils;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by aaron on 2017/1/6.
 */

public class MyStringRequest extends StringRequest {
	private Map<String, String> headers = new HashMap<String, String>();

	public MyStringRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener){
		super(method, url, listener, errorListener);
	}

	/**
	 * 处理中文乱码
	 * 
	 * @param response
	 * @return
	 */
	@Override
	protected Response<String> parseNetworkResponse(NetworkResponse response) {
		// TODO Auto-generated method stub
		String str = null;
		try {
			str = new String(response.data, "gb2312");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Response.success(str, HttpHeaderParser.parseCacheHeaders(response));
	}

	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		return headers;
	}

	/**
	 * 设置请求头信息
	 * 
	 * @param title
	 * @param content
	 */
	public void setHeader(String title, String content) {
		headers.put(title, content);
	}
}
