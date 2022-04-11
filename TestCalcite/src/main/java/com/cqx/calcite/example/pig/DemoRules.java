package com.cqx.calcite.example.pig;

import com.google.common.collect.ImmutableList;
import org.apache.calcite.plan.Convention;
import org.apache.calcite.plan.RelTraitSet;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.convert.ConverterRule;
import org.apache.calcite.rel.logical.*;

import java.util.List;

/**
 * DemoRules
 *
 * @author chenqixu
 */
public class DemoRules {
    public static final List<ConverterRule> ALL_PIG_OPT_RULES =
            ImmutableList.of(DemoFilterRule.INSTANCE,
                    DemoTableScanRule.INSTANCE,
                    DemoProjectRule.INSTANCE,
                    DemoAggregateRule.INSTANCE,
                    DemoJoinRule.INSTANCE);

    // prevent instantiation
    private DemoRules() {}

    /**
     * Rule to convert a {@link org.apache.calcite.rel.logical.LogicalFilter} to a
     * {@link DemoFilter}.
     */
    private static class DemoFilterRule extends ConverterRule {
        private static final DemoFilterRule INSTANCE = Config.INSTANCE
                .withConversion(LogicalFilter.class, Convention.NONE,
                        DemoRel.CONVENTION, "DemoFilterRule")
                .withRuleFactory(DemoFilterRule::new)
                .toRule(DemoFilterRule.class);

        protected DemoFilterRule(Config config) {
            super(config);
        }

        @Override public RelNode convert(RelNode rel) {
            final LogicalFilter filter = (LogicalFilter) rel;
            final RelTraitSet traitSet = filter.getTraitSet().replace(DemoRel.CONVENTION);
            return new DemoFilter(rel.getCluster(), traitSet,
                    convert(filter.getInput(), DemoRel.CONVENTION), filter.getCondition());
        }
    }

    /**
     * Rule to convert a {@link org.apache.calcite.rel.logical.LogicalTableScan}
     * to a {@link DemoTableScan}.
     */
    private static class DemoTableScanRule extends ConverterRule {
        private static final DemoTableScanRule INSTANCE = Config.INSTANCE
                .withConversion(LogicalTableScan.class, Convention.NONE,
                        DemoRel.CONVENTION, "DemoTableScanRule")
                .withRuleFactory(DemoTableScanRule::new)
                .as(Config.class)
                .toRule(DemoTableScanRule.class);

        protected DemoTableScanRule(Config config) {
            super(config);
        }

        @Override public RelNode convert(RelNode rel) {
            final LogicalTableScan scan = (LogicalTableScan) rel;
            final RelTraitSet traitSet =
                    scan.getTraitSet().replace(DemoRel.CONVENTION);
            return new DemoTableScan(rel.getCluster(), traitSet, scan.getTable());
        }
    }

    /**
     * Rule to convert a {@link org.apache.calcite.rel.logical.LogicalProject} to
     * a {@link DemoProject}.
     */
    private static class DemoProjectRule extends ConverterRule {
        private static final DemoProjectRule INSTANCE = Config.INSTANCE
                .withConversion(LogicalProject.class, Convention.NONE,
                        DemoRel.CONVENTION, "DemoProjectRule")
                .withRuleFactory(DemoProjectRule::new)
                .toRule(DemoProjectRule.class);

        protected DemoProjectRule(Config config) {
            super(config);
        }

        @Override public RelNode convert(RelNode rel) {
            final LogicalProject project = (LogicalProject) rel;
            final RelTraitSet traitSet = project.getTraitSet().replace(DemoRel.CONVENTION);
            return new DemoProject(project.getCluster(), traitSet, project.getInput(),
                    project.getProjects(), project.getRowType());
        }
    }

    /**
     * Rule to convert a {@link org.apache.calcite.rel.logical.LogicalAggregate} to a
     * {@link DemoAggregate}.
     */
    private static class DemoAggregateRule extends ConverterRule {
        private static final DemoAggregateRule INSTANCE = Config.INSTANCE
                .withConversion(LogicalAggregate.class, Convention.NONE,
                        DemoRel.CONVENTION, "DemoAggregateRule")
                .withRuleFactory(DemoAggregateRule::new)
                .toRule(DemoAggregateRule.class);

        protected DemoAggregateRule(Config config) {
            super(config);
        }

        @Override public RelNode convert(RelNode rel) {
            final LogicalAggregate agg = (LogicalAggregate) rel;
            final RelTraitSet traitSet = agg.getTraitSet().replace(DemoRel.CONVENTION);
            return new DemoAggregate(agg.getCluster(), traitSet, agg.getInput(),
                    agg.getGroupSet(), agg.getGroupSets(), agg.getAggCallList());
        }
    }

    /**
     * Rule to convert a {@link org.apache.calcite.rel.logical.LogicalJoin} to
     * a {@link DemoJoin}.
     */
    private static class DemoJoinRule extends ConverterRule {
        private static final DemoJoinRule INSTANCE = Config.INSTANCE
                .withConversion(LogicalJoin.class, Convention.NONE,
                        DemoRel.CONVENTION, "DemoJoinRule")
                .withRuleFactory(DemoJoinRule::new)
                .toRule(DemoJoinRule.class);

        protected DemoJoinRule(Config config) {
            super(config);
        }

        @Override public RelNode convert(RelNode rel) {
            final LogicalJoin join = (LogicalJoin) rel;
            final RelTraitSet traitSet = join.getTraitSet().replace(DemoRel.CONVENTION);
            return new DemoJoin(join.getCluster(), traitSet, join.getLeft(), join.getRight(),
                    join.getCondition(), join.getJoinType());
        }
    }
}
