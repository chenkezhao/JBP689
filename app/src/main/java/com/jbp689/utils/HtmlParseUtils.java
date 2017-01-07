package com.jbp689.utils;

import com.jbp689.entity.KLine;
import com.jbp689.entity.TransactionDetail;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * HTML解析工具包 Created by aaron on 2017/1/6.
 */

public class HtmlParseUtils {

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
}
