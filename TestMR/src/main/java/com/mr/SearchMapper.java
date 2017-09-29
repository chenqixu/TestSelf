package com.mr;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.Text;

public class SearchMapper extends TableMapper<ImmutableBytesWritable, Text> {
	private static final Log LOG = LogFactory.getLog(SearchMapper.class);
	
	private static String TABLE_NAME = "tablename";  
    private static byte[] FAMILY_NAME = Bytes.toBytes("cfname");  
    private static byte[][] QUALIFIER_NAME = { Bytes.toBytes("col1"),  
            Bytes.toBytes("col2"), Bytes.toBytes("col3") };  
	
	private int numOfFilter = 0;

	private Text word = new Text();

	String[] strConditionStrings = new String[] { "", "", "" }/* { "新C87310", "10", "2" } */;

	/* 
	 * private void init(Configuration conf) throws IOException, 
	 * InterruptedException { strConditionStrings[0] = 
	 * conf.get("search.license").trim(); strConditionStrings[1] = 
	 * conf.get("search.carColor").trim(); strConditionStrings[2] = 
	 * conf.get("search.direction").trim(); LOG.info("license: " + 
	 * strConditionStrings[0]); } 
	 */
	protected void setup(Context context) throws IOException,
			InterruptedException {
		strConditionStrings[0] = context.getConfiguration().get(
				"search.license").trim();
		strConditionStrings[1] = context.getConfiguration().get("search.color")
				.trim();
		strConditionStrings[2] = context.getConfiguration().get(
				"search.direction").trim();
	}

	protected void map(ImmutableBytesWritable key, Result value, Context context)
			throws InterruptedException, IOException {
		String string = "";
		String tempString;

		/**/
		for (int i = 0; i < 1; i++) {
			// /在此map里进行filter的功能  
			tempString = Text.decode(value.getValue(FAMILY_NAME,
					QUALIFIER_NAME[i]));
			if (tempString.equals(/* strConditionStrings[i] */"新C87310")) {
				LOG.info("新C87310. conf: " + strConditionStrings[0]);
				if (tempString.equals(strConditionStrings[i])) {
					string = string + tempString + " ";
				} else {
					return;
				}
			}

			else {
				return;
			}
		}

		word.set(string);
		context.write(null, word);
	}
}
