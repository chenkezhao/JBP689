package com.jbp689.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.codetroopers.betterpickers.calendardatepicker.MonthAdapter;
import com.codetroopers.betterpickers.datepicker.DatePickerDialogFragment;
import com.jbp689.JBPApplication;
import com.jbp689.R;
import com.jbp689.db.dao.KLineDao;
import com.jbp689.entity.KLine;
import com.jbp689.utils.CommonUtils;
import com.jbp689.utils.HtmlParseUtils;
import com.jbp689.utils.MessageUtils;
import com.jbp689.utils.StringUtils;
import com.jbp689.utils.VolleyUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

public class ResultActivity extends BaseActivity {
    private static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker_name";
    private KLine mKLine;
    private TextView totalVolume;
    private TextView upVolume;
    private TextView middleVolume;
    private TextView downVolume;
    private com.jbp689.widgets.KLine wkLine;
    private String  unit = "手";
    private FloatingActionButton fabChangeDate;
    private final int CALENDAR_ID=689;
    private String mDate;
    private VolleyUtils mVolleyUtils;
    private HtmlParseUtils mHtmlParseUtils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        mVolleyUtils = new VolleyUtils(this);
        mHtmlParseUtils = new HtmlParseUtils();
        Intent intent = this.getIntent();
        mKLine = (KLine) intent.getSerializableExtra("kLine");
        if(mKLine.getTotalVolume()==0){
            MessageUtils.getInstance().showSnackbar(JBPApplication.getInstance().getRootView(ResultActivity.this),"当前没数据（不是交易日或未开盘）");
        }
        initActionBar(mKLine);
        initView();
        setKLineData(mKLine);
    }

    private void initActionBar(KLine mKLine){
        setTitle(mKLine.getName()+"("+mKLine.getCode()+")");
        setSubtitle(mKLine.getDate());
    }

    private void initView(){
        totalVolume = (TextView) findViewById(R.id.tv_totalVolume);
        upVolume = (TextView) findViewById(R.id.tv_upVolume);
        middleVolume = (TextView) findViewById(R.id.tv_middleVolume);
        downVolume = (TextView) findViewById(R.id.tv_downVolume);
        wkLine = (com.jbp689.widgets.KLine)findViewById(R.id.w_kLine);
        fabChangeDate = (FloatingActionButton) findViewById(R.id.fab_changeDate);
        fabChangeDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showDialog(CALENDAR_ID);
                showDatePickerDialog();

//                Calendar now = Calendar.getInstance();
//                now.add(Calendar.DATE, 1);
//                MonthAdapter.CalendarDay maxDate = new MonthAdapter.CalendarDay(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
//                CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment()
//                        .setOnDateSetListener(new CalendarDatePickerDialogFragment.OnDateSetListener() {
//                            @Override
//                            public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
//                                MessageUtils.getInstance().showProgressDialog(ResultActivity.this,"系统提示","数据下载分析中...");
//                                String date = year+"-"+(monthOfYear+1)+"-"+dayOfMonth ;
//                                mDate = CommonUtils.dateToStringFormat(date);
//                                queryTradeHistory(mKLine.getCode(),mDate);//方式三
//                            }
//                        })
//                        .setFirstDayOfWeek(Calendar.SUNDAY)
//                        .setPreselectedDate(year, month, day)
//                        .setDateRange(null, maxDate)
//                        .setDoneText("确定")
//                        .setCancelText("取消")
//                        .setThemeLight();
//                cdp.show(getSupportFragmentManager(), FRAG_TAG_DATE_PICKER);
            }
        });
    }
//    @Override
//    protected Dialog onCreateDialog(int id) {
//        // TODO Auto-generated method stub
//        if (id == CALENDAR_ID) {
//            return new DatePickerDialog(this, new  DatePickerDialog.OnDateSetListener() {
//                @Override
//                public void onDateSet(DatePicker arg0,
//                                      int arg1, int arg2, int arg3) {
//                    // TODO Auto-generated method stub
//                    // arg1 = year, arg2 = month, arg3 = day
//                    MessageUtils.getInstance().showProgressDialog(ResultActivity.this,"系统提示","数据下载分析中...");
//                    String date = arg1+"-"+(arg2+1)+"-"+arg3 ;
//                    mDate = CommonUtils.dateToStringFormat(date);
//                    queryTradeHistory(mKLine.getCode(),mDate);//方式三
//                }
//            }, year, month, day);
//        }
//        return null;
//    }

    public void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year, month, day;
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dpd = new DatePickerDialog(this/*,R.style.Custom*/, new  DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker arg0,
                                  int arg1, int arg2, int arg3) {
                // TODO Auto-generated method stub
                // arg1 = year, arg2 = month, arg3 = day
                MessageUtils.getInstance().showProgressDialog(ResultActivity.this,"系统提示","数据下载分析中...");
                String date = arg1+"-"+(arg2+1)+"-"+arg3 ;
                mDate = CommonUtils.dateToStringFormat(date);
                queryTradeHistory(mKLine.getCode(),mDate);//方式三
            }
        }, year, month, day);
        dpd.getDatePicker().setMaxDate(new Date().getTime());
        dpd.show();
    }

    /**
     * 方式三
     * @param code
     * @return
     */
    private void queryTradeHistory(String code, String date){
        if(CommonUtils.dateToStringFormat(new Date()).equals(date)){
            //今日
            mVolleyUtils.getTransactionDetail(code);//方式一
        }else{
            KLine kLine = KLineDao.getInstance().queryKLineIsExist(code,date);
            if(kLine!=null){
                //本地
                setKLineData(kLine);
                MessageUtils.getInstance().showSnackbar(JBPApplication.getInstance().getRootView(ResultActivity.this),"本地获取历史成交明细！");
            }else{
                //历史
                mHtmlParseUtils.parseTradeHistory(code,date,null);
            }
        }
    }

    private void setKLineData(KLine mKLine){
        if(mKLine.getTotalVolume()!=0){
            DecimalFormat df = new DecimalFormat("0.00");
            if(mKLine.isRed()){
                totalVolume.setText("总成交量("+unit+")："+mKLine.getTotalVolume()+"（100%）");
                upVolume.setText("收盘价->最高价 成交量("+unit+")-上："+mKLine.getUpVolume()+"（"+df.format(mKLine.getUpVolume()*100.0/mKLine.getTotalVolume())+"%）");
                middleVolume.setText("收盘价->开盘价 成交量("+unit+")-中："+mKLine.getMiddleVolume()+"（"+df.format(mKLine.getMiddleVolume()*100.0/mKLine.getTotalVolume())+"%）");
                downVolume.setText("开盘价->最低价 成交量("+unit+")-下："+mKLine.getDownVolume()+"（"+df.format(mKLine.getDownVolume()*100.0/mKLine.getTotalVolume())+"%）");
                totalVolume.setTextColor(0xffff0000);
                upVolume.setTextColor(0xffff0000);
                middleVolume.setTextColor(0xffff0000);
                downVolume.setTextColor(0xffff0000);
                wkLine.setkLineColor(0xffff0000);
            }else{
                totalVolume.setText("总成交量("+unit+")："+mKLine.getTotalVolume()+"（100%）");
                upVolume.setText("开盘价->最高价 成交量("+unit+")-上："+mKLine.getUpVolume()+"（"+df.format(mKLine.getUpVolume()*100.0/mKLine.getTotalVolume())+"%）");
                middleVolume.setText("开盘价->收盘价 成交量("+unit+")-中："+mKLine.getMiddleVolume()+"（"+df.format(mKLine.getMiddleVolume()*100.0/mKLine.getTotalVolume())+"%）");
                downVolume.setText("收盘价->最低价 成交量("+unit+")-下："+mKLine.getDownVolume()+"（"+df.format(mKLine.getDownVolume()*100.0/mKLine.getTotalVolume())+"%）");
                totalVolume.setTextColor(0xFF005A00);
                upVolume.setTextColor(0xFF005A00);
                middleVolume.setTextColor(0xFF005A00);
                downVolume.setTextColor(0xFF005A00);
                wkLine.setkLineColor(0xFF005A00);
            }
            wkLine.setTotalVolume(mKLine.getTotalVolume());
            wkLine.setUpVolume(mKLine.getUpVolume());
            wkLine.setMiddleVolume(mKLine.getMiddleVolume());
            wkLine.setDownVolume(mKLine.getDownVolume());
            //View重新调用一次draw
            wkLine.invalidate();
        }else{
            totalVolume.setText("今日没数据，请查询历史数据！");
            totalVolume.setTextColor(0xffff0000);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(KLine kLine) {
        MessageUtils.getInstance().closeProgressDialog();
        if(StringUtils.isBlank(kLine.getCode()) || kLine.getTotalVolume()==0){
            MessageUtils.getInstance().showSnackbar(JBPApplication.getInstance().getRootView(ResultActivity.this),"该股票代码不存在或者当前没数据（不是交易日）！");
            return;
        }
        setKLineData(kLine);
        initActionBar(kLine);
    }
}
