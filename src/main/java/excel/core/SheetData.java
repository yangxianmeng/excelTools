package excel.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.JSONObject;
import log.AbstractLog;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.analysis.function.Log;

/**
 * 一个页签数据
 * @author Yxm
 */
public class SheetData {

    private AbstractLog log;

    private String excelName;
    /** 页签名字 */
    private String sheetName;
    /** 每一行数据 */
    private List<JSONObject> dataList = new ArrayList<>();
    private List<Map<Integer, String>> testList = new ArrayList<>();
    /** 第一行头文件 */
    private Map<Integer, ColumnInfo> columnInfos = new LinkedHashMap<>();
    /** 是否异常 */
    private boolean isException;
    /** 是否忽略 */
    private boolean isIgnore;

    /** 当前处理的行号 */
    private int currentRow;
    /**字段集合，用来排重用的，忽略大小写*/
    private Set<String> fieldNames = new HashSet<>();
    /** sn集合，用来排重用的 */
    private Set<Object> sns = new HashSet<>();
    /** sn的数据类型 */
    private String snType;


    public SheetData(AbstractLog log, String excelName) {
        this.log = log;
        this.excelName = excelName;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    /**
     * 增加一行数据
     * @param rowNum
     * @param data
     */
    public void addData(int rowNum, Map<Integer, String> data) {
        try {
            if (rowNum != currentRow + 1) {
                throw new Exception(String.format("sheet:%s, 行:%d, 是空行", this.sheetName, currentRow + 2));
            }
            currentRow = rowNum;
            switch (rowNum) {
                case 0:
                    onReadHeadCS(data);
                    break;
                case 1:
                    onReadHeadType(data);
                    break;
                case 2:
                    onReadHeadName(data);
                    break;
                case 3:
                    onReadHeadNote(data);
                    break;
                default:
                    onReadData(data);
            }
        } catch (Exception e) {
            getLog().error("Error:处理<" + getSheetName() + ">时异常，原因: " + e.getMessage() + "    ->右键打开错误文件");
            getLog().addErrorFile(excelName);
            isException = true;
        }
        testList.add(data);
    }
    public AbstractLog getLog() {
        return log;
    }

    /**
     * 读取CS头信息，包含S的列是需要处理的，包含I的列表标识需要建索引
     * @param rowContents
     */
    private void onReadHeadCS(Map<Integer, String> rowContents) {
        for (Map.Entry<Integer, String> entry : rowContents.entrySet()) {
            String content = entry.getValue();
            if (StringUtils.containsAny(content, 'S', 's')) {
                ColumnInfo columnInfo = new ColumnInfo();
                columnInfo.setIndex(StringUtils.containsAny(content, 'I', 'i'));
                columnInfos.put(entry.getKey(), columnInfo);
            }
        }
        // 没有一列是S，忽略该表的生成
        if (columnInfos.isEmpty()) {
            isIgnore = true;
        }
    }

    private void onReadHeadType(Map<Integer, String> rowContents) throws Exception {
        for (Map.Entry<Integer, String> entry : rowContents.entrySet()) {
            int col = entry.getKey();

            String content = entry.getValue();
            if (StringUtils.isBlank(content)) {
                throw new Exception(String.format("sheet:%s, 行:2, 列:%d, 数据类型为空,请检查", this.sheetName, col + 1));
            }
            EDataType dataType = EDataType.getByText(content);
            if (dataType == null) {
                throw new Exception(String.format("sheet:%s, 行:2, 列:%d, 未知数据类型:%s,请检查", this.sheetName, col + 1, content));
            }
            ColumnInfo columnInfo = columnInfos.get(col);
            if (columnInfo != null) {
                columnInfo.setType(dataType);
            } else {
                getLog().error("Error:处理<" + getSheetName() + ">时异常，原因: 没有找到 ColumnInfo。 content = " + rowContents);
            }
        }
    }

    private void onReadHeadName(Map<Integer, String> rowContents) throws Exception {
        for (Map.Entry<Integer, String> entry : rowContents.entrySet()) {
            int col = entry.getKey();
            String content = entry.getValue();
            if (StringUtils.isBlank(content)) {
                throw new Exception(String.format("sheet:%s, 行:3, 列:%d, 字段名为空,请检查", this.sheetName, col + 1));
            }

            if (fieldNames.contains(content.toLowerCase())) {
                throw new Exception(String.format("sheet:%s, 行:3, 列:%d, 配置中存在重复字段:%s", this.sheetName, col + 1, content));
            } else {
                fieldNames.add(content.toLowerCase());
            }
            ColumnInfo columnInfo = columnInfos.get(col);
            if (columnInfo != null) {
                columnInfo.setName(content);
            } else {
                getLog().error("Error:处理<" + getSheetName() + ">时异常，原因: 没有找到 ColumnInfo。 content = " + rowContents);
            }
        }

        // 第一个字段必须是sn，sn名称忽略大小写，会统一转成小写，为了兼容老神雕
        ColumnInfo columnInfo = columnInfos.get(0);
        if (columnInfo == null) {
            throw new Exception(String.format("sheet:%s, 行:3, 列:1, 后面某列是s，但是第一列sn却不是s", this.sheetName));
        } else if (!columnInfo.getName().equalsIgnoreCase("sn")) {
            throw new Exception(String.format("sheet:%s, 行:3, 列:1, 第一个字段必须是sn", this.sheetName));
        } else {
            columnInfo.setName(columnInfo.getName().toLowerCase());
        }

        // sn只能是int、long或者String
        switch (columnInfo.getType()) {
            case INT:
                snType = "Integer";
                break;
            case LONG:
                snType = "Long";
                break;
            case STRING:
                snType = "String";
                break;
            default:
                throw new Exception(String.format("sheet:%s, sn只能是int、long或者String类型", this.sheetName));
        }
    }


    /**
     * 读取字段注释
     * @param rowContents
     */
    private void onReadHeadNote(Map<Integer, String> rowContents) {
        for (Map.Entry<Integer, String> entry : rowContents.entrySet()) {
            int col = entry.getKey();
            ColumnInfo columnInfo = columnInfos.get(col);
            if (columnInfo != null) {
                columnInfo.setNote(entry.getValue().replaceAll("\n", ""));
            }
        }
    }

    private void onReadData(Map<Integer, String> rowContents) throws Exception {
        JSONObject jo = new JSONObject();
        for (Map.Entry<Integer, String> entry : rowContents.entrySet()) {
            int col = entry.getKey();
            ColumnInfo columnInfo = columnInfos.get(col);
            if (columnInfo == null) {
                getLog().error("Error:处理<" + getSheetName() + ">时异常，原因: 没有找到 ColumnInfo。 content = " + rowContents);
                continue;
            }
            String content = entry.getValue();
            if (StringUtils.isBlank(content)) {
                if (columnInfo.getName().equals("sn")) {
                    throw new Exception(String.format("sheet:%s, 行:%d, 未填写sn或者是空行", this.sheetName, currentRow + 1));
                }
                jo.put(columnInfo.getName(), columnInfo.getType().getDefaultValue());
                continue;
            }

            try {
                jo.put(columnInfo.getName(), columnInfo.getType().parse(content));
            } catch (Exception e) {
                throw new Exception(String.format("sheet:%s, 行:%d, 列:%d, %s", this.sheetName, currentRow + 1, col + 1, e.getMessage()));
            }
        }

        Object sn = jo.get("sn");
        // sn中有重复的值
        if (sns.contains(sn)) {
            throw new Exception(String.format("sheet:%s, 行:%d, sn中有重复的值:%s", this.sheetName, currentRow + 1, sn.toString()));
        }
        sns.add(sn);
        dataList.add(jo);
    }

    public List<JSONObject> getDataList() {
        return dataList;
    }

    public List<Map<Integer, String>> getTestList() {
        return testList;
    }
}
