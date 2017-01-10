package com.jbp689.entity;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * 成交明细 Created by aaron on 2017/1/6.
 */

@Table(name = "T_TRANSACTIONDETAIL")
public class TransactionDetail extends EntityBase{
	@Column(name = "NAME")
	private String	name;			// 股票名称
	@Column(name = "CODE")
	private String	code;			// 股票代码
	@Column(name = "CURRENTPRICE")
	private double	currentPrice;	// 当前价格（收盘价）
	@Column(name = "OPENPRICE")
	private double	openPrice;		// 今日开盘价
	@Column(name = "HIGHPRICE")
	private double	highPrice;		// 今日最高价
	@Column(name = "LOWESTPRICE")
	private double	lowestPrice;	// 今日最低价
	@Column(name = "CLOSEPRICE")
	private double	closePrice;		// 昨日收盘价
	@Column(name = "RANGE")
	private double	range;			// 涨跌幅
	@Column(name = "DATE")
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
