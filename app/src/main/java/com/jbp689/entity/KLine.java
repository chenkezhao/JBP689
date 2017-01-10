package com.jbp689.entity;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * K线 Created by aaron on 2017/1/6.
 */
@Table(name = "T_KLINE")
public class KLine extends EntityBase {
	@Column(name = "NAME")
	private String	name;			// 股票名称
	@Column(name = "CODE")
	private String	code;			// 股票代码
	@Column(name = "TOTALVOLUME")
	private long	totalVolume;	// 总成交量
	@Column(name = "UPVOLUME")
	private long	upVolume;		// K线上部分
	@Column(name = "MIDDLEVOLUME")
	private long	middleVolume;	// K线中间部分
	@Column(name = "DOWNVOLUME")
	private long	downVolume;		// K线下部分
	@Column(name = "ISRED")
	private boolean	isRed;			// 是否阳线
	@Column(name = "DATE")
	private String	date;			// 日期

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
