package com.jbp689.activity;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.jbp689.R;

public class HelpActivity extends BaseActivity {

    private WebView webHelp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        setTitle("帮助说明");
        //显示html文件
        webHelp = (WebView) findViewById(R.id.web_help);
        webHelp.loadUrl("file:///android_asset/help.html");
        webHelp.setWebViewClient(new WebViewClient());
        WebSettings webSettings = webHelp.getSettings();
        webSettings.setJavaScriptEnabled(true);
    }
}
