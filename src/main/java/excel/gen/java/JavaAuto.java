package excel.gen.java;

import java.util.Properties;

import excel.AutoBase;
import excel.ExcelPoi;
import excel.core.SheetData;
import log.AbstractLog;

/**
 * 自动生成Java
 * @author Yxm
 **/
public class JavaAuto extends AutoBase {

    private String templateName;

    public JavaAuto(AbstractLog log, Properties properties) {
        super(log, properties);
    }

    @Override
    protected boolean initProperties() {
        templateName = properties.getProperty("java_template_name");
        return true;
    }

    @Override
    protected void gen() {
        super.gen();
        new ExcelPoi(this, null).run();
    }

    @Override
    protected void genEnd(SheetData sheetData) {

    }
    @Override
    public String getTemplateName() {
        return properties.getProperty("java_template_name");
    }

    @Override
    public String getTargetName() {
        return null;
    }
}
