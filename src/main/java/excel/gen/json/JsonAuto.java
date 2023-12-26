package excel.gen.json;

import java.util.Properties;

import excel.AutoBase;
import excel.ExcelUtils;
import excel.core.SheetData;
import log.AbstractLog;
import org.apache.commons.lang3.StringUtils;

/**
 * 自动生成Json
 * @author Yxm
 **/
public class JsonAuto extends AutoBase {
    /** 生成JSON文件的临时路径 */
    private String exportJsonTemporaryPath;
    /** 生成JSON文件的最终路径 */
    private String exportJsonPath;


    public JsonAuto(AbstractLog log, Properties properties) {
        super(log, properties);
    }

    @Override
    protected void gen() {

    }
    @Override
    protected boolean initProperties() {
        exportJsonTemporaryPath = properties.getProperty("excel.export.json.temporary.path");
        // JSON文件的临时路径没有设置
        if (StringUtils.isEmpty(exportJsonTemporaryPath)) {
            getLog().error("JSON文件的临时路径没有设置");
            return false;
        }

        // JSON文件的临时路径创建失败
        if (!ExcelUtils.mkdirs(exportJsonTemporaryPath, getLog())) {
            return false;
        }
        exportJsonPath = getProperties().getProperty("excel.export.json.path");
        // JSON文件的最终导出路径没有设置
        if (StringUtils.isEmpty(exportJsonPath)) {
            getLog().error("JSON文件的最终路径没有设置");
            return false;
        }

        // JSON文件的最终导出路径创建失败
        if (!ExcelUtils.mkdirs(exportJsonPath, getLog())) {
            return false;
        }
        return true;
    }

    @Override
    protected void genEnd(SheetData sheetData) {

    }

    @Override
    public String getTemplateName() {
        return null;
    }

    @Override
    public String getTargetName() {
        return null;
    }

    public String extractSheetName(String fileName) {
        if (fileName.startsWith("Conf") && fileName.endsWith(".json") && fileName.length() > "Conf.json".length()) {
            return fileName.substring(4, fileName.length() - 5);
        }
        return null;
    }
}
