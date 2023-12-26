package main;

import javax.swing.*;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import excel.gui.GenGUI;

/**
 * @author Yxm
 */
public class ExcelMain {

    static final String path = "templeFile/";
    public static void main(String[] args) {
        String configFileName = path + "tools-core.properties";
        if (args.length >= 1) {
            configFileName = args[0];
        }
        URL configURL = Thread.currentThread().getContextClassLoader().getResource(configFileName);
        if (configURL == null) {
            System.err.println("配置文件不存在：" + configFileName);
            System.exit(-1);
        }

        Properties properties = new Properties();
        try (InputStreamReader isr = new InputStreamReader(configURL.openStream(), StandardCharsets.UTF_8)) {
            properties.load(isr);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("载入配置文件出错：" + configFileName);
            System.exit(-2);
        }

        SwingUtilities.invokeLater(() -> new GenGUI(properties).createAndShowGUI());
    }
}
