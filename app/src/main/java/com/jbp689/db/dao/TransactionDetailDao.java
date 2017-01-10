package com.jbp689.db.dao;

import com.jbp689.entity.TransactionDetail;

import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.SqlInfoBuilder;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;

import java.util.List;

/**
 * 访问T_TRANSACTIONDETAIL
 * Created by Administrator on 2017/1/10.
 */

public class TransactionDetailDao extends BaseDao{
    private static  TransactionDetailDao mTransactionDetailDao = new TransactionDetailDao();
    private TransactionDetailDao() {
        super();
    }
    public static TransactionDetailDao getInstance(){
        return mTransactionDetailDao;
    }
    /**
     * 插入
     * @param transactionDetail
     */
    public void insert(TransactionDetail transactionDetail) {
        try {
            db.save(transactionDetail);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 批量插入
     * @param transactionDetailList
     */
    public void insert(List<TransactionDetail> transactionDetailList) {
        try {
            db.save(transactionDetailList);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 清空表
     */
    public void clearTable(){

        try {
            db.delete(TransactionDetail.class, WhereBuilder.b("id", "!=", "0"));
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据id删除
     * @param id
     */
    public void deleteById(String id){

        try {
            db.delete(TransactionDetail.class, WhereBuilder.b("id", "=", id));
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除
     */
    public void delete(TransactionDetail transactionDetail){
        try {
            db.delete(transactionDetail);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新
     * @param transactionDetail
     * @param updateColumnNames
     */
    public boolean update(TransactionDetail transactionDetail, String... updateColumnNames){
        try {
            db.update(transactionDetail, updateColumnNames);
            return true;
        } catch (DbException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 更新
     */
    public void update(TransactionDetail transactionDetail){
        try {
            KeyValue[] karr = {};
            List<KeyValue> keyValues = SqlInfoBuilder.entity2KeyValueList(db.getTable(TransactionDetail.class), transactionDetail);
            db.update(TransactionDetail.class, WhereBuilder.b("id", "=", transactionDetail.getId()), keyValues.toArray(karr));
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询所有
     */
    public List<TransactionDetail> getAll(){
        try {
            List<TransactionDetail> transactionDetailList = db.findAll(TransactionDetail.class);
            return transactionDetailList;
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据id查询
     * @param id
     */
    public TransactionDetail getTransactionDetailById(String id){
        try {
            return db.selector(TransactionDetail.class).where("id", "=", id).findFirst();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }



    /**
     * 根据指定列查询（等值查询）
     * @param columnName    列名（全大写）
     * @param value         查询value
     * @return
     */
    public List<TransactionDetail> getBy(String columnName, String value){
        try {
            return db.selector(TransactionDetail.class).where(columnName, "=", value).findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }
}
