package com.cqx.calcite.example;

import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.test.SqlTestFactory;
import org.apache.calcite.sql.test.SqlValidatorTester;
import org.apache.calcite.sql.validate.SqlValidator;

import java.util.List;

/**
 * MySqlValidatorTester
 *
 * @author chenqixu
 */
public class MySqlValidatorTester extends SqlValidatorTester {

    public MySqlValidatorTester(SqlTestFactory factory) {
        super(factory);
    }

    @Override
    public void checkFieldOrigin(String sql, String fieldOriginList) {
        SqlValidator validator = getValidator();
        SqlNode n = parseAndValidate(validator, sql);
        final List<List<String>> list = validator.getFieldOrigins(n);
        final StringBuilder buf = new StringBuilder("{");
        int i = 0;
        for (List<String> strings : list) {
            if (i++ > 0) {
                buf.append(", ");
            }
            if (strings == null) {
                buf.append("null");
            } else {
                int j = 0;
                for (String s : strings) {
                    if (j++ > 0) {
                        buf.append('.');
                    }
                    buf.append(s);
                }
            }
        }
        buf.append("}");
        System.out.println(buf.toString());
    }
}
