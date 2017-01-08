package com.jbp689.entity;

import java.io.Serializable;

/**
 * K线 Created by aaron on 2017/1/6.
 */

public class KLine implements Serializable {
	private String	name;			// 股票名称
	private String	code;			// 股票代码
	private long	totalVolume;	// 总成交量
	private long	upVolume;		// K线上部分
	private long	middleVolume;	// K线中间部分
	private long	downVolume;		// K线下部分
	private boolean	isRed;			// 是否阳线
	private String	date;			// 日期+时间

	public KLine() {
	}
	public KLine(String code) {
		this.code = code;
	}
	public KLine(boolean isRed) {
		this.isRed = isRed;
	}


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

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
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

	public boolean isRed() {
		return isRed;
	}

	public void setRed(boolean red) {
		isRed = red;
	}
}
