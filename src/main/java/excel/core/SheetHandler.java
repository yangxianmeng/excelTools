package excel.core;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler.SheetContentsHandler;
import org.apache.poi.xssf.usermodel.XSSFComment;

/**
 * 读一行数据
 * @author Yxm
 */
public class SheetHandler implements SheetContentsHandler {

    /** 当前行内容 */
    private Map<Integer, String> currentDataMap = new HashMap<>();
    /** 当前行号，从0开始 */
    private int currentRow = -1;
    /** 当前列号，从0开始 */
    private int currentCol = -1;

    private final SheetData sheetData;

    public SheetHandler(SheetData sheetData) {
        this.sheetData = sheetData;
    }

    @Override
    public void startRow(int rowNum) {
        currentRow = rowNum;
        currentCol = -1;
        currentDataMap.clear();
    }

    @Override
    public void endRow(int rowNum) {
        if (sheetData != null) {
            sheetData.addData(rowNum, currentDataMap);
        }
    }

    @Override
    public void cell(String cellReference, String formattedValue, XSSFComment comment) {
        /*if (cellReference == null) {
            cellReference = new CellAddress(currentRow, currentCol + 1).formatAsString();
        }
        // 计算列号
        int thisCol = (new CellReference(cellReference)).getCol();
        // 填充中间的空列
        int missedCols = thisCol - currentCol - 1;
        for (int i = 0; i < missedCols; i++) {
            currentDataMap.put(null);
        }*/

        //一个单元格处理
        final int column = comment.getColumn();
        formattedValue = StringUtils.trim(formattedValue);
        currentDataMap.put(column, formattedValue);
    }
}
