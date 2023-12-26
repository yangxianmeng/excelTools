package main;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.util.WorkBookUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Yxm
 **/
public class TestMain {
    /**haha*/
    public void listFill() {
        String fileName = "E:\\" + "listFill01.xlsx";
        XSSFWorkbook xssfWorkbook = null;
        try {
            xssfWorkbook = new XSSFWorkbook(new FileInputStream(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //获取需要删除的Sheet
        Sheet sheet = xssfWorkbook.getSheetAt(0);
        //删除第100列
        deleteColumn(sheet, 100);
        try (OutputStream fileOut = new FileOutputStream(fileName)) {
            xssfWorkbook.write(fileOut);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 删除列
     * @param sheet
     * @param columnToDelete
     */
    public static void deleteColumn(Sheet sheet, int columnToDelete) {
        System.out.println(sheet.getLastRowNum());
        for (int r = 0; r <= sheet.getLastRowNum(); r++) {
            Row row = sheet.getRow(r);
            for (int c = columnToDelete; c <= row.getLastCellNum(); c++) {
                Cell cOld = row.getCell(c);
                if (cOld != null) {
                    row.removeCell(cOld);
                }
                Cell cNext = row.getCell(c + 1);
                if (cNext != null) {
                    Cell cNew = row.createCell(c, cNext.getCellType());
                    cloneCell(cNew, cNext);
                    if (r == 0) {
                        sheet.setColumnWidth(c, sheet.getColumnWidth(c + 1));
                    }
                }
            }
        }
    }

    /**
     * 右边列左移,样式值设置
     * @param cNew
     * @param cOld
     */
    private static void cloneCell(Cell cNew, Cell cOld) {
        cNew.setCellComment(cOld.getCellComment());
        cNew.setCellStyle(cOld.getCellStyle());

        if (CellType.BOOLEAN == cNew.getCellType()) {
            cNew.setCellValue(cOld.getBooleanCellValue());
        } else if (CellType.NUMERIC == cNew.getCellType()) {
            cNew.setCellValue(cOld.getNumericCellValue());
        } else if (CellType.STRING == cNew.getCellType()) {
            cNew.setCellValue(cOld.getStringCellValue());
        } else if (CellType.ERROR == cNew.getCellType()) {
            cNew.setCellValue(cOld.getErrorCellValue());
        } else if (CellType.FORMULA == cNew.getCellType()) {
            cNew.setCellValue(cOld.getCellFormula());
        }
    }

    public static void main(String[] args) throws NoSuchFieldException, FileNotFoundException {
        String sheetname = "chengji|aaa";
    }
}
