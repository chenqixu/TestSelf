package com.cqx.calcite.example.pig;

import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.RelOptTable;
import org.apache.calcite.plan.RelTraitSet;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.core.Project;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rex.RexNode;

import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * TODO
 *
 * @author chenqixu
 */
public class DemoProject extends Project implements DemoRel {

    /** Creates a DemoProject. */
    public DemoProject(RelOptCluster cluster, RelTraitSet traitSet, RelNode input,
                      List<? extends RexNode> projects, RelDataType rowType) {
        super(cluster, traitSet, ImmutableList.of(), input, projects, rowType);
        assert getConvention() == DemoRel.CONVENTION;
    }

    @Override public Project copy(RelTraitSet traitSet, RelNode input, List<RexNode> projects,
                                  RelDataType rowType) {
        return new DemoProject(input.getCluster(), traitSet, input, projects, rowType);
    }

    @Override public void implement(Implementor implementor) {
        System.out.println(getTable());
    }

    /**
     * Override this method so it looks down the tree to find the table this node
     * is acting on.
     */
    @Override public RelOptTable getTable() {
        return getInput().getTable();
    }
}
