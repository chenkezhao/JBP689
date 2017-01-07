package com.jbp689.entity;

/**
 * 成交明细 Created by aaron on 2017/1/6.
 */

public class TransactionDetail {
	private String	name;			// 股票名称
	private String	code;			// 股票代码
	private double	currentPrice;	// 当前价格（收盘价）
	private double	openPrice;		// 今日开盘价
	private double	highPrice;		// 今日最高价
	private double	lowestPrice;	// 今日最低价
	private double	closePrice;		// 昨日收盘价
	private double	range;			// 涨跌幅
	private String	date;			// 日期+时间

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getCurrentPrice() {
		return currentPrice;
	}

	public void setCurrentPrice(double currentPrice) {
		this.currentPrice = currentPrice;
	}

	public double getOpenPrice() {
		return openPrice;
	}

	public void setOpenPrice(double openPrice) {
		this.openPrice = openPrice;
	}

	public double getHighPrice() {
		return highPrice;
	}

	public void setHighPrice(double highPrice) {
		this.highPrice = highPrice;
	}

	public double getLowestPrice() {
		return lowestPrice;
	}

	public void setLowestPrice(double lowestPrice) {
		this.lowestPrice = lowestPrice;
	}

	public double getClosePrice() {
		return closePrice;
	}

	public void setClosePrice(double closePrice) {
		this.closePrice = closePrice;
	}

	public double getRange() {
		return range;
	}

	public void setRange(double range) {
		this.range = range;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
}
