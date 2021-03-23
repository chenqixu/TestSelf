package com.cqx.common.utils.redis.bean;

/**
 * 解析bean
 *
 * @author chenqixu
 */
public class RedisSqlParserBean {
    private String table_type;
    private String table_name;
    private String[] table_fileds;
    private String condition;
    private HashFiled hashFiled = HashFiled.newbuilder();
    private InsertValue insertValue = InsertValue.newbuilder();

    public static RedisSqlParserBean newbuilder() {
        return new RedisSqlParserBean();
    }

    public String getTable_type() {
        return table_type;
    }

    public void setTable_type(String table_type) {
        this.table_type = table_type;
    }

    public String getTable_name() {
        return table_name;
    }

    public void setTable_name(String table_name) {
        this.table_name = table_name;
    }

    public String[] getTable_fileds() {
        return table_fileds;
    }

    public void setTable_fileds(String[] table_fileds) {
        this.table_fileds = table_fileds;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public HashFiled getHashFiled() {
        return hashFiled;
    }

    public void setHashFiled(boolean isAll) {
        if (isAll) {
            hashFiled.setAll();
        } else {
            for (String field : table_fileds) {
                if (field.equals("key")) hashFiled.setKey(true);
                if (field.equals("field")) hashFiled.setField(true);
                if (field.equals("value")) hashFiled.setValue(true);
            }
        }
    }

    public InsertValue getInsertValue() {
        return insertValue;
    }

    public void setInsertValue(InsertValue insertValue) {
        this.insertValue = insertValue;
    }
}
