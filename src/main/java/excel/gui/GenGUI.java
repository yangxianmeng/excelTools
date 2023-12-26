package excel.gui;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Properties;

import excel.AutoBase;
import log.AbstractLog;
import log.CacheLog;
import log.GuiLog;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Yxm
 **/
public class GenGUI {
    /** 日志对象 */
    private AbstractLog log;
    /** 配置文件 */
    private Properties properties;

    /** 主窗口 */
    private JFrame frame;
    /** 工具栏窗口 */
    private JToolBar toolBar;
    /** 正常日志窗口 */
    private JTextArea taNormal;
    /** 错误日志窗口 */
    private JTextArea taError;

    public GenGUI(Properties properties) {
        this.properties = properties;
    }

    /**
     * 创建并显示界面
     */
    public void createAndShowGUI() {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception e) {
            System.err.println("设置风格失败");
        }

        // 创建及设置窗口
        frame = new JFrame("服务器配置代码生成工具 V2.1.0--有新需求请RTX联系黄志坤");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(867, 649);

        // 工具栏
        toolBar = new JToolBar("测试工具栏");
        toolBar.setFloatable(false);
        toolBar.setRollover(true);

        // 正常日志
        taNormal = new JTextArea(
                "Q & A\r\n" +
                        "A: False\r\n" +
                        "Q: 布尔数值类型默认是什么？ \r\n" +
                        "A: False\r\n" +
                        "Q: String数值类型默认是什么？ \r\n" +
                        "A: 空字符串\"\"\r\n" +
                        "Q: 布尔数值可以填1或0这样的数值吗？ \r\n" +
                        "A: 可以,0代表False,1代表True\r\n" +
                        "Q: Excel文件Sheet名称有什么规则吗？ \r\n" +
                        "A: 规则为\"Test|测试\",不符合的将不会生成,请将英文的首字母大写!\r\n" +
                        "Q: 我想加点注释在配置旁边可以吗？ \r\n" +
                        "A: 可以,只要不填头文件的配置,都不会生成\r\n" +
                        "Q: 我不看这个说明,可以吗？ \r\n" +
                        "A: 不可以!不过不看文档,这很策划!\r\n" +
                        "");
        taNormal.setBorder(new TitledBorder("处理日志："));
        taNormal.setFont(new Font("Microsoft YaHei", Font.PLAIN, 15));
        JScrollPane spNormal = new JScrollPane(taNormal);
        spNormal.setPreferredSize(new Dimension(867, 420));

        // 错误日志
        taError = new JTextArea();
        taError.setBorder(new TitledBorder(null, "错误日志：", TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION, new Font("Microsoft YaHei", Font.PLAIN, 12)));
        taError.setFont(new Font(Font.MONOSPACED, Font.BOLD, 16));
        taError.setForeground(new Color(0xFF6B68));
        JScrollPane spError = new JScrollPane(taError);

        // 日志输出
        log = new GuiLog(taNormal, taError);
        initToolBar();

        // 错误日志框右键菜单
        taError.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    JPopupMenu contextMenu = new JPopupMenu();
                    for (String errFile : new HashSet<>(log.getErrorFiles())) {
                        JMenuItem menuItem = new JMenuItem("打开 " + errFile);
                        menuItem.addActionListener(evt -> openFile(errFile, log));
                        contextMenu.add(menuItem);
                    }

                    contextMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(toolBar, BorderLayout.PAGE_START);
        panel.add(new JSplitPane(JSplitPane.VERTICAL_SPLIT, spNormal, spError), BorderLayout.CENTER);
        frame.add(panel);
        frame.setVisible(true);

        // 立即开始生成
        boolean autoGen = Boolean.parseBoolean(properties.getProperty("tools.gui.auto", "true"));
        if (autoGen) {
            ((JButton)toolBar.getComponentAtIndex(0)).doClick();
        }
    }

    private void initToolBar() {
        // 一键全生成按钮
        toolBar.add(createToolButton("全部生成", () -> startGenAll(log, properties, this::genEndCallBack)));
        toolBar.addSeparator();

        // 单个工具的生成按钮
        java.util.List<AutoBase> serialTools = getTools(log, properties, false);
        java.util.List<AutoBase> parallelTools = getTools(log, properties, true);
        // 只有一个工具，忽略了，因为一键全生成按钮功能是一样的了
        if (serialTools.size() + parallelTools.size() <= 1) {
            return;
        }

        for (AutoBase tool : serialTools) {
            toolBar.add(createToolButton(getToolName(tool), () -> startGenOne(tool, this::genEndCallBack)));
        }
        for (AutoBase tool : parallelTools) {
            toolBar.add(createToolButton(getToolName(tool), () -> startGenOne(tool, this::genEndCallBack)));
        }
    }

    private JComponent createToolButton(String text, Runnable handler) {
        // 生成按钮
        JButton btGen = new JButton(text);
        btGen.setFont(new Font("Microsoft YaHei", Font.BOLD, 18));
        btGen.setBorder(new LineBorder(new Color(0xACABAB), 1, true));
        btGen.setPreferredSize(new Dimension(90, 35));

        // 按钮事件
        btGen.addActionListener(e -> {
            taNormal.requestFocus();
            taError.setText("");
            Arrays.stream(toolBar.getComponents()).forEach(b -> b.setEnabled(false));
            new Thread(handler).start();
        });
        return btGen;
    }

    private void genEndCallBack() {
        SwingUtilities.invokeLater(() -> {
            Arrays.stream(toolBar.getComponents()).forEach(b -> b.setEnabled(true));
            frame.setVisible(true);
            frame.requestFocus();
        });
    }

    static void startGenAll(AbstractLog log, Properties properties, Runnable endCallBack) {
        log.clean();
        long time = System.currentTimeMillis();

        java.util.List<AutoBase> serialTools = getTools(log, properties, false);
        serialTools.stream().filter(Objects::nonNull).forEach(AutoBase::startup);
        java.util.List<AutoBase> parallelTools = getTools(log, properties, true);
        parallelTools.parallelStream().filter(Objects::nonNull).forEach(t -> {
            t.startup();
            log.copy((CacheLog)t.getLog());
        });

        log.info("======================================================================================");
        if (log.isFailed()) {
            log.info("生成失败，有错误产生，详细请看下面的错误窗口！");
        } else {
            log.info(String.format("全部生成完毕！恭喜你！没有错误产生，可以关闭窗口了！%d个工具，总耗时%d毫秒",
                    serialTools.size() + parallelTools.size(), System.currentTimeMillis() - time));
        }
        log.info("======================================================================================");
        log.info("=================================请关闭该窗口 继续配表检查=============================");
        endCallBack.run();
    }

    private void startGenOne(AutoBase tool, Runnable endCallBack) {
        log.clean();
        long time = System.currentTimeMillis();
        // 每次生成，new一个新的工具实例，因为啊有的实例有状态未清除，不支持多次调用
        AutoBase newTool = getGenBase(log, properties, tool.getClass().getName());
        assert newTool != null;
        newTool.startup();

        log.info("======================================================================================");
        if (log.isFailed()) {
            log.info("生成失败，有错误产生，详细请看下面的错误窗口！");
        } else {
            log.info(String.format("生成完毕！恭喜你！没有错误产生！总耗时%d毫秒", System.currentTimeMillis() - time));
        }
        log.info("======================================================================================");
        endCallBack.run();
    }

    /**
     * 获取工具的名字<br>
     *     org.jow.core.gen.GenClean返回Clea<br>
     *     org.jow.tools.excelToJson.Gen返回Exec
     * @param tool 工具对象
     * @return 工具名
     */
    private static String getToolName(AutoBase tool) {
        String className = tool.getClass().getName();
        String toolName = className;
        do {
            int indexOf = className.lastIndexOf('.');
            if (indexOf < 0) {
                break;
            }

            // 如果类名不是Gen，tool名字就是类名
            toolName = className.substring(indexOf + 1);
            if (!toolName.equalsIgnoreCase("Gen")) {
                break;
            }

            className = className.substring(0, indexOf);
            toolName = className;
        } while (true);

        if (toolName.startsWith("Gen")) {
            toolName = toolName.substring("Gen".length());
        }

        return StringUtils.substring(StringUtils.capitalize(toolName), 0, 5);
    }

    /**
     * 获取指定前缀的所有工具列表
     * @param log 日志对象
     * @param properties 配置对象
     * @param parallel 是获取tools.gen还是获取tools.gen.parallel工具列表
     * @return 工具列表
     */
    private static java.util.List<AutoBase> getTools(AbstractLog log, Properties properties, boolean parallel) {
        String prefix = parallel ? "tools.gen.parallel" : "tools.gen";
        java.util.List<AutoBase> tools = new ArrayList<>();
        for (int i = 1; i < 100; ++i) {
            String toolName = properties.getProperty(prefix + i);
            if (toolName != null) {
                AbstractLog realLog = parallel ? new CacheLog() : log;
                tools.add(getGenBase(realLog, properties, toolName));
            } else {
                break;
            }
        }
        return tools;
    }

    private static AutoBase getGenBase(AbstractLog log, Properties properties, String className) {
        try {
            Class<?> clazz = Class.forName(className);
            Constructor<?> constructor = clazz.getDeclaredConstructor(AbstractLog.class, Properties.class);
            constructor.setAccessible(true);
            return (AutoBase) constructor.newInstance(log, properties);
        } catch (Exception e) {
            log.error(String.format("%s生成出错：%s", className, e.getMessage()));
            return null;
        }
    }

    private static void openFile(String fileName, AbstractLog log) {
        Desktop desk = Desktop.getDesktop();
        try {
            File file = new File(fileName);
            if (file.exists()) {
                desk.open(file);
            }
        } catch (Exception e) {
            log.error("打开文件错误：" + e.getMessage());
        }
    }

}
