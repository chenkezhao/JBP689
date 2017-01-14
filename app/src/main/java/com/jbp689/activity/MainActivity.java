package com.jbp689.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.jbp689.R;
import com.jbp689.db.dao.KLineDao;
import com.jbp689.entity.KLine;
import com.jbp689.entity.MessageEvent;
import com.jbp689.entity.TransactionDetail;
import com.jbp689.utils.CommonUtils;
import com.jbp689.utils.HtmlParseUtils;
import com.jbp689.utils.MessageUtils;
import com.jbp689.utils.StringUtils;
import com.jbp689.utils.VolleyUtils;
import com.jbp689.widgets.MyAutoCompleteTextView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends BaseActivity {

    private Button btnAnalysis;
    private MyAutoCompleteTextView etcode;
    private VolleyUtils mVolleyUtils;
    private String mCode;
    private String mDate;
    private HtmlParseUtils mHtmlParseUtils;
    private KLineDao kLineDao;
    private String codes[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHtmlParseUtils = new HtmlParseUtils();
        setMainHomeUp();
        setSubtitle("sh:上海上市的股票，sz:深圳上市的股票");
        mVolleyUtils = new VolleyUtils(MainActivity.this);
        kLineDao = KLineDao.getInstance();
        initView();
    }

    private void initView(){
        btnAnalysis = (Button) findViewById(R.id.btn_analysis);
        etcode = (MyAutoCompleteTextView) findViewById(R.id.et_code);
        codes = kLineDao.getAllCode();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, codes);
        etcode.setAdapter(adapter);
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
                    mDate = CommonUtils.dateToStringFormat(new Date());
//                  getTransactionDetail(code);//方式一
//                  nmHtmlParseUtils.parseTradeHistory(new KLine(code),"2017-01-06");//方式二
                    queryTradeHistory(mCode,mDate);//方式三
                }else{
                    etcode.setError("输入格式不正确！");
                    return;
                }
            }
        });
    }

    /**
     * 方式三
     * @param code
     * @return
     */
    private void queryTradeHistory(final String code, final String date){
        if(CommonUtils.dateToStringFormat(new Date()).equals(date)){
            //今日
            mVolleyUtils.getTransactionDetail(code);//方式一
        }else{
            //历史
            mHtmlParseUtils.parseTradeHistory(code,date,null);
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MessageEvent event) {
        KLine kLine = event.getkLine();
        TransactionDetail td = event.getTd();
        MessageUtils.getInstance().closeProgressDialog();
        Intent intent = new Intent(MainActivity.this,ResultActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("kLine", kLine);
        bundle.putSerializable("td", td);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
