package com.cqx.calcite.example.pig;

import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.RelOptTable;
import org.apache.calcite.plan.RelOptUtil;
import org.apache.calcite.plan.RelTraitSet;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.core.Filter;
import org.apache.calcite.rex.RexCall;
import org.apache.calcite.rex.RexInputRef;
import org.apache.calcite.rex.RexLiteral;
import org.apache.calcite.rex.RexNode;

import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.List;

import static org.apache.calcite.sql.SqlKind.INPUT_REF;
import static org.apache.calcite.sql.SqlKind.LITERAL;

/**
 * TODO
 *
 * @author chenqixu
 */
public class DemoFilter extends Filter implements DemoRel {

    /** Creates a DemoFilter. */
    public DemoFilter(RelOptCluster cluster, RelTraitSet traitSet, RelNode input, RexNode condition) {
        super(cluster, traitSet, input, condition);
        assert getConvention() == DemoRel.CONVENTION;
    }

    @Override public Filter copy(RelTraitSet traitSet, RelNode input, RexNode condition) {
        return new DemoFilter(getCluster(), traitSet, input, condition);
    }

    @Override public void implement(Implementor implementor) {
        implementor.visitChild(0, getInput());
        implementor.addStatement(getDemoFilterStatement(implementor));
    }

    /**
     * Override this method so it looks down the tree to find the table this node
     * is acting on.
     */
    @Override public RelOptTable getTable() {
        return getInput().getTable();
    }

    /**
     * Generates Demo Latin filtering statements. For example
     *
     * <blockquote>
     *   <pre>table = FILTER table BY score &gt; 2.0;</pre>
     * </blockquote>
     */
    private String getDemoFilterStatement(Implementor implementor) {
        Preconditions.checkState(containsOnlyConjunctions(condition));
        String relationAlias = implementor.getDemoRelationAlias(this);
        List<String> filterConditionsConjunction = new ArrayList<>();
        for (RexNode node : RelOptUtil.conjunctions(condition)) {
            filterConditionsConjunction.add(getSingleFilterCondition(implementor, node));
        }
        String allFilterConditions =
                String.join(" AND ", filterConditionsConjunction);
        return relationAlias + " = FILTER " + relationAlias + " BY " + allFilterConditions + ';';
    }

    private String getSingleFilterCondition(Implementor implementor, RexNode node) {
        switch (node.getKind()) {
            case EQUALS:
                return getSingleFilterCondition(implementor, "==", (RexCall) node);
            case LESS_THAN:
                return getSingleFilterCondition(implementor, "<", (RexCall) node);
            case LESS_THAN_OR_EQUAL:
                return getSingleFilterCondition(implementor, "<=", (RexCall) node);
            case GREATER_THAN:
                return getSingleFilterCondition(implementor, ">", (RexCall) node);
            case GREATER_THAN_OR_EQUAL:
                return getSingleFilterCondition(implementor, ">=", (RexCall) node);
            default:
                throw new IllegalArgumentException("Cannot translate node " + node);
        }
    }

    private String getSingleFilterCondition(Implementor implementor, String op, RexCall call) {
        final String fieldName;
        final String literal;
        final RexNode left = call.operands.get(0);
        final RexNode right = call.operands.get(1);
        if (left.getKind() == LITERAL) {
            if (right.getKind() != INPUT_REF) {
                throw new IllegalArgumentException(
                        "Expected a RexCall with a single field and single literal");
            } else {
                fieldName = implementor.getFieldName(this, ((RexInputRef) right).getIndex());
                literal = getLiteralAsString((RexLiteral) left);
            }
        } else if (right.getKind() == LITERAL) {
            if (left.getKind() != INPUT_REF) {
                throw new IllegalArgumentException(
                        "Expected a RexCall with a single field and single literal");
            } else {
                fieldName = implementor.getFieldName(this, ((RexInputRef) left).getIndex());
                literal = getLiteralAsString((RexLiteral) right);
            }
        } else {
            throw new IllegalArgumentException(
                    "Expected a RexCall with a single field and single literal");
        }

        return '(' + fieldName + ' ' + op + ' ' + literal + ')';
    }

    private boolean containsOnlyConjunctions(RexNode condition) {
        return RelOptUtil.disjunctions(condition).size() == 1;
    }

    /**
     * Converts a literal to a Demo Latin string literal.
     *
     * <p>TODO: do proper literal to string conversion + escaping
     */
    private String getLiteralAsString(RexLiteral literal) {
        return '\'' + RexLiteral.stringValue(literal) + '\'';
    }
}
