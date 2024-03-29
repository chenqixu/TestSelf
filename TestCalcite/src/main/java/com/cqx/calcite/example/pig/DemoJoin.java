package com.cqx.calcite.example.pig;

import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.RelOptTable;
import org.apache.calcite.plan.RelOptUtil;
import org.apache.calcite.plan.RelTraitSet;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.core.Join;
import org.apache.calcite.rel.core.JoinRelType;
import org.apache.calcite.rex.RexCall;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.sql.SqlKind;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO
 *
 * @author chenqixu
 */
public class DemoJoin extends Join implements DemoRel {

    /** Creates a DemoJoin. */
    public DemoJoin(RelOptCluster cluster, RelTraitSet traitSet, RelNode left, RelNode right,
                   RexNode condition, JoinRelType joinType) {
        super(cluster, traitSet, ImmutableList.of(), left, right, condition,
                ImmutableSet.of(), joinType);
        assert getConvention() == DemoRel.CONVENTION;
    }

    @Override public Join copy(RelTraitSet traitSet, RexNode conditionExpr, RelNode left,
                               RelNode right, JoinRelType joinType, boolean semiJoinDone) {
        return new DemoJoin(getCluster(), traitSet, left, right, conditionExpr, joinType);
    }

    @Override public void implement(Implementor implementor) {
        implementor.visitChild(0, getLeft());
        implementor.visitChild(0, getRight());
        implementor.addStatement(getDemoJoinStatement(implementor));
    }

    /**
     * The Demo alias of the joined relation will have the same name as one from
     * the left side of the join.
     */
    @Override public RelOptTable getTable() {
        return getLeft().getTable();
    }

    /**
     * Constructs a Demo JOIN statement in the form of
     * <pre>
     * {@code
     * A = JOIN A BY f1 LEFT OUTER, B BY f2;
     * }
     * </pre>
     * Only supports simple equi-joins with single column on both sides of
     * <code>=</code>.
     */
    private String getDemoJoinStatement(Implementor implementor) {
        if (!getCondition().isA(SqlKind.EQUALS)) {
            throw new IllegalArgumentException("Only equi-join are supported");
        }
        List<RexNode> operands = ((RexCall) getCondition()).getOperands();
        if (operands.size() != 2) {
            throw new IllegalArgumentException("Only equi-join are supported");
        }
        List<Integer> leftKeys = new ArrayList<>(1);
        List<Integer> rightKeys = new ArrayList<>(1);
        List<Boolean> filterNulls = new ArrayList<>(1);
        RelOptUtil.splitJoinCondition(getLeft(), getRight(), getCondition(), leftKeys, rightKeys,
                filterNulls);

        String leftRelAlias = implementor.getDemoRelationAlias((DemoRel) getLeft());
        String rightRelAlias = implementor.getDemoRelationAlias((DemoRel) getRight());
        String leftJoinFieldName = implementor.getFieldName((DemoRel) getLeft(), leftKeys.get(0));
        String rightJoinFieldName = implementor.getFieldName((DemoRel) getRight(), rightKeys.get(0));

        return implementor.getDemoRelationAlias((DemoRel) getLeft()) + " = JOIN " + leftRelAlias + " BY "
                + leftJoinFieldName + ' ' + getDemoJoinType() + ", " + rightRelAlias + " BY "
                + rightJoinFieldName + ';';
    }

    /**
     * Get a string representation of the type of join for use in a Demo script.
     * Demo does not have an explicit "inner" marker, so return an empty string in
     * this case.
     */
    private String getDemoJoinType() {
        switch (getJoinType()) {
            case INNER:
                return "";
            default:
                return getJoinType().name();
        }
    }
}
