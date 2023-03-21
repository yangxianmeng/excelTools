package excel.gen.json;

import java.util.Properties;

import excel.AutoBase;
import excel.core.SheetData;
import log.AbstractLog;

/**
 * 自动生成Json
 * @author Yxm
 **/
public class JsonAuto extends AutoBase {


    public JsonAuto(AbstractLog log, Properties properties) {
        super(log, properties);
    }


    @Override
    protected void initProperties() {

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
}
