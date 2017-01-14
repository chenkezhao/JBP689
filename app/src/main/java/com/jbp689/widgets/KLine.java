package com.jbp689.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.jbp689.JBPApplication;
import com.jbp689.R;

import java.text.DecimalFormat;

/**
 * K线绘制
 * Created by Administrator on 2017/1/7.
 */

public class KLine extends View{


    /*属性*/
    private long	totalVolume;	// 总成交量
    private long	upVolume;		// K线上部分
    private long	middleVolume;	// K线中间部分
    private long	downVolume;		// K线下部分
    private int kLineColor;//颜色

    public KLine(Context context) {
        super(context);
    }

    public KLine(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public KLine(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //从布局文件中读取的自定义属性解析出来
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.KLine,defStyleAttr,0);
        int n = a.getIndexCount();
        for(int i=0;i<n;i++){
            int attr = a.getIndex(i);
            switch (attr){
                case R.styleable.KLine_totalVolume:
                    totalVolume = a.getInt(attr,1);
                    break;
                case R.styleable.KLine_upVolume:
                    upVolume = a.getInt(attr,0);
                    break;
                case R.styleable.KLine_middleVolume:
                    middleVolume = a.getInt(attr,0);
                    break;
                case R.styleable.KLine_downVolume:
                    downVolume = a.getInt(attr,0);
                    break;
                case R.styleable.KLine_kLineColor:
                    kLineColor = a.getInt(attr,0xff000000);
                    break;
                default:
                    break;
            }

        }

    }

    @TargetApi(21)
    public KLine(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    /**
     * 测量
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 绘制
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        /*控件使用的油漆（画笔）*/
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(kLineColor);

        if(totalVolume==0){
//            paint.setColor(0xffffffff);
//            canvas.drawRect(new Rect(0, 0, this.getWidth(), this.getHeight()),paint);
            return;
        }

        int screenWidth = JBPApplication.getInstance().getScreenWidth();
        int startHeight = 100;
        int endHeight = this.getHeight()-2*startHeight;
        int viewHeight = endHeight-startHeight;
        //线
        canvas.drawLine(screenWidth/2, startHeight, screenWidth/2, endHeight, paint);
        Paint divider =new Paint(Paint.ANTI_ALIAS_FLAG);
        divider.setColor(getResources().getColor(R.color.colorDivider));
        canvas.drawLine(0, 10, screenWidth, 10, divider);
        canvas.drawLine(0, this.getHeight()-10, screenWidth, this.getHeight()-10, divider);

        canvas.drawLine(screenWidth/2, startHeight, screenWidth/2, endHeight, paint);
        //矩形
        DecimalFormat df = new DecimalFormat("#");
        int top = Integer.parseInt(df.format((upVolume*1.0/totalVolume)*viewHeight+startHeight));
        int bottom = Integer.parseInt(df.format(((upVolume+middleVolume)*1.0/totalVolume)*viewHeight+startHeight));
        canvas.drawRect(new Rect((screenWidth/2)-50, top, (screenWidth/2)+50, bottom),paint);
        //绘制描述线
        int tempTop = top-startHeight;
        int tempBotton = bottom-startHeight;
        canvas.drawLine(screenWidth/2,tempTop/2+startHeight,screenWidth/4,tempTop/2+startHeight, paint);
        canvas.drawLine(screenWidth/2,tempBotton-(tempBotton-tempTop)/2+startHeight,screenWidth*3/4,tempBotton-(tempBotton-tempTop)/2+startHeight, paint);
        canvas.drawLine(screenWidth/2,endHeight-((viewHeight-tempBotton)/2),screenWidth/4,endHeight-((viewHeight-tempBotton)/2), paint);
        //绘制文字
        TextPaint textPaint = new TextPaint(paint);
        textPaint.setAlpha(255);
        textPaint.setTextSize(30);
        textPaint.setColor(kLineColor);
        textPaint.setTextAlign(Paint.Align.CENTER);
        DecimalFormat df1 = new DecimalFormat("0.00");
        canvas.drawText(df1.format(upVolume*100.0/totalVolume)+"%", screenWidth/4,tempTop/2+startHeight, textPaint);
        canvas.drawText(df1.format(middleVolume*100.0/totalVolume)+"%", screenWidth*3/4,tempBotton-(tempBotton-tempTop)/2+startHeight, textPaint);
        canvas.drawText(df1.format(downVolume*100.0/totalVolume)+"%", screenWidth/4,endHeight-((viewHeight-tempBotton)/2), textPaint);
    }

    public long getTotalVolume() {
        return totalVolume;
    }

    public void setTotalVolume(long totalVolume) {
        this.totalVolume = totalVolume;
    }

    public long getUpVolume() {
        return upVolume;
    }

    public void setUpVolume(long upVolume) {
        this.upVolume = upVolume;
    }

    public long getMiddleVolume() {
        return middleVolume;
    }

    public void setMiddleVolume(long middleVolume) {
        this.middleVolume = middleVolume;
    }

    public long getDownVolume() {
        return downVolume;
    }

    public void setDownVolume(long downVolume) {
        this.downVolume = downVolume;
    }

    public int getkLineColor() {
        return kLineColor;
    }

    public void setkLineColor(int kLineColor) {
        this.kLineColor = kLineColor;
    }
}
