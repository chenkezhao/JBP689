package com.jbp689.entity;

/**
 * Created by Administrator on 2017/1/13.
 */

public class MessageEvent {
    private KLine kLine;
    private TransactionDetail td;

    public MessageEvent() {
    }

    public MessageEvent(KLine kLine, TransactionDetail td) {
        this.kLine = kLine;
        this.td = td;
    }

    public KLine getkLine() {
        return kLine;
    }

    public void setkLine(KLine kLine) {
        this.kLine = kLine;
    }

    public TransactionDetail getTd() {
        return td;
    }

    public void setTd(TransactionDetail td) {
        this.td = td;
    }
}
