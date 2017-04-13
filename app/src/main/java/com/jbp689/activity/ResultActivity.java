package com.jbp689.activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.codetroopers.betterpickers.calendardatepicker.MonthAdapter;
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
import com.jbp689.utils.VolleyUtils;
import com.jbp689.widgets.GestureListener;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

public class ResultActivity extends BaseActivity {
    private static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker_name";
    private KLine mKLine;
    private TransactionDetail mTransactionDetail;
    private TextView totalVolume;
    private com.jbp689.widgets.KLine wkLine;
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
            setSubtitle("今日");
            mKLine.setDate(CommonUtils.dateToStringFormat(new Date()));
            return;
        }
        setKLineData(mKLine,mTransactionDetail);
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
        MessageUtils.getInstance().showProgressDialog(ResultActivity.this,"系统提示", "数据下载分析中...",true);
        if(CommonUtils.dateToStringFormat(new Date()).equals(date)){
            //今日
            mVolleyUtils.getTransactionDetail(code);//方式一
        }else{
            KLineDao kLineDao =  KLineDao.getInstance();
            TransactionDetailDao detailDao = TransactionDetailDao.getInstance();
            KLine kLine =kLineDao.queryKLineIsExist(code,date);
            TransactionDetail detail = detailDao.getTransactionDetailBy(code,date);
            if(kLine!=null && detail!=null){
                //本地
                TransactionDetail td=transactionDetailDao.getTransactionDetailBy(code,date);
                setKLineData(kLine,td);
                initActionBar(kLine);
                mKLine = kLine;
//                MessageUtils.getInstance().showSnackbar(JBPApplication.getInstance().getRootView(ResultActivity.this),"本地获取历史成交明细！");
            }else{
                //历史
                if(kLine!=null){
                    kLineDao.delete(kLine);
                }
                if(detail!=null){
                    detailDao.delete(detail);
                }
                mHtmlParseUtils.parseTradeHistory(code,date,null);
            }
        }
    }

    private void setKLineData(KLine mKLine,TransactionDetail td){
        MessageUtils.getInstance().closeProgressDialog();
        if(mKLine.getTotalVolume()!=0){
            DecimalFormat dfPrice = new DecimalFormat("#,###");
            totalVolume.setText("总成交量："+dfPrice.format(mKLine.getTotalVolume()*1.00/100.0)+"手（100%），换手率："+td.getTurnover());
            if(mKLine.isRed()){
                totalVolume.setTextColor(0xffff0000);
                wkLine.setkLineColor(0xffff0000);
            }else{
                totalVolume.setTextColor(0xFF005A00);
                wkLine.setkLineColor(0xFF005A00);
            }
            wkLine.setTotalVolume(mKLine.getTotalVolume());
            wkLine.setUpVolume(mKLine.getUpVolume());
            wkLine.setMiddleVolume(mKLine.getMiddleVolume());
            wkLine.setDownVolume(mKLine.getDownVolume());
            //View重新调用一次draw
            wkLine.invalidate();

            double c = td.getClosePrice();
            currentPrice.setText(Html.fromHtml("<font color=#212121>当前|收盘：</font><font color=#"+(td.getCurrentPrice()>c?"ff0000":td.getCurrentPrice()==c?"757575":"00ff00")+">"+td.getCurrentPrice()+"</font>"));
            openPrice.setText(Html.fromHtml("<font color=#212121>开盘：</font><font color=#"+(td.getOpenPrice()>c?"ff0000":td.getOpenPrice()==c?"757575":"00ff00")+">"+td.getOpenPrice()+"</font>"));
            closePrice.setText(Html.fromHtml("<font color=#212121>昨收：</font><font color=#"+(td.getClosePrice()>c?"ff0000":td.getClosePrice()==c?"757575":"00ff00")+">"+td.getClosePrice()+"</font>"));
            highPrice.setText(Html.fromHtml("<font color=#212121>最高：</font><font color=#"+(td.getHighPrice()>c?"ff0000":td.getHighPrice()==c?"757575":"00ff00")+">"+td.getHighPrice()+"</font>"));
            lowestPrice.setText(Html.fromHtml("<font color=#212121>最低：</font><font color=#"+(td.getLowestPrice()>c?"ff0000":td.getLowestPrice()==c?"757575":"00ff00")+">"+td.getLowestPrice()+"</font>"));
        }else{
            setNullData("输入的日期为非交易日期或没有交易数据！");
        }
    }
    private void setNullData(String msg){
        totalVolume.setText(msg);
        totalVolume.setTextColor(0xffff0000);
        wkLine.setTotalVolume(0);
        //View重新调用一次draw
        wkLine.invalidate();

        currentPrice.setText("");
        openPrice.setText("");
        closePrice.setText("");
        highPrice.setText("");
        lowestPrice.setText("");

        Date today = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(CommonUtils.dateToFormat(today));
        calendar.set(Calendar.DATE,calendar.get(Calendar.DATE)-1);
        Date date = CommonUtils.dateToFormat(mKLine.getDate());
        if(date!=null){
            if(calendar.getTime().getTime()==date.getTime()){
                mKLine.setDate(CommonUtils.dateToStringFormat(today));
                setSubtitle(CommonUtils.dateToStringFormat(today));
            }
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
    public void onEventMainThread(MessageEvent event) {
        KLine kLine = event.getkLine();
        mKLine = kLine;
        TransactionDetail td = event.getTd();
        setKLineData(kLine,td);
        initActionBar(kLine);
    }
}
