package com.cqx.calcite.example;

import org.apache.calcite.avatica.util.Casing;
import org.apache.calcite.avatica.util.Quoting;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.schema.impl.AbstractTable;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql.validate.SqlConformanceEnum;
import org.apache.calcite.tools.FrameworkConfig;
import org.apache.calcite.tools.Frameworks;
import org.apache.calcite.tools.Planner;

/**
 * AbstractTableFactory
 *
 * @author chenqixu
 */
public class AbstractTableFactory {
    private SchemaPlus rootSchema = Frameworks.createRootSchema(true);

    public void addTable(String table_name, AbstractTable abstractTable) {
        rootSchema.add(table_name, abstractTable);
    }

    public Planner buildPlanner() {
        final FrameworkConfig config = Frameworks.newConfigBuilder()
//                .parserConfig(SqlParser.Config.DEFAULT)//默认
                .parserConfig(SqlParser.configBuilder()
                        .setCaseSensitive(false)//大小写忽略
                        .setQuoting(Quoting.BACK_TICK)
                        .setQuotedCasing(Casing.TO_UPPER)
                        .setUnquotedCasing(Casing.TO_UPPER)
                        .setConformance(SqlConformanceEnum.ORACLE_12)
                        .build())
                .defaultSchema(rootSchema)
                .build();
        return Frameworks.getPlanner(config);
    }
}
