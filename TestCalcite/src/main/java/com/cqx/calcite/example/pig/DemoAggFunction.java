package com.cqx.calcite.example.pig;

import org.apache.calcite.sql.SqlKind;

/**
 * Supported Demo aggregate functions and their Calcite counterparts. The enum's
 * name() is the same as the function's name in Demo Latin.
 *
 * @author chenqixu
 */
public enum DemoAggFunction {
    COUNT(SqlKind.COUNT, false),
    COUNT_STAR(SqlKind.COUNT, true);

    private final SqlKind calciteFunc;
    private final boolean star; // as in COUNT(*)

    DemoAggFunction(SqlKind calciteFunc) {
        this(calciteFunc, false);
    }

    DemoAggFunction(SqlKind calciteFunc, boolean star) {
        this.calciteFunc = calciteFunc;
        this.star = star;
    }

    public static DemoAggFunction valueOf(SqlKind calciteFunc, boolean star) {
        for (DemoAggFunction pigAggFunction : values()) {
            if (pigAggFunction.calciteFunc == calciteFunc && pigAggFunction.star == star) {
                return pigAggFunction;
            }
        }
        throw new IllegalArgumentException("Demo agg func for " + calciteFunc + " is not supported");
    }
}
