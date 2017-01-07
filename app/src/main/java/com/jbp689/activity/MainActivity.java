package com.jbp689.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.android.volley.Response;
import com.jbp689.JBPApplication;
import com.jbp689.R;
import com.jbp689.entity.KLine;
import com.jbp689.entity.TransactionDetail;
import com.jbp689.utils.HtmlParseUtils;
import com.jbp689.utils.MessageUtils;
import com.jbp689.utils.StringUtils;
import com.jbp689.utils.VolleyUtils;

import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    Button btnAnalysis;
    TextInputEditText etcode;
//    private ProgressDialog progressDialog;
    private VolleyUtils mVolleyUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initActonBar();
        mVolleyUtils = new VolleyUtils(MainActivity.this);
        initView();
        initEnvent();
    }
    private void initActonBar(){
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setSubtitle("sh:上海上市的股票，sz:深圳上市的股票");
        }
    }
    private void initView(){
        btnAnalysis = (Button) findViewById(R.id.btn_analysis);
        etcode = (TextInputEditText) findViewById(R.id.et_code);
    }
    private void initEnvent(){
        btnAnalysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String code = etcode.getText().toString();
                if(StringUtils.isBlank(code)){
                    etcode.setError("请输入股票代码！");
                    return;
                }
                //验证模板
                String pattern = "(sh|sz)[0-9]{6}";
                // 创建 Pattern 对象
                Pattern r = Pattern.compile(pattern);
                // 现在创建 matcher 对象
                Matcher m = r.matcher(code);
                if (m.find()) {
//                    progressDialog = ProgressDialog.show(MainActivity.this, "系统提示", "数据分析中...", true, false);
                    MessageUtils.getInstance().showProgressDialog(MainActivity.this,"系统提示", "数据分析中...");
                    getTransactionDetail(code);
                }else{
                    etcode.setError("输入格式不正确！");
                    return;
                }
            }
        });
    }


    private void getTransactionDetail(final String code){
        String url = "http://hq.sinajs.cn/list="+code;
        mVolleyUtils.stringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String temp =response.substring(response.indexOf("\"")+1,response.lastIndexOf("\""));
                if(temp.trim().length()==0){
                    MessageUtils.getInstance().showSnackbar(JBPApplication.getInstance().getRootView(MainActivity.this),"该股票代码不存在！");
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
                td.setDate(arr[30]+arr[31]);
                start(td,code);
            }
        });
    }

    private void start(final TransactionDetail td, final String code){
        String url = "http://vip.stock.finance.sina.com.cn/quotes_service/view/cn_price.php?symbol="+code;
        mVolleyUtils.stringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                byte[] b = response.getBytes(Charset.forName("utf-8"));
                String html = new String(b);
                KLine kLine = new HtmlParseUtils().parseKLine(html,td,new KLine(code));
//                progressDialog.dismiss();
                MessageUtils.getInstance().closeProgressDialog();
                Intent intent = new Intent(MainActivity.this,ResultActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("kLine", kLine);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
}
