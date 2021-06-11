package com.cqx.common.utils.jdbc.postgresql;

/**
 * PGDeclare
 *
 * @author chenqixu
 */
public class PGDeclare {
    private final String insert = "insert into %s(%s) values(%s)";
    private final String selectWhere = "select count(1) into hasval from %s where %s";
    private String pgdeclare;

    private PGDeclare() {
        StringBuilder sb = new StringBuilder();
        sb.append("do")
                .append(" $$")
                .append(" DECLARE")
                .append("  hasval numeric;")
                .append(" BEGIN")
                .append(" %s;")
                .append(" if hasval=0 THEN")
                .append(" %s;")
                .append(" end if;")
                .append(" END;")
                .append(" $$");
        pgdeclare = sb.toString();
    }

    public static PGDeclare builder() {
        return new PGDeclare();
    }

    public String declare(String tableName, String insert_fields, String insert_values, String where_values) {
        String _insert = String.format(insert, tableName, insert_fields, insert_values);
        String _selectWhere = String.format(selectWhere, tableName, where_values);
        return String.format(pgdeclare, _selectWhere, _insert);
    }
}
