package com.jbp689.db.dao;

import com.jbp689.entity.KLine;

import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.SqlInfoBuilder;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.List;

/**
 * 访问T_KLINE
 * Created by Administrator on 2017/1/10.
 */

public class KLineDao  extends BaseDao{
    private static  KLineDao mKLineDao = new KLineDao();
    private KLineDao() {
        super();
    }
    public static KLineDao getInstance(){
        return mKLineDao;
    }
    /**
     * 插入
     * @param kLine
     */
    public void insert(KLine kLine) {
        try {
            db.save(kLine);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 批量插入
     * @param kLineList
     */
    public void insert(List<KLine> kLineList) {
        try {
            db.save(kLineList);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 清空表
     */
    public void clearTable(){

        try {
            db.delete(KLine.class, WhereBuilder.b("id", "!=", "0"));
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
            db.delete(KLine.class, WhereBuilder.b("id", "=", id));
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除
     */
    public void delete(KLine kLine){
        try {
            db.delete(kLine);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新
     * @param kLine
     * @param updateColumnNames
     */
    public boolean update(KLine kLine, String... updateColumnNames){
        try {
            db.update(kLine, updateColumnNames);
            return true;
        } catch (DbException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 更新
     */
    public void update(KLine kLine){
        try {
            KeyValue[] karr = {};
            List<KeyValue> keyValues = SqlInfoBuilder.entity2KeyValueList(db.getTable(KLine.class), kLine);
            db.update(KLine.class, WhereBuilder.b("id", "=", kLine.getId()), keyValues.toArray(karr));
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询所有
     */
    public List<KLine> getAll(){
        try {
            List<KLine> kLineList = db.findAll(KLine.class);
            return kLineList;
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据id查询
     * @param id
     */
    public KLine getKLineById(String id){
        try {
            return db.selector(KLine.class).where("id", "=", id).findFirst();
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
    public List<KLine> getBy(String columnName, String value){
        try {
            return db.selector(KLine.class).where(columnName, "=", value).findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据股票代码和日期判断是否存在
     * @param code
     * @param date
     */
    public KLine queryKLineIsExist(String code, String date) {
        try {
            return db.selector(KLine.class).where("CODE","=",code).and("DATE","=",date).findFirst();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String[] getAllCode(){
        try {
            List<DbModel> list = db.selector(KLine.class).select("distinct code").findAll();
            String codes[]= new String[list.size()];
            int len = list.size();
            for(int i=0;i<len;i++){
                codes[i] = list.get(i).getString("CODE");
            }
            return codes;
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }
}
