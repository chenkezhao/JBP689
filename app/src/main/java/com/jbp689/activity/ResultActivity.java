package com.jbp689.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.jbp689.R;
import com.jbp689.entity.KLine;

import java.text.DecimalFormat;

public class ResultActivity extends BaseActivity {

    private KLine mKLine;
    private TextView totalVolume;
    private TextView upVolume;
    private TextView middleVolume;
    private TextView downVolume;
    private TextView date;
    private com.jbp689.widgets.KLine wkLine;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        Intent intent = this.getIntent();
        mKLine = (KLine) intent.getSerializableExtra("kLine");
        initActionBar();
        initView();
        setData();

    }

    private void initView(){
        totalVolume = (TextView) findViewById(R.id.tv_totalVolume);
        upVolume = (TextView) findViewById(R.id.tv_upVolume);
        middleVolume = (TextView) findViewById(R.id.tv_middleVolume);
        downVolume = (TextView) findViewById(R.id.tv_downVolume);
        date = (TextView) findViewById(R.id.tv_date);
        wkLine = (com.jbp689.widgets.KLine)findViewById(R.id.w_kLine);
    }

    private void setData(){
        if(mKLine!=null){
            DecimalFormat df = new DecimalFormat("0.00");
            if(mKLine.isRed()){
                date.setText(mKLine.getDate());
                totalVolume.setText("总成交量(手)："+mKLine.getTotalVolume()+"（100%）");
                upVolume.setText("收盘价->最高价 成交量(手)-上："+mKLine.getUpVolume()+"（"+df.format(mKLine.getUpVolume()*100.0/mKLine.getTotalVolume())+"%）");
                middleVolume.setText("收盘价->开盘价 成交量(手)-中："+mKLine.getMiddleVolume()+"（"+df.format(mKLine.getMiddleVolume()*100.0/mKLine.getTotalVolume())+"%）");
                downVolume.setText("开盘价->最低价 成交量(手)-下："+mKLine.getDownVolume()+"（"+df.format(mKLine.getDownVolume()*100.0/mKLine.getTotalVolume())+"%）");
                totalVolume.setTextColor(0xffff0000);
                upVolume.setTextColor(0xffff0000);
                middleVolume.setTextColor(0xffff0000);
                downVolume.setTextColor(0xffff0000);
                date.setTextColor(0xffff0000);
                wkLine.setkLineColor(0xffff0000);
            }else{
                date.setText(mKLine.getDate());
                totalVolume.setText("总成交量(手)："+mKLine.getTotalVolume()+"（100%）");
                upVolume.setText("开盘价->最高价 成交量(手)-上："+mKLine.getUpVolume()+"（"+df.format(mKLine.getUpVolume()*100.0/mKLine.getTotalVolume())+"%）");
                middleVolume.setText("开盘价->收盘价 成交量(手)-中："+mKLine.getMiddleVolume()+"（"+df.format(mKLine.getMiddleVolume()*100.0/mKLine.getTotalVolume())+"%）");
                downVolume.setText("收盘价->最低价 成交量(手)-下："+mKLine.getDownVolume()+"（"+df.format(mKLine.getDownVolume()*100.0/mKLine.getTotalVolume())+"%）");
                totalVolume.setTextColor(0xFF005A00);
                upVolume.setTextColor(0xFF005A00);
                middleVolume.setTextColor(0xFF005A00);
                downVolume.setTextColor(0xFF005A00);
                date.setTextColor(0xFF005A00);
                wkLine.setkLineColor(0xFF005A00);
            }
            wkLine.setTotalVolume(mKLine.getTotalVolume());
            wkLine.setUpVolume(mKLine.getUpVolume());
            wkLine.setMiddleVolume(mKLine.getMiddleVolume());
            wkLine.setDownVolume(mKLine.getDownVolume());
            //View重新调用一次draw
            wkLine.invalidate();
        }
    }
    private void initActionBar(){
        ActionBar actionbar = getSupportActionBar();
        if(actionbar!=null){
            actionbar.setTitle(mKLine.getName());
            actionbar.setSubtitle(mKLine.getCode());
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setDisplayShowHomeEnabled(false);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.help:
                startActivity(new Intent(ResultActivity.this,HelpActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
