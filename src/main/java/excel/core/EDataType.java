package excel.core;

/**
 * @author Yxm
 **/
public enum EDataType {
    INT("int", 0),
    LONG("long", 0L),
    FLOAT("float", 0f),
    DOUBLE("double", 0.0),
    BOOLEAN("boolean", false),
    STRING("String", ""),

    INT_ARRAY("int[]", ""),
    LONG_ARRAY("long[]", ""),
    FLOAT_ARRAY("float[]", ""),
    DOUBLE_ARRAY("double[]", ""),
    BOOLEAN_ARRAY("boolean[]", ""),
    STRING_ARRAY("String[]", ""),
            ;

    /** 类型描述 */
    private String typeName;
    /** 默认值 */
    private Object defaultValue;

    private EDataType(String typeName, Object defaultValue) {
        this.typeName = typeName;
        this.defaultValue = defaultValue;
    }

    public Object parse(String str) {
        switch (this) {
            case INT:
                return Integer.parseInt(str);
            case LONG:
                return Long.parseLong(str);
            case FLOAT:
                return Float.parseFloat(str);
            case DOUBLE:
                return Double.parseDouble(str);
            case BOOLEAN:
                if (str.equalsIgnoreCase("true") || str.equals("1")) {
                    return true;
                } else if (str.equalsIgnoreCase("false") || str.equals("0")) {
                    return false;
                } else {
                    throw new IllegalArgumentException(String.format("数据格式错误，只能是0，1，true，false，值:%s", str));
                }
            default:
                return str;
        }
    }

    public String getTypeName() {
        return typeName;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    /**
     * 通过描述获取枚举类型
     * @param text
     * @return
     */
    public static EDataType getByText(String text) {
        for (EDataType e : EDataType.values()) {
            if (e.typeName.equals(text)) {
                return e;
            }
        }

        return null;
    }
}
