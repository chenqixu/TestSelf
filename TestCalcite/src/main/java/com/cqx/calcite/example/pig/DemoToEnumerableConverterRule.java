package com.cqx.calcite.example.pig;

import org.apache.calcite.adapter.enumerable.EnumerableConvention;
import org.apache.calcite.plan.RelTraitSet;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.convert.ConverterRule;

/**
 * DemoToEnumerableConverterRule
 *
 * @author chenqixu
 */
public class DemoToEnumerableConverterRule extends ConverterRule {
    public static final ConverterRule INSTANCE = Config.INSTANCE
            .withConversion(RelNode.class, DemoRel.CONVENTION,
                    EnumerableConvention.INSTANCE, "DemoToEnumerableConverterRule")
            .withRuleFactory(DemoToEnumerableConverterRule::new)
            .toRule(DemoToEnumerableConverterRule.class);

    private DemoToEnumerableConverterRule(Config config) {
        super(config);
    }

    @Override public RelNode convert(RelNode rel) {
        RelTraitSet newTraitSet = rel.getTraitSet().replace(getOutConvention());
        return new DemoToEnumerableConverter(rel.getCluster(), newTraitSet, rel);
    }
}
