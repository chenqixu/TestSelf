package com.cqx.calcite.example.pig;

import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.RelOptTable;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.schema.TranslatableTable;
import org.apache.calcite.schema.impl.AbstractTable;

import org.apache.pig.data.DataType;

/**
 * DemoTable
 *
 * @author chenqixu
 */
public class DemoTable extends AbstractTable implements TranslatableTable {
    private final String filePath;
    private final String[] fieldNames;

    /**
     * Creates a DemoTable.
     */
    public DemoTable(String filePath, String[] fieldNames) {
        this.filePath = filePath;
        this.fieldNames = fieldNames;
    }

    @Override
    public RelDataType getRowType(RelDataTypeFactory typeFactory) {
        final RelDataTypeFactory.Builder builder = typeFactory.builder();
        for (String fieldName : fieldNames) {
            // only supports CHARARRAY types for now
            final RelDataType relDataType = typeFactory
                    .createSqlType(DemoDataType.valueOf(DataType.CHARARRAY).getSqlType());
            final RelDataType nullableRelDataType = typeFactory
                    .createTypeWithNullability(relDataType, true);
            builder.add(fieldName, nullableRelDataType);
        }
        return builder.build();
    }

    public String getFilePath() {
        return filePath;
    }

    @Override
    public RelNode toRel(RelOptTable.ToRelContext context, RelOptTable relOptTable) {
        final RelOptCluster cluster = context.getCluster();
        return new DemoTableScan(cluster, cluster.traitSetOf(DemoRel.CONVENTION), relOptTable);
    }
}
