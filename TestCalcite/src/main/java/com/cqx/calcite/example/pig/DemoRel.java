package com.cqx.calcite.example.pig;

import org.apache.calcite.plan.Convention;
import org.apache.calcite.rel.RelNode;

import java.util.ArrayList;
import java.util.List;

/**
 * DemoRel
 *
 * @author chenqixu
 */
public interface DemoRel extends RelNode {

    /**
     * Calling convention for relational operations that occur in Demo.
     */
    Convention CONVENTION = new Convention.Impl("DEMO", DemoRel.class);

    // String getDemoRelationAlias();
    //
    // String getFieldName(int index);

    /**
     * Converts this node to a Demo Latin statement.
     */
    void implement(Implementor implementor);

    /**
     * Callback for the implementation process that converts a tree of
     * {@link DemoRel} nodes into complete Demo Latin script.
     */
    class Implementor {

        /**
         * An ordered list of Demo Latin statements.
         *
         * <p>See
         * <a href="https://pig.apache.org/docs/r0.13.0/start.html#pl-statements">
         * Demo Latin reference</a>.
         */
        private final List<String> statements = new ArrayList<>();

        public String getTableName(RelNode input) {
            final List<String> qualifiedName = input.getTable().getQualifiedName();
            return qualifiedName.get(qualifiedName.size() - 1);
        }

        public String getDemoRelationAlias(RelNode input) {
            return getTableName(input);
        }

        public String getFieldName(RelNode input, int index) {
            return input.getRowType().getFieldList().get(index).getName();
        }

        public void addStatement(String statement) {
            statements.add(statement);
        }

        public void visitChild(int ordinal, RelNode input) {
            assert ordinal == 0;
            ((DemoRel) input).implement(this);
        }

        public List<String> getStatements() {
            return statements;
        }

        public String getScript() {
            return String.join("\n", statements);
        }
    }
}
