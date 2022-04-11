package com.cqx.calcite.example.pig;

import org.apache.calcite.adapter.enumerable.EnumerableRel;
import org.apache.calcite.adapter.enumerable.EnumerableRelImplementor;
import org.apache.calcite.adapter.enumerable.JavaRowFormat;
import org.apache.calcite.adapter.enumerable.PhysType;
import org.apache.calcite.adapter.enumerable.PhysTypeImpl;
import org.apache.calcite.linq4j.tree.BlockBuilder;
import org.apache.calcite.linq4j.tree.Expressions;
import org.apache.calcite.plan.ConventionTraitDef;
import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.RelTraitSet;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.convert.ConverterImpl;
import org.apache.calcite.runtime.Hook;
import org.apache.calcite.util.BuiltInMethod;

import java.util.List;

/**
 * DemoToEnumerableConverter
 *
 * @author chenqixu
 */
public class DemoToEnumerableConverter
        extends ConverterImpl
        implements EnumerableRel {
    /** Creates a DemoToEnumerableConverter. */
    protected DemoToEnumerableConverter(
            RelOptCluster cluster,
            RelTraitSet traits,
            RelNode input) {
        super(cluster, ConventionTraitDef.INSTANCE, traits, input);
    }

    @Override public RelNode copy(RelTraitSet traitSet, List<RelNode> inputs) {
        return new DemoToEnumerableConverter(
                getCluster(), traitSet, sole(inputs));
    }

    /**
     * {@inheritDoc}
     *
     * <p>This implementation does not actually execute the associated Demo Latin
     * script and return results. Instead it returns an empty
     * {@link org.apache.calcite.adapter.enumerable.EnumerableRel.Result}
     * in order to allow for testing and verification of every step of query
     * processing up to actual physical execution and result verification.
     *
     * <p>Next step is to invoke Demo from here, likely in local mode, have it
     * store results in a predefined file so they can be read here and returned as
     * a {@code Result} object.
     */
    public Result implement(EnumerableRelImplementor implementor, Prefer pref) {
        final BlockBuilder list = new BlockBuilder();
        final PhysType physType =
                PhysTypeImpl.of(implementor.getTypeFactory(), rowType,
                        pref.prefer(JavaRowFormat.ARRAY));
        DemoRel.Implementor impl = new DemoRel.Implementor();
        impl.visitChild(0, getInput());
        Hook.QUERY_PLAN.run(impl.getScript()); // for script validation in tests
        list.add(
                Expressions.return_(null,
                        Expressions.call(
                                BuiltInMethod.EMPTY_ENUMERABLE.method)));
        return implementor.result(physType, list.toBlock());
    }
}
