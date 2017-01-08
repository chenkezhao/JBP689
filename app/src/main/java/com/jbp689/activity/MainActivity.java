package com.jbp689.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends BaseActivity {

    Button btnAnalysis;
    TextInputEditText etcode;
    private VolleyUtils mVolleyUtils;
    private String mCode;
    private String mDate;

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
                String pattern = "^(sh|sz)[0-9]{6}$";
                // 创建 Pattern 对象
                Pattern r = Pattern.compile(pattern);
                // 现在创建 matcher 对象
                Matcher m = r.matcher(code);
                if (m.find()) {
                    MessageUtils.getInstance().showProgressDialog(MainActivity.this,"系统提示", "数据下载分析中...");
                    mCode = code;
                    mDate = "2017-01-06";
//                  getTransactionDetail(code);//方式一
//                  new HtmlParseUtils().parseTradeHistory(new KLine(code),"2017-01-06");//方式二
                    queryTradeHistory(code,mDate);//方式三
                }else{
                    etcode.setError("输入格式不正确！");
                    return;
                }
            }
        });
    }


    /**
     * 获取最新股票明细信息
     * @param code
     */
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
                td.setDate(arr[30]+" "+arr[31]);
                start(td,code);
            }
        });
    }

    /**
     * 获取最新分价表
     * @param td
     * @param code
     */
    private void start(final TransactionDetail td, final String code){
        String url = "http://vip.stock.finance.sina.com.cn/quotes_service/view/cn_price.php?symbol="+code;
        mVolleyUtils.stringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                byte[] b = response.getBytes(Charset.forName("utf-8"));
                String html = new String(b);
                KLine kLine = new HtmlParseUtils().parseKLine(html,td,new KLine(code));
                MessageUtils.getInstance().closeProgressDialog();
                Intent intent = new Intent(MainActivity.this,ResultActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("kLine", kLine);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(KLine kLine) {
        MessageUtils.getInstance().closeProgressDialog();
        if(StringUtils.isBlank(kLine.getCode())){
            MessageUtils.getInstance().showSnackbar(JBPApplication.getInstance().getRootView(MainActivity.this),"该股票代码不存在！");
            return;
        }
        Intent intent = new Intent(MainActivity.this,ResultActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("kLine", kLine);
        intent.putExtras(bundle);
        startActivity(intent);
    }




    final private int REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 123;
    private void queryTradeHistory(String code,String date) {
        if(android.os.Build.VERSION.SDK_INT>=23){
            checkPermission();
        }
        new HtmlParseUtils().parseTradeHistory(code,date);
    }

    @TargetApi(23)
    private void checkPermission(){
        int hasWriteContactsPermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                MessageUtils.getInstance().showAlertDialog(this, "系统提示", "如果不赋予程序任何权限，程序将不会运行！", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                REQUEST_CODE_WRITE_EXTERNAL_STORAGE);
                    }
                });
                return;
            }
            requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE_WRITE_EXTERNAL_STORAGE);
            return;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_WRITE_EXTERNAL_STORAGE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    new HtmlParseUtils().parseTradeHistory(mCode,mDate);
                } else {
                    // Permission Denied
                    MessageUtils.getInstance().closeProgressDialog();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
