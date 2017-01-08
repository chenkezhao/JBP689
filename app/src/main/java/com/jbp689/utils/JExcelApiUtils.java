package com.jbp689.utils;

import android.widget.Toast;

import com.jbp689.JBPApplication;
import com.jbp689.entity.KLine;
import com.jbp689.entity.TransactionDetail;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import jxl.Cell;
import jxl.CellType;
import jxl.Sheet;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

/**
 * excel文档操作 Created by Administrator on 2017/1/8.
 */

public class JExcelApiUtils {

    /**
     * 根据股票代码获取从新浪下载的历史明细excel读取数据
     * @param file 下载的文件
     * @param kLine new KLine(isRed)
     * @param td TransactionDetail对象
     * @return
     */
	public static KLine readSinaTradehistoryXls(File file,KLine kLine,TransactionDetail td) {
        jxl.Workbook readwb = null;
		try {
			// 构建Workbook对象, 只读Workbook对象
			// 直接从本地文件创建Workbook
//			InputStream instream = new FileInputStream(file);
			readwb = Workbook.getWorkbook(file);
			// Sheet的下标是从0开始
			// 获取第一张Sheet表
			Sheet readsheet = readwb.getSheet(0);
			// 获取Sheet表中所包含的总列数
			int rsColumns = readsheet.getColumns();
			// 获取Sheet表中所包含的总行数
			int rsRows = readsheet.getRows();
			// 获取指定单元格的对象引用
            long totalVolume = 0; // 总成交量
            long upVolume = 0; // K线上部分
            long middleVolume = 0; // K线中间部分
            long downVolume = 0; // K线下部分
			for (int i = 0; i < rsRows; i++) {
				for (int j = 0; j < rsColumns; j++) {
                    // 成交价（元）
                    Cell cellPrice = readsheet.getCell(1, 1);//列，行
                    double price = Double.parseDouble(cellPrice.getContents());
                    // 成交量（手）
                    Cell cellVolume = readsheet.getCell(3, 1);//列，行
                    long volume = Long.parseLong(cellVolume.getContents());
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
			}
            kLine.setUpVolume(upVolume);
            kLine.setMiddleVolume(middleVolume);
            kLine.setDownVolume(downVolume);
            kLine.setTotalVolume(totalVolume);
            kLine.setName(td.getName());
            kLine.setDate(td.getDate());

            //删除下载的文件
            if (file.isFile() && file.exists()) {
                if(!file.delete()){
                    Toast.makeText(JBPApplication.getInstance(), "excel文件删除失败！", Toast.LENGTH_LONG).show();
                }
            }
            return kLine;
		} catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(JBPApplication.getInstance(), "异常："+ex.toString(), Toast.LENGTH_LONG).show();
        }finally {
            if(readwb!=null){
                readwb.close();
            }
        }
        return new KLine();
    }
    public static void write(){
        jxl.Workbook readwb = null;
        try {
            //利用已经创建的Excel工作薄,创建新的可写入的Excel工作薄
            jxl.write.WritableWorkbook wwb = Workbook.createWorkbook(new File("F:/红楼人物1.xls"), readwb);
            //读取第一张工作表
            jxl.write.WritableSheet ws = wwb.getSheet(0);
            //获得第一个单元格对象
            jxl.write.WritableCell wc = ws.getWritableCell(0, 0);
            //判断单元格的类型, 做出相应的转化
            if (wc.getType() == CellType.LABEL) {
                Label l = (Label) wc;
                l.setString("新姓名");
            }
            //写入Excel对象
            wwb.write();
            wwb.close();
        }catch (Exception ex){
            ex.printStackTrace();
        }finally {
            if(readwb!=null){
                readwb.close();
            }
        }
    }

}
