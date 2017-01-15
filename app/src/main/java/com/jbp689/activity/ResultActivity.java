package com.jbp689.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.codetroopers.betterpickers.calendardatepicker.MonthAdapter;
import com.codetroopers.betterpickers.datepicker.DatePickerDialogFragment;
import com.jbp689.JBPApplication;
import com.jbp689.R;
import com.jbp689.db.dao.KLineDao;
import com.jbp689.db.dao.TransactionDetailDao;
import com.jbp689.entity.KLine;
import com.jbp689.entity.MessageEvent;
import com.jbp689.entity.TransactionDetail;
import com.jbp689.utils.CommonUtils;
import com.jbp689.utils.HtmlParseUtils;
import com.jbp689.utils.MessageUtils;
import com.jbp689.utils.StringUtils;
import com.jbp689.utils.VolleyUtils;
import com.jbp689.widgets.GestureListener;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;

public class ResultActivity extends BaseActivity {
    private static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker_name";
    private KLine mKLine;
    private TransactionDetail mTransactionDetail;
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


    private TextView currentPrice;
    private TextView openPrice;
    private TextView closePrice;
    private TextView highPrice;
    private TextView lowestPrice;

    private TransactionDetailDao transactionDetailDao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        mVolleyUtils = new VolleyUtils(this);
        mHtmlParseUtils = new HtmlParseUtils();
        transactionDetailDao = TransactionDetailDao.getInstance();
        Intent intent = this.getIntent();
        mKLine = (KLine) intent.getSerializableExtra("kLine");
        mTransactionDetail = (TransactionDetail) intent.getSerializableExtra("td");
        if(mKLine.getTotalVolume()==0){
            MessageUtils.getInstance().showSnackbar(JBPApplication.getInstance().getRootView(ResultActivity.this),"当前非交易日期或没有交易数据！");
        }
        initActionBar(mKLine);
        initView();
        if(CommonUtils.weekendMethod(CommonUtils.dateToStringFormat(new Date()))){
            totalVolume.setText("今天是周未！");
            totalVolume.setTextColor(0xffff0000);
            return;
        }
        setKLineData(mKLine,mTransactionDetail);
    }

    private void setTransactionDetail(TransactionDetail td){
        if(td==null || td.getCurrentPrice()==0){
            currentPrice.setText("");
            openPrice.setText("");
            closePrice.setText("");
            highPrice.setText("");
            lowestPrice.setText("");
            return;
        }
        double c = td.getClosePrice();
        currentPrice.setText(Html.fromHtml("<font color=#212121>当前|收盘</font>：<font color=#"+(td.getCurrentPrice()>c?"ff0000":td.getCurrentPrice()==c?"757575":"00ff00")+">"+td.getCurrentPrice()+"</font>"));
        openPrice.setText(Html.fromHtml("<font color=#212121>开盘：</font><font color=#"+(td.getOpenPrice()>c?"ff0000":td.getOpenPrice()==c?"757575":"00ff00")+">"+td.getOpenPrice()+"</font>"));
        closePrice.setText(Html.fromHtml("<font color=#212121>昨收：</font><font color=#"+(td.getClosePrice()>c?"ff0000":td.getClosePrice()==c?"757575":"00ff00")+">"+td.getClosePrice()+"</font>"));
        highPrice.setText(Html.fromHtml("<font color=#212121>最高：</font><font color=#"+(td.getHighPrice()>c?"ff0000":td.getHighPrice()==c?"757575":"00ff00")+">"+td.getHighPrice()+"</font>"));
        lowestPrice.setText(Html.fromHtml("<font color=#212121>最低：</font><font color=#"+(td.getLowestPrice()>c?"ff0000":td.getLowestPrice()==c?"757575":"00ff00")+">"+td.getLowestPrice()+"</font>"));
    }
    private void initActionBar(KLine mKLine){
        setTitle(mKLine.getName()+"("+mKLine.getCode()+")");
        setSubtitle(mKLine.getDate());
    }

    private void initView(){
        currentPrice= (TextView) findViewById(R.id.tv_currentPrice);
        openPrice= (TextView) findViewById(R.id.tv_openPrice);
        closePrice= (TextView) findViewById(R.id.tv_closePrice);
        highPrice= (TextView) findViewById(R.id.tv_highPrice);
        lowestPrice= (TextView) findViewById(R.id.tv_lowestPrice);
        totalVolume = (TextView) findViewById(R.id.tv_totalVolume);
        upVolume = (TextView) findViewById(R.id.tv_upVolume);
        middleVolume = (TextView) findViewById(R.id.tv_middleVolume);
        downVolume = (TextView) findViewById(R.id.tv_downVolume);
        wkLine = (com.jbp689.widgets.KLine)findViewById(R.id.w_kLine);
        wkLine.setLongClickable(true);
        wkLine.setOnTouchListener(new MyGestureListener(this));
        fabChangeDate = (FloatingActionButton) findViewById(R.id.fab_changeDate);
        fabChangeDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showDialog(CALENDAR_ID);
//                showDatePickerDialog();

                Calendar calendar = Calendar.getInstance();
                int year, month, day;
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);

                MonthAdapter.CalendarDay maxDate = new MonthAdapter.CalendarDay(year, month, day);
                CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment()
                        .setOnDateSetListener(new CalendarDatePickerDialogFragment.OnDateSetListener() {
                            @Override
                            public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
                                String date = year+"-"+(monthOfYear+1)+"-"+dayOfMonth ;
                                mDate = CommonUtils.dateToStringFormat(date);
                                queryTradeHistory(mKLine.getCode(),mDate);//方式三
                            }
                        })
                        .setFirstDayOfWeek(Calendar.SUNDAY)
                        .setPreselectedDate(year, month, day)
                        .setDateRange(null, maxDate)
                        .setDoneText("确定")
                        .setCancelText("取消")
                        .setThemeCustom(R.style.MyCustomBetterPickersDialogs);
                cdp.show(getSupportFragmentManager(), FRAG_TAG_DATE_PICKER);
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
//                    String date = arg1+"-"+(arg2+1)+"-"+arg3 ;
//                    mDate = CommonUtils.dateToStringFormat(date);
//                    queryTradeHistory(mKLine.getCode(),mDate);//方式三
//                }
//            }, year, month, day);
//        }
//        return null;
//    }

    /**
     * 继承GestureListener，重写left和right方法
     */
    private class MyGestureListener extends GestureListener {
        public MyGestureListener(Context context) {
            super(context);
        }
        @Override
        public boolean left() {
            String date = mKLine.getDate();
            if(date.indexOf("今日")!=-1){
                MessageUtils.getInstance().showSnackbar(JBPApplication.getInstance().getRootView(ResultActivity.this),"不能再后了！再往后就穿越了。");
                return false;
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(CommonUtils.dateToFormat(date));
            //先加一天
            calendar.set(Calendar.DATE,calendar.get(Calendar.DATE)+1);
            date=CommonUtils.dateToStringFormat(calendar.getTime());
            boolean isWeekend = false;
            while (CommonUtils.weekendMethod(date)){
                calendar.set(Calendar.DATE,calendar.get(Calendar.DATE)+1);
                date=CommonUtils.dateToStringFormat(calendar.getTime());
                isWeekend = true;
            }
            if(isWeekend){
                Toast.makeText(ResultActivity.this,"已经为你跳过周未！",Toast.LENGTH_SHORT).show();
            }
            if(CommonUtils.dateToFormat(new Date()).getTime()<CommonUtils.dateToFormat(date).getTime()){
                MessageUtils.getInstance().showSnackbar(JBPApplication.getInstance().getRootView(ResultActivity.this),"不能再后了！再往后就穿越了。");
                return false;
            }
            queryTradeHistory(mKLine.getCode(),date);//方式三
            return super.left();
        }

        @Override
        public boolean right() {
            String date = mKLine.getDate();
            if(date.indexOf("今日")!=-1){
                date=CommonUtils.dateToStringFormat(new Date());
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(CommonUtils.dateToFormat(date));
            //先减一天
            calendar.set(Calendar.DATE,calendar.get(Calendar.DATE)-1);
            date=CommonUtils.dateToStringFormat(calendar.getTime());
            boolean isWeekend = false;
            while (CommonUtils.weekendMethod(date)){
                calendar.set(Calendar.DATE,calendar.get(Calendar.DATE)-1);
                date=CommonUtils.dateToStringFormat(calendar.getTime());
                isWeekend = true;
            }
            if(isWeekend){
                Toast.makeText(ResultActivity.this,"已经为你跳过周未！",Toast.LENGTH_SHORT).show();
            }
            queryTradeHistory(mKLine.getCode(),date);//方式三
            return super.right();
        }
    }
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
        MessageUtils.getInstance().showProgressDialog(ResultActivity.this,"系统提示", "数据下载分析中...");
        if(CommonUtils.dateToStringFormat(new Date()).equals(date)){
            //今日
            mVolleyUtils.getTransactionDetail(code);//方式一
        }else{
            KLine kLine = KLineDao.getInstance().queryKLineIsExist(code,date);
            if(kLine!=null){
                //本地
                TransactionDetail td=transactionDetailDao.getTransactionDetailBy(code,date);
                setKLineData(kLine,td);
                initActionBar(kLine);
                mKLine = kLine;
//                MessageUtils.getInstance().showSnackbar(JBPApplication.getInstance().getRootView(ResultActivity.this),"本地获取历史成交明细！");
            }else{
                //历史
                mHtmlParseUtils.parseTradeHistory(code,date,null);
            }
        }
    }

    private void setKLineData(KLine mKLine,TransactionDetail td){
        MessageUtils.getInstance().closeProgressDialog();
        setTransactionDetail(td);
        if(mKLine.getTotalVolume()!=0){
            DecimalFormat df = new DecimalFormat("0.00");
            DecimalFormat dfPrice = new DecimalFormat("#,###");
            if(mKLine.isRed()){
                totalVolume.setText("总成交量："+dfPrice.format(mKLine.getTotalVolume())+unit+"（100%）");
                upVolume.setText("收盘到最高价之间的成交量-上："+dfPrice.format(mKLine.getUpVolume())+unit+"（"+df.format(mKLine.getUpVolume()*100.0/mKLine.getTotalVolume())+"%）");
                middleVolume.setText("收盘到开盘价之间的成交量-中："+dfPrice.format(mKLine.getMiddleVolume())+unit+"（"+df.format(mKLine.getMiddleVolume()*100.0/mKLine.getTotalVolume())+"%）");
                downVolume.setText("开盘到最低价之间的成交量-下："+dfPrice.format(mKLine.getDownVolume())+unit+"（"+df.format(mKLine.getDownVolume()*100.0/mKLine.getTotalVolume())+"%）");
                totalVolume.setTextColor(0xffff0000);
                upVolume.setTextColor(0xffff0000);
                middleVolume.setTextColor(0xffff0000);
                downVolume.setTextColor(0xffff0000);
                wkLine.setkLineColor(0xffff0000);
            }else{
                totalVolume.setText("总成交量："+dfPrice.format(mKLine.getTotalVolume())+unit+"（100%）");
                upVolume.setText("开盘到最高价之间的成交量-上："+dfPrice.format(mKLine.getUpVolume())+unit+"（"+df.format(mKLine.getUpVolume()*100.0/mKLine.getTotalVolume())+"%）");
                middleVolume.setText("开盘到收盘价之间的成交量-中："+dfPrice.format(mKLine.getMiddleVolume())+unit+"（"+df.format(mKLine.getMiddleVolume()*100.0/mKLine.getTotalVolume())+"%）");
                downVolume.setText("收盘到最低价之间的成交量-下："+dfPrice.format(mKLine.getDownVolume())+unit+"（"+df.format(mKLine.getDownVolume()*100.0/mKLine.getTotalVolume())+"%）");
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
            setNullData("输入的日期为非交易日期或没有交易数据！");
        }
    }
    private void setNullData(String msg){
        totalVolume.setText(msg);
        totalVolume.setTextColor(0xffff0000);
        upVolume.setText("");
        middleVolume.setText("");
        downVolume.setText("");
        wkLine.setTotalVolume(0);
        //View重新调用一次draw
        wkLine.invalidate();
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
    public void onEventMainThread(MessageEvent event) {
        KLine kLine = event.getkLine();
        TransactionDetail td = event.getTd();
        setKLineData(kLine,td);
        initActionBar(kLine);
        mKLine = kLine;
    }
}
