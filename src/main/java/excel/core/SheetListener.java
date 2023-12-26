package excel.core;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.read.listener.ReadListener;
import log.AbstractLog;

/**
 * @author Yxm
 */
public class SheetListener implements ReadListener<Map<Integer, String>> {

    private final AbstractLog log;
    private final String excelName;
    private SheetData sheetData;
    private Consumer<SheetData> consumer;

    public SheetListener(Consumer<SheetData> consumer, AbstractLog log, String excelName) {
        this.consumer = consumer;
        this.log = log;
        this.excelName = excelName;
    }

    @Override
    public void invoke(Map<Integer, String> data, AnalysisContext context) {
        sheetData.addData(context.readSheetHolder().getRowIndex(), data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {

    }

    @Override
    public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
        if (sheetData != null) {
            consumer.accept(sheetData);
        } else {
            sheetData = new SheetData(log, excelName);
            sheetData.setSheetName(context.readSheetHolder().getSheetName());
            Map<Integer, String> head = new HashMap<>();
            for (Map.Entry<Integer, ReadCellData<?>> entry : headMap.entrySet()) {
                head.put(entry.getKey(), entry.getValue().getStringValue());
            }
            sheetData.addData(context.readSheetHolder().getRowIndex(), head);
        }
    }
}
