package com.mr.orc;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.orc.TypeDescription;
import org.apache.orc.mapred.OrcStruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * OrcReducer
 *
 * @author chenqixu
 */
public class OrcReducer extends Reducer<NullWritable, Text, NullWritable, OrcStruct> {

    private static final Logger logger = LoggerFactory.getLogger(OrcReducer.class);
    //具体OrcStruct字段对应hadoop的定义参考https://orc.apache.org/docs/mapreduce.html
    private TypeDescription schema = TypeDescription.fromString("struct<name:string,age:string>");
    private OrcStruct orcs = (OrcStruct) OrcStruct.createValue(schema);
    private final NullWritable nw = NullWritable.get();

    public void reduce(NullWritable key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        for (Text val : values) {
            if (val.toString() == null) continue;
            String[] strVals = val.toString().split("\\|");
            if (strVals.length == 2) {
                Text txtName = new Text();
                txtName.set(strVals[0]);
                orcs.setFieldValue(0, txtName);
                Text txtAge = new Text();
                txtAge.set(strVals[1]);
                orcs.setFieldValue(1, txtAge);
                context.write(nw, orcs);
            }
        }
    }
}
