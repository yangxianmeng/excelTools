package main;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.read.metadata.holder.ReadRowHolder;
import com.alibaba.excel.read.metadata.holder.ReadSheetHolder;
import com.alibaba.excel.read.metadata.holder.xlsx.XlsxReadSheetHolder;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.metadata.WriteSheet;
import excel.ExcelUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class KeepTopNRowsExample {
    static String excelFilePath = "H:\\shendiao\\branchs\\CHWeChatTrunkSimplify20231127\\trunk\\public\\config\\fix";
    static String outputFilePath = "H:\\shendiao\\branchs\\CHWeChatTrunkSimplify20231127\\trunk\\public\\config\\fix\\f\\";

    static List<String> ignoreFileList = Arrays.asList("参数|Param");
    // 读取 Excel 文件
    public static void main(String[] args) {
        int topNRowsToKeep = 4; // 保留前 N 行
        ArrayList<File> fileList = ExcelUtils.getFileList(new File(excelFilePath));
        for (File file : fileList) {
            String fileName = file.getName();
            processExcel(file, outputFilePath + fileName, topNRowsToKeep);
        }
    }

    // 处理 Excel 文件
    private static void processExcel(File inputFileP, String outputFilePath, int topNRowsToKeep) {
        Map<Integer, List<Object>> dataBuffer = new HashMap<>();
        Map<Integer, String> sheetNameMap = new HashMap<>();
        DeleteRowsListener deleteRowsListener = new DeleteRowsListener(topNRowsToKeep, sheetNameMap, dataBuffer);
        try {
            EasyExcel.read(inputFileP, deleteRowsListener).doReadAll();
        }catch (Exception e) {
            e.printStackTrace();
        }

        writeExcel(outputFilePath, sheetNameMap, dataBuffer);
    }

    private static void writeExcel(String outputFilePath, Map<Integer, String> sheetNameMap, Map<Integer, List<Object>> dataBuffer) {
        ExcelWriter excelWriter = new ExcelWriterBuilder()
                .file(outputFilePath)
                .head(List.class) // 请替换为你的实际对象类型
                .build();

        for (Map.Entry<Integer, String> entry : sheetNameMap.entrySet()) {
            WriteSheet writeSheet = EasyExcel.writerSheet(entry.getKey(), entry.getValue()).build();
            List<Object> dataList = dataBuffer.get(entry.getKey());
            excelWriter.write(dataList, writeSheet);
        }

        excelWriter.finish();
    }


    // 监听器，用于处理每个 sheet 的数据
    private static class DeleteRowsListener extends AnalysisEventListener<Object> {
        private int topNRowsToKeep;
        private Map<Integer, String> sheetNameMap;
        private Map<Integer, List<Object>> dataBuffer;

        public DeleteRowsListener(int topNRowsToKeep, Map<Integer, String> sheetNameMap, Map<Integer, List<Object>> dataBuffer){
            this.topNRowsToKeep = topNRowsToKeep;
            this.sheetNameMap = sheetNameMap;
            this.dataBuffer = dataBuffer;
        }

        @Override
        public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
            ReadSheetHolder readSheet = context.readSheetHolder();
            int sheetIndex = readSheet.getSheetNo();
            String sheetName = readSheet.getSheetName();
            sheetNameMap.put(sheetIndex, sheetName);
            List<Object> objects = dataBuffer.computeIfAbsent(sheetIndex, k -> new ArrayList<>());
            XlsxReadSheetHolder xlsxReadSheetHolder = (XlsxReadSheetHolder) context.currentReadHolder();
            Integer columnIndex = xlsxReadSheetHolder.getColumnIndex();
            System.out.println(columnIndex + " ------------- " + sheetName);
            LinkedHashMap<Integer, String> headMap2 = new LinkedHashMap<>();
            for (int i = 0; i <= columnIndex; i++) {
                ReadCellData<?> readCellData = headMap.get(i);
                if (readCellData == null) {
                    headMap2.put(i, "");
                } else {
                    headMap2.put(i, readCellData.getStringValue());
                }
            }
            objects.add(headMap2);
        }

        @Override
        public void invoke(Object data, AnalysisContext context) {
            ReadRowHolder readRowHolder = context.readRowHolder();
            int rowIndex = readRowHolder.getRowIndex();
            ReadSheetHolder readSheet = context.readSheetHolder();
            int sheetIndex = readSheet.getSheetNo();
            List<Object> objects = dataBuffer.computeIfAbsent(sheetIndex, k -> new ArrayList<>());
            String sheetName = readSheet.getSheetName();
            // 如果当前行在前 N 行内，则保存数据
            if (!sheetName.contains("|") || ignoreFileList.contains(sheetName)) {
                objects.add(data);
                return;
            }
            if (rowIndex < topNRowsToKeep) {
                objects.add(data);
            }
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {
            System.out.println(context.readSheetHolder().getSheetName() + " ----- 处理完毕");
        }
    }
}
