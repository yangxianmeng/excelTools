package excel.gen.json;

import java.io.File;
import java.util.ArrayList;
import java.util.Properties;

import excel.ExcelUtils;
import log.AbstractLog;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Yxm
 **/
public class Conf {
    /** 配置文件的路径 */
    private String configPath;
    private File[] files;

    boolean load(Properties properties, AbstractLog log) {
        configPath = properties.getProperty("excel.config.path");
        // 配置文件的路径没有配置
        if (StringUtils.isEmpty(configPath)) {
            log.error("配置文件的路径没有设置");
            return false;
        }

        // 配置文件的路径配置了，但是路径不存在或者路径存在但是不是个目录
        File configDir = new File(configPath);
        if (!configDir.exists() || !configDir.isDirectory()) {
            log.error("配置文件的路径设置错误:" + configPath);
            return false;
        }
        // 过滤出excel文件
        ArrayList<File> fileList = ExcelUtils.getFileList(configDir);
        files = fileList.toArray(new File[fileList.size()]);
        return true;
    }

    public String getConfigPath() {
        return configPath;
    }

    public File[] getFiles() {
        return files;
    }
}
