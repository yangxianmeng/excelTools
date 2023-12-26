package main;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSONObject;
import excel.core.SheetData;
import excel.core.SheetListener;
import log.CacheLog;

import java.util.List;
import java.util.Map;


public class DelTest {

    public static void main(String[] args) {
        System.setProperty("logFileName", "del");
        String path = "D:\\NewXiaKe.xlsx";
        String path2 = "D:\\NNewXiaKe.xlsx";
        EasyExcel.read(path, new SheetListener(v-> {
            SheetData sheetData =  v;
            List<Map<Integer, String>> testList = v.getTestList();
            System.out.println(sheetData.getSheetName());
            EasyExcel.write(path2, SheetData.class).sheet(sheetData.getSheetName()).doWrite(testList);

        }, new CacheLog(), "NewXiaKe.xlsx")).doReadAll();
    }

}