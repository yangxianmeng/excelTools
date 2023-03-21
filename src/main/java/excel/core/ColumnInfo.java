package excel.core;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Yxm
 **/
public class ColumnInfo {
    /** 字段名 */
    private String name;
    /** 字段类型 */
    private EDataType type;
    /** 字段注释 */
    private String note;
    /** 是否索引 */
    private boolean index;

    public Map<String, String> toInfoMap() {
        Map<String, String> infoMap = new LinkedHashMap<>();
        infoMap.put("type", type.getTypeName());
        infoMap.put("name", name);
        infoMap.put("note", note);
        return infoMap;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EDataType getType() {
        return type;
    }

    public void setType(EDataType type) {
        this.type = type;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public boolean isIndex() {
        return index;
    }

    public void setIndex(boolean index) {
        this.index = index;
    }
}
