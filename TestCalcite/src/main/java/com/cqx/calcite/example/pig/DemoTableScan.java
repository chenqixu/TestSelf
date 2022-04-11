package com.cqx.calcite.example.pig;

import com.google.common.collect.ImmutableList;
import org.apache.calcite.adapter.enumerable.EnumerableRules;
import org.apache.calcite.jdbc.CalciteSchema;
import org.apache.calcite.plan.*;
import org.apache.calcite.rel.core.TableScan;
import org.apache.calcite.rel.rules.CoreRules;
import org.apache.calcite.rel.type.RelDataTypeField;
import org.apache.pig.data.DataType;

import java.util.ArrayList;
import java.util.List;

/**
 * DemoTableScan
 *
 * @author chenqixu
 */
public class DemoTableScan extends TableScan implements DemoRel {

    /** Creates a DemoTableScan. */
    public DemoTableScan(RelOptCluster cluster, RelTraitSet traitSet, RelOptTable table) {
        super(cluster, traitSet, ImmutableList.of(), table);
        assert getConvention() == DemoRel.CONVENTION;
    }

    @Override public void implement(Implementor implementor) {
        final DemoTable pigTable = getPigTable(implementor.getTableName(this));
        final String alias = implementor.getDemoRelationAlias(this);
        final String schema = '(' + getSchemaForPigStatement(implementor)
                + ')';
        final String statement = alias + " = LOAD '" + pigTable.getFilePath()
                + "' USING PigStorage() AS " + schema + ';';
        implementor.addStatement(statement);
    }

    private DemoTable getPigTable(String name) {
        final CalciteSchema schema = getTable().unwrap(org.apache.calcite.jdbc.CalciteSchema.class);
        return (DemoTable) schema.getTable(name, false).getTable();
    }

    private String getSchemaForPigStatement(Implementor implementor) {
        final List<String> fieldNamesAndTypes = new ArrayList<>(
                getTable().getRowType().getFieldList().size());
        for (RelDataTypeField f : getTable().getRowType().getFieldList()) {
            fieldNamesAndTypes.add(getConcatenatedFieldNameAndTypeForPigSchema(implementor, f));
        }
        return String.join(", ", fieldNamesAndTypes);
    }

    private String getConcatenatedFieldNameAndTypeForPigSchema(Implementor implementor,
                                                               RelDataTypeField field) {
        final DemoDataType pigDataType = DemoDataType.valueOf(field.getType().getSqlTypeName());
        final String fieldName = implementor.getFieldName(this, field.getIndex());
        return fieldName + ':' + DataType.findTypeName(pigDataType.getPigType());
    }

    @Override public void register(RelOptPlanner planner) {
        planner.addRule(DemoToEnumerableConverterRule.INSTANCE);
        for (RelOptRule rule : DemoRules.ALL_PIG_OPT_RULES) {
            planner.addRule(rule);
        }
        // Don't move Aggregates around, otherwise PigAggregate.implement() won't
        // know how to correctly procuce Pig Latin
        planner.removeRule(CoreRules.AGGREGATE_EXPAND_DISTINCT_AGGREGATES);
        // Make sure planner picks PigJoin over EnumerableHashJoin. Should there be
        // a rule for this instead for removing ENUMERABLE_JOIN_RULE here?
        planner.removeRule(EnumerableRules.ENUMERABLE_JOIN_RULE);
    }
}
