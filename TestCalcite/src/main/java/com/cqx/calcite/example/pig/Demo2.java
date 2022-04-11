package com.cqx.calcite.example.pig;

import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.tools.Frameworks;

/**
 * Demo2
 *
 * @author chenqixu
 */
public class Demo2 {

    private SchemaPlus createTestSchema() {
        SchemaPlus result = Frameworks.createRootSchema(false);
        result.add("t",
                new DemoTable("target/data.txt",
                        new String[] { "tc0", "tc1" }));
        result.add("s",
                new DemoTable("target/data2.txt",
                        new String[] { "sc0", "sc1" }));
        return result;
    }
}
