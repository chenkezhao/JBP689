package com.jbp689.utils;

import com.android.volley.Response;
import com.jbp689.JBPApplication;
import com.jbp689.entity.KLine;
import com.jbp689.entity.MessageEvent;
import com.jbp689.entity.TransactionDetail;

import org.greenrobot.eventbus.EventBus;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * HTML解析工具包 Created by aaron on 2017/1/6.
 */

public class HtmlParseUtils {

    private TransactionDetail td;
    private boolean flag = true;
    private  boolean isFirst=true;
    private List<KLine> kLineList = new ArrayList<KLine>();
    private VolleyUtils volleyUtils;
    private int page = 1;

    public HtmlParseUtils(){
        volleyUtils = new VolleyUtils(JBPApplication.getInstance());
    }

	public KLine parseKLine(String html, TransactionDetail td,KLine kLine) {
		Document doc = Jsoup.parse(html);
		Elements trs = doc.select("div#divListTemplate>table>tbody tr");
		long totalVolume = 0; // 总成交量
		long upVolume = 0; // K线上部分
		long middleVolume = 0; // K线中间部分
		long downVolume = 0; // K线下部分
        if(td.getOpenPrice()<td.getCurrentPrice()){
            //阳线
            kLine.setRed(true);
        }else{
            kLine.setRed(false);
        }
		for (Element tr : trs) {
			// 成交价（元）
			double price = Double.parseDouble(tr.child(0).text().trim());
			// 成交量（股）
			long volume = Long.parseLong(tr.child(1).text().trim());
            totalVolume += volume;
            if(kLine.isRed()){
                //阳线
                if (price <= td.getCurrentPrice() && price > td.getOpenPrice()) {
                    // 中间
                    middleVolume+=volume;
                }else if(price>td.getCurrentPrice()){
                    //上
                    upVolume+=volume;
                }else if(price<=td.getOpenPrice()){
                    //下
                    downVolume+=volume;
                }
            }else{
                if (price <= td.getOpenPrice() && price > td.getCurrentPrice()) {
                    // 中间
                    middleVolume+=volume;
                }else if(price>td.getOpenPrice()){
                    //上
                    upVolume+=volume;
                }else if(price<=td.getCurrentPrice()){
                    //下
                    downVolume+=volume;
                }
            }
		}

        kLine.setUpVolume(upVolume);
        kLine.setMiddleVolume(middleVolume);
        kLine.setDownVolume(downVolume);
        kLine.setTotalVolume(totalVolume);
        kLine.setName(td.getName());
        kLine.setDate(td.getDate());
		return kLine;
	}

    /**
     * 解析历史成交明细
     * http://vip.stock.finance.sina.com.cn/quotes_service/view/vMS_tradehistory.php?symbol=sz002259&date=2017-01-06&page=1
     * @return
     */
    public void parseTradeHistory(final KLine kLine, final String date){
            String url = "http://vip.stock.finance.sina.com.cn/quotes_service/view/vMS_tradehistory.php?symbol="+kLine.getCode()+"&date="+date+"&page="+page;
            volleyUtils.stringRequest(url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    page++;
                    byte[] b = response.getBytes(Charset.forName("utf-8"));
                    Document doc = Jsoup.parse(new String(b));
                    if(td==null){
                        td = new TransactionDetail();
                    }
                    if(isFirst){
                        isFirst = false;
                        Element detail = doc.select("div#quote_area").first();
                        Elements detailTrs = detail.child(1).select("table>tbody>tr");
                        if(detailTrs.isEmpty()){
                            return;
                        }
                        td.setName(doc.select("h1#stockName").first().text().trim());
                        td.setCode(kLine.getCode());
                        td.setCurrentPrice(Double.parseDouble(detailTrs.get(0).child(1).text().trim()));
//                      td.setRange(Double.parseDouble(detailTrs.get(1).child(1).text().trim()));
                        td.setClosePrice(Double.parseDouble(detailTrs.get(2).child(1).text().trim()));
                        td.setOpenPrice(Double.parseDouble(detailTrs.get(3).child(1).text().trim()));
                        td.setHighPrice(Double.parseDouble(detailTrs.get(4).child(1).text().trim()));
                        td.setLowestPrice(Double.parseDouble(detailTrs.get(5).child(1).text().trim()));
                        td.setDate(date);


                        //开始统计成交量
                        if(td.getOpenPrice()<td.getCurrentPrice()){
                            //阳线
                            kLine.setRed(true);
                        }else{
                            kLine.setRed(false);
                        }
                    }
                    Elements trs= doc.select("table#datatbl>tbody>tr");
                    if(trs.select("td").size()<=1){
                        flag = false;
                        finish();
                        return;
                    }
                    //当前页的统计
                    long totalVolume = 0; // 总成交量
                    long upVolume = 0; // K线上部分
                    long middleVolume = 0; // K线中间部分
                    long downVolume = 0; // K线下部分
                    for(Element tr:trs){
                        // 成交价（元）
                        double price = Double.parseDouble(tr.child(1).text().trim());
                        // 成交量（手）
                        long volume = Long.parseLong(tr.child(4).text().trim());
                        totalVolume += volume;
                        if(kLine.isRed()){
                            //阳线
                            if (price <= td.getCurrentPrice() && price > td.getOpenPrice()) {
                                // 中间
                                middleVolume +=volume;
                            }else if(price>td.getCurrentPrice()){
                                //上
                                upVolume +=volume;
                            }else if(price<=td.getOpenPrice()){
                                //下
                                downVolume +=volume;
                            }
                        }else{
                            if (price <= td.getOpenPrice() && price > td.getCurrentPrice()) {
                                // 中间
                                middleVolume +=volume;
                            }else if(price>td.getOpenPrice()){
                                //上
                                upVolume +=volume;
                            }else if(price<=td.getCurrentPrice()){
                                //下
                                downVolume +=volume;
                            }
                        }
                    }
                    kLine.setUpVolume(upVolume);
                    kLine.setMiddleVolume(middleVolume);
                    kLine.setDownVolume(downVolume);
                    kLine.setTotalVolume(totalVolume);
                    kLine.setName(td.getName());
                    kLine.setDate(td.getDate());
                    kLineList.add(kLine);
                    parseTradeHistory(new KLine(td.getCode()),td.getDate());
                }
            });
    }


    private void finish(){
        if(kLineList.size()==0){
            EventBus.getDefault().post(null);
            return;
        }
        KLine kLine = new KLine();
        kLine.setDate(kLineList.get(0).getDate());
        kLine.setName(kLineList.get(0).getName());
        kLine.setRed(kLineList.get(0).isRed());
        long totalVolume = 0; // 总成交量
        long upVolume = 0; // K线上部分
        long middleVolume = 0; // K线中间部分
        long downVolume = 0; // K线下部分
        for(KLine k:kLineList){
            totalVolume+=k.getTotalVolume();
            upVolume+=k.getUpVolume();
            middleVolume+=k.getMiddleVolume();
            downVolume+=k.getDownVolume();
        }
        //1手=100股
        kLine.setUpVolume(upVolume/100);
        kLine.setMiddleVolume(middleVolume/100);
        kLine.setDownVolume(downVolume/100);
        kLine.setTotalVolume(totalVolume/100);
        MessageEvent event= new MessageEvent(kLine,td);
        EventBus.getDefault().post(event);
    }




    /**
     * 解析历史成交明细
     * http://vip.stock.finance.sina.com.cn/quotes_service/view/vMS_tradehistory.php?symbol=sz002259&date=2017-01-06&page=1
     * @return
     */
    public void parseTradeHistory(final String code, final String date,final TransactionDetail transactionDetail){
        final String url = "http://vip.stock.finance.sina.com.cn/quotes_service/view/vMS_tradehistory.php?symbol="+code+"&date="+date+"&page=1";
        volleyUtils.stringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                byte[] b = response.getBytes(Charset.forName("utf-8"));
                Document doc = Jsoup.parse(new String(b));
                Element detail = doc.select("div#quote_area").first();
                String name = doc.select("html>head>title").text().trim();
                name = name.substring(0,name.indexOf("("));
                if(transactionDetail!=null){
                    //今日当前行情
                    td = transactionDetail;
                    td.setCode(code);
                }else{
                    td = new TransactionDetail();
                    //历史
                    Elements detailTrs = detail.select("table>tbody>tr");
                    if(detailTrs.isEmpty()){
                        return;
                    }
                    td.setName(/*doc.select("h1#stockName").text().trim()*/name);
                    td.setCode(code);
                    td.setDate(date);
                    td.setCurrentPrice(Double.parseDouble(detailTrs.get(0).child(1).text().trim()));
                    td.setClosePrice(Double.parseDouble(detailTrs.get(2).child(1).text().trim()));
                    td.setOpenPrice(Double.parseDouble(detailTrs.get(3).child(1).text().trim()));
                    td.setHighPrice(Double.parseDouble(detailTrs.get(4).child(1).text().trim()));
                    td.setLowestPrice(Double.parseDouble(detailTrs.get(5).child(1).text().trim()));
                }
                //开始统计成交量
                boolean isRed = true;
                if(td.getOpenPrice()>td.getCurrentPrice()){
                    //阴线
                    isRed = false;
                }
                String downUrl = "http://market.finance.sina.com.cn/downxls.php?date="+date+"&symbol="+code;
                String savePath  = JBPApplication.getInstance().getUserHomePath().getAbsolutePath()+ File.separator+date+"_成交明细_"+code+".txt";
                CommonUtils.downloadSinaTradehistoryFile(downUrl,savePath,code,isRed,td);
            }
        });
    }
}
