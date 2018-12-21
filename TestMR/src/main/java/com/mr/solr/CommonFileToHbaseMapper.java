package com.mr.solr;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * solr索引
 *
 * @author chenqixu
 */
public class CommonFileToHbaseMapper extends Mapper<LongWritable, Text, ImmutableBytesWritable, KeyValue> {
    public String columnFamily;
    public int modNUm;
    public String solrUrl = "http://10.1.8.75:8983/solr/new_core";    //http://10.47.224.30:8080/solr/index.html#/new_core/collection-overview
    private static Configuration conf = null;
    private FileSystem fs = null;
    private FSDataOutputStream stm = null;
    private HttpSolrServer client = null;
    private List<SolrInputDocument> docs = null;


    protected void setup(Mapper<LongWritable, Text, ImmutableBytesWritable, KeyValue>.Context context)
            throws IOException {
        docs = new LinkedList<SolrInputDocument>();
        if (conf == null) {
            conf = context.getConfiguration();
        }
        this.columnFamily = conf.get("COLUMN_FAMILY");

        this.modNUm = Integer.parseInt(conf.get("MOD_NUM"));
        int partition = context.getTaskAttemptID().getTaskID().getId();
        this.fs = FileSystem.newInstance(conf);
        Path path = new Path(new StringBuilder().append(conf.get("FULL_ERROR_OUTPUT_PATH")).append(conf.get("ERROR_FILE_NAME")).append("_").append(partition).toString());

        //创建solrClient同时指定超时时间，不指定走默认配置
        this.client = new HttpSolrServer(solrUrl);
        if (!this.fs.exists(path)) {
            this.stm = this.fs.create(path);
        } else {
            this.fs.delete(path, true);
            this.stm = this.fs.create(path);
        }
        System.out.println("setup 完成！！");
    }

    protected void cleanup(Mapper<LongWritable, Text, ImmutableBytesWritable, KeyValue>.Context context) throws IOException {
        this.stm.close();
        this.fs.close();
        try {
            this.client.add(docs);
            this.client.commit();
            this.docs.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("cleanup 完成！！！");
    }

    protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, ImmutableBytesWritable, KeyValue>.Context context)
            throws IOException {
        String line = value.toString();

        line = CommonUtils.pretreatmentPhone(line, conf);
        line = CommonUtils.pretreatmentTime(line, conf);
        String[] valuesArray = null;
        if (conf.get("SOURCE_SPILT").equals("|"))
            valuesArray = line.split("\\|", -1);
        else {
            valuesArray = line.split(conf.get("SOURCE_SPILT"), -1);
        }

        int outputCount = 0;

        if (valuesArray.length != Integer.parseInt(conf.get("SOURCE_DATE_LENGTH"))) {
            this.stm.writeBytes(new StringBuilder().append("valuesArray:\t").append(valuesArray).append("\n").toString());
            this.stm.writeBytes(new StringBuilder().append("conf.get(Constant.SOURCE_SPILT):\t").append(conf.get("SOURCE_SPILT")).append("\n").toString());

            this.stm.writeBytes(new StringBuilder().append("The source leng error:\t").append(value).append("\n").toString());
            return;
        }
        try {
            StringBuilder rowkeyString = new StringBuilder();
            String mod = CommonUtils.generateModKey(Long.parseLong(valuesArray[Integer.parseInt(conf.get("MOD_POSITION"))]), this.modNUm);

            rowkeyString.append(mod).append(conf.get("ROWKEY_SPILT"));
            String[] rowkeyField = conf.get("ROWKEY_POSITIONS").split(",");

            for (int i = 0; i < rowkeyField.length; i++) {
                if (CommonUtils.isNumeric(rowkeyField[i])) {
                    rowkeyString.append(valuesArray[Integer.parseInt(rowkeyField[i])]).append(conf.get("ROWKEY_SPILT"));
                } else if (rowkeyField[i].contains("+")) {
                    String[] str = rowkeyField[i].split("\\+");
                    for (int j = 0; j < str.length; j++) {
                        if (CommonUtils.isNumeric(str[j])) {
                            rowkeyString.append(valuesArray[Integer.parseInt(str[j])]);
                        } else {
                            rowkeyString.append(str[j]);
                        }
                    }
                    rowkeyString.append(conf.get("ROWKEY_SPILT"));
                } else {
                    rowkeyString.append(rowkeyField[i]).append(conf.get("ROWKEY_SPILT"));
                }

            }

            ImmutableBytesWritable rowkey = new ImmutableBytesWritable(Bytes.toBytes(rowkeyString.toString().substring(0, rowkeyString.toString().length() - conf.get("ROWKEY_SPILT").length())));

//==============================================================================
            //创建文档doc
            SolrInputDocument doc = new SolrInputDocument();
            doc.addField("id", rowkeyString.toString().substring(0, rowkeyString.toString().length() - conf.get("ROWKEY_SPILT").length()));
//==================================================================================
            KeyValue kv = null;
            String[] qualifys = conf.get("QUALIFY_MAP_VALUE").split(";");
            for (int i = 0; i < qualifys.length; i++) {
                StringBuilder qualifyValue = new StringBuilder();
                String[] qualify = qualifys[i].split(":");
                String qualifyName = qualify[0];
                String qualifyValueIndex = qualify[1];
                if (qualifyValueIndex.equals("-1")) {
                    qualifyValue.append(line).append(conf.get("SOURCE_SPILT"));
//==================================================================================
                    doc.addField("msisdn", line.split("\\|")[2].trim());
//==================================================================================
                } else {
                    String[] index = qualifyValueIndex.split(",");
                    for (int j = 0; j < index.length; j++) {
                        qualifyValue.append(valuesArray[Integer.parseInt(index[j])]).append(conf.get("SOURCE_SPILT"));
                    }

                }

                kv = new KeyValue(Bytes.toBytes(rowkeyString.toString().substring(0, rowkeyString.toString().length() - conf.get("ROWKEY_SPILT").length())), Bytes.toBytes(this.columnFamily), Bytes.toBytes(qualifyName), System.currentTimeMillis(), Bytes.toBytes(qualifyValue.toString().substring(0, qualifyValue.toString().length() - conf.get("SOURCE_SPILT").length())));
//==================================================================================
//				UpdateResponse updateResponse = client.add(doc);
//==================================================================================
                if (null != kv)
                    context.write(rowkey, kv);
//==================================================================================
//				System.out.println("docs+1...");
				this.docs.add(doc);

                if (this.docs.size() == Integer.parseInt(conf.get("SOLR_COMMIT_LIMIT"))) {
                    System.out.println("client commit...");
                    this.client.add(docs);
                    this.client.commit();
                    this.docs.clear();
                }
//==================================================================================
            }
        } catch (Exception e) {
            this.stm.writeBytes(new StringBuilder().append("The rowkey error:\t").append(value).append("\n").append(e.getMessage()).toString());

            e.printStackTrace();
        }
    }
}
