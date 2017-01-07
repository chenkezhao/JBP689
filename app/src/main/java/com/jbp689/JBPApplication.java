package com.jbp689;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

/**
 * Created by Administrator on 2017/1/7.
 */

public class JBPApplication extends Application {
	private static JBPApplication instance;

	public static JBPApplication getInstance() {
		return instance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		this.instance = JBPApplication.this;
	}

	/**
	 * 获取屏幕高度
	 * 
	 * @return
	 */
	public int getScreenHeight() {
		WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
		int height = wm.getDefaultDisplay().getHeight();
		return height;
	}

	/**
	 * 获取屏幕宽度
	 * 
	 * @return
	 */
	public int getScreenWidth() {
		WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
		int width = wm.getDefaultDisplay().getWidth();
		return width;
	}

	public View getRootView(Activity context) {
		return ((ViewGroup) context.findViewById(android.R.id.content)).getChildAt(0);
	}
}
