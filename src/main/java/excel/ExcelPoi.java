package excel;

import java.io.File;
import java.io.InputStream;

import com.alibaba.excel.EasyExcel;
import excel.core.SheetData;
import excel.core.SheetHandler;
import excel.core.SheetListener;
import log.AbstractLog;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.util.XMLHelper;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFReader.SheetIterator;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.model.StylesTable;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

/**
 * @author Yxm
 */
public class ExcelPoi implements Runnable {

    protected AbstractLog log;
    /** 需要处理的excel路径 */
    protected File excelFile;
    /** excel名字 */
    protected String excelName;
    /** 生成工具实例 */
    private AutoBase auto;
    /** 是否使用EasyExcel */
    private boolean isEasyAuto;

    public ExcelPoi(AutoBase auto, File file) {
        this.log = auto.log;
        this.excelFile = file;
        this.excelName = file.getName();
        this.auto = auto;
    }

   @Override
    public void run() {
       log.info(String.format("开始处理：【%s】 isEasyAuto:【%s】", excelName, isEasyAuto));
       final long l = System.currentTimeMillis();
       if (isEasyAuto) {
           autoMakeEasyExcel();
       } else {
           autoMakeOpc();
       }
       log.info(String.format("处理完成 耗时：【%d】", System.currentTimeMillis() - l));
   }

    private void autoMakeOpc() {
        try (OPCPackage open = OPCPackage.open(excelFile, PackageAccess.READ)) {
            ReadOnlySharedStringsTable sharedStringsTable = new ReadOnlySharedStringsTable(open);
            XSSFReader xssfReader = new XSSFReader(open);
            StylesTable stylesTable = xssfReader.getStylesTable();
            //获得所有的子页签
            SheetIterator sheetsData = (SheetIterator) xssfReader.getSheetsData();
            while (sheetsData.hasNext()) {
                SheetData sheetData = new SheetData(log, excelName);
                try (final InputStream stream = sheetsData.next()) {
                    //读取sheet数据
                    DataFormatter formatter = new DataFormatter();
                    InputSource source = new InputSource(stream);
                    try {
                        XMLReader sheetParser = XMLHelper.newXMLReader();
                        ContentHandler handler = new XSSFSheetXMLHandler(stylesTable, null, sharedStringsTable,
                                new SheetHandler(sheetData), formatter, false);
                        sheetParser.setContentHandler(handler);
                        sheetParser.parse(source);
                    } catch (Exception e) {

                    }
                }
                auto.genEnd(sheetData);
            }
        } catch (Exception e) {

        }
    }
    public void autoMakeEasyExcel() {
        EasyExcel.read(excelFile, new SheetListener(auto::genEnd, log, excelName)).doReadAll();
    }
}
