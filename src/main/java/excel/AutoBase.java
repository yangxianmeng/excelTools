package excel;

import java.io.File;
import java.net.URL;
import java.util.Locale;
import java.util.Properties;

import excel.core.IConfig;
import excel.core.SheetData;
import excel.gen.json.Conf;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import log.AbstractLog;

/**
 * 自动生成基类
 * @author Yxm
 */
public abstract class AutoBase implements IConfig {
    /** 页签数据合并标识  */
    public static final String COMBINE_IDENTIFY = "\\$";
    /** 页签数据合并标识  */
    public static final String CONTAIN_COMBINE_IDENTIFY = "$";
    /** 日志对象 */
    protected AbstractLog log;
    /** 配置文件 */
    protected Properties properties;
    /** 模板文件 */
    protected Template template;
    /** 配置文件 */
    private Conf conf;

    public AutoBase(AbstractLog log, Properties properties) {
        this.log = log;
        this.properties = properties;
        this.conf = new Conf();
    }

    public void startup() {
        log.info("=================================");
        String name = this.getClass().getName();
        log.info(String.format("-----------> 开始生成：文件【%s】", name));
        long l1 = System.currentTimeMillis();
        if (!initProperties()) {
            log.info(String.format("文件【%s】 配置加载失败!", name));
            return;
        }
        if (loadConfig()) {
            gen();
            log.info(String.format("-----------> 文件【%s】完成。 \n" +
                    "耗时：【%d】秒", name, (System.currentTimeMillis() - l1) / 1000));
            log.info("=================================");
        } else {
            log.error("================= 生成失败! 加载配置出错。 ================");
        }
    }

    protected abstract boolean initProperties();

    /**
     * 加载配置文件
     * @return
     */
    protected boolean loadConfig() {
        // 设置模板
        String templateName = getTemplateName();
        URL templateURL = Thread.currentThread().getContextClassLoader().getResource(templateName);
        if (templateURL == null) {
            return false;
        }
        File templateFile = new File(templateURL.getPath());
        try {
            Configuration cfg = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
            cfg.setDirectoryForTemplateLoading(templateFile.getParentFile());
            cfg.setObjectWrapper(new DefaultObjectWrapper(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS));
            cfg.setEncoding(Locale.getDefault(), "UTF-8");
            template = cfg.getTemplate(templateFile.getName());
        } catch (Exception e) {
            log.error(String.format("加载模板【%s】异常! message=%s", templateName, e.getMessage()));
            return false;
        }
        return true;
    }

    /**
     * 开始生成
     */
    protected void gen() {

    }

    public AbstractLog getLog() {
        return log;
    }

    public Properties getProperties() {
        return properties;
    }

    /**
     * 一个页签所有数据处理回调
     * @param sheetData
     * @throws Exception
     */
    protected abstract void genEnd(SheetData sheetData);
}
