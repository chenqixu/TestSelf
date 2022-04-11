package com.cqx.calcite.example;

import com.cqx.calcite.bean.HrSchema;
import com.cqx.calcite.bean.signal.five.FiveSignalSchema;
import com.cqx.calcite.bean.signal.five.N1N2;
import org.apache.calcite.adapter.java.ReflectiveSchema;
import org.apache.calcite.jdbc.CalciteConnection;
import org.apache.calcite.plan.RelOptUtil;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.RelRoot;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.schema.Schema;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.schema.impl.AbstractTable;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.calcite.tools.FrameworkConfig;
import org.apache.calcite.tools.Frameworks;
import org.apache.calcite.tools.Planner;

import java.sql.*;
import java.util.Properties;

/**
 * CalciteJavaBean
 *
 * @author chenqixu
 */
public class CalciteJavaBean {
    private static final String SQL1 = "select d.deptno, min(e.empid) as min_empid\n"
            + "from hr.emps as e\n"
            + "join hr.depts as d\n"
            + "  on e.deptno = d.deptno\n"
            + "group by d.deptno\n"
            + "having count(*) > 1";
    private FiveSignalSchema fiveSignalSchema;

    public static void main(String[] args) throws Exception {
        CalciteJavaBean calciteJavaBean = new CalciteJavaBean();
//        calciteJavaBean.queryBySQL(SQL1);
//        calciteJavaBean.parser();
//        calciteJavaBean.abstractTable();
        calciteJavaBean.init();
        String sql_n1n2 = "select msisdn from bigdata.n1n2 where msisdn between 1440609800000 and 1440609999999";
        calciteJavaBean.query(sql_n1n2);
        N1N2[] n1n2 = {
                new N1N2(1440609800000L),
                new N1N2(1440709800000L),
                new N1N2(1440809800001L),
                new N1N2(1440909999999L)
        };
        calciteJavaBean.newData(n1n2);
        calciteJavaBean.query(sql_n1n2);

        String sql_n5 = "select dnn,snssai_sst from bigdata.n5 where (" +
                "(dnn is not null and dnn not like 'CMNET%') " +
                "and (dnn is not null and dnn not like 'CMWAP%') " +
                "and (dnn is not null and dnn not like 'IMS%') " +
                "and (dnn is not null and dnn not like 'CMDTJ%')) or snssai_sst=128";
        calciteJavaBean.query(sql_n5);
    }

    private void init() throws ClassNotFoundException {
        Class.forName("org.apache.calcite.jdbc.Driver");
        fiveSignalSchema = new FiveSignalSchema();
    }

    private void query(String sql) throws SQLException {
        System.out.println(String.format("query：%s", sql));
        ResultSet resultSet = null;
        Statement statement = null;
        try (Connection conn = getConn()) {
            CalciteConnection calciteConnection = conn.unwrap(CalciteConnection.class);
            SchemaPlus rootSchema = calciteConnection.getRootSchema();
            Schema schema = new ReflectiveSchema(fiveSignalSchema);
            // 给schema 5g中添加表
            rootSchema.add("bigdata", schema);
            statement = calciteConnection.createStatement();
            resultSet = statement.executeQuery(sql);
            int columnCount = resultSet.getMetaData().getColumnCount();
            while (resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    Object val = resultSet.getObject(i);
                    System.out.println(String.format("val：%s", val));
                }
            }
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
        }
    }

    private Connection getConn() throws SQLException {
        Properties info = new Properties();
        info.setProperty("lex", "JAVA");
        return DriverManager.getConnection("jdbc:calcite:", info);
    }

    private void newData(N1N2[] data) {
        fiveSignalSchema.setN1n2(data);
    }

    public void queryBySQL(String sql) throws Exception {
        Class.forName("org.apache.calcite.jdbc.Driver");
        Properties info = new Properties();
        info.setProperty("lex", "JAVA");
        Connection connection = DriverManager.getConnection("jdbc:calcite:", info);
        CalciteConnection calciteConnection = connection.unwrap(CalciteConnection.class);
        SchemaPlus rootSchema = calciteConnection.getRootSchema();
        Schema schema = new ReflectiveSchema(new HrSchema());
        //给schema hr中添加表
        rootSchema.add("hr", schema);
        Statement statement = calciteConnection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        while (resultSet.next()) {
            String deptno = resultSet.getString("deptno");
            int min_empid = resultSet.getInt("min_empid");
            System.out.println(String.format("deptno：%s，min_empid：%s", deptno, min_empid));
        }
        resultSet.close();
        statement.close();
        connection.close();
    }

    public void parser() throws Exception {
        //ConfigBuilder
        Frameworks.ConfigBuilder configBuilder = Frameworks.newConfigBuilder();

        //给schema hr中添加表
        SchemaPlus schemaPlus = Frameworks.createRootSchema(true);
        schemaPlus.add("hr", new ReflectiveSchema(new HrSchema()));

        //设置默认schema
        configBuilder.defaultSchema(schemaPlus);

        //SQL 大小写不敏感
        SqlParser.ConfigBuilder paresrConfig = SqlParser.configBuilder();
        paresrConfig.setCaseSensitive(false).setConfig(paresrConfig.build());
        configBuilder.parserConfig(paresrConfig.build());

        //最后build成FrameworkConfig
        FrameworkConfig frameworkConfig = configBuilder.build();

        //从FrameworkConfig生成一个Planner
        Planner planner = Frameworks.getPlanner(frameworkConfig);
        SqlNode sqlNode;
        RelRoot relRoot;
        //parser阶段
        sqlNode = planner.parse(SQL1);
        //validate阶段
        planner.validate(sqlNode);
        //获取RelNode树的根
        relRoot = planner.rel(sqlNode);
        RelNode relNode = relRoot.project();
        //打印relNode
        System.out.print(RelOptUtil.toString(relNode));
    }

    public void abstractTable() throws Exception {
        String sql = "insert into table1(d,e,f) select a,b,c from table2 as t1 where t1.a='001' and t1.b='a'";
        AbstractTableFactory abstractTableFactory = new AbstractTableFactory();
        abstractTableFactory.addTable("table1", new AbstractTable() {
            @Override
            public RelDataType getRowType(RelDataTypeFactory typeFactory) {
                RelDataTypeFactory.Builder builder = typeFactory.builder();
                RelDataType t0 = typeFactory.createTypeWithNullability(typeFactory.createSqlType(SqlTypeName.VARCHAR), true);
                RelDataType t1 = typeFactory.createTypeWithNullability(typeFactory.createSqlType(SqlTypeName.VARCHAR), true);
                RelDataType t2 = typeFactory.createTypeWithNullability(typeFactory.createSqlType(SqlTypeName.VARCHAR), true);
                builder.add("d", t0);
                builder.add("e", t1);
                builder.add("f", t2);
                return builder.build();
            }
        });
        abstractTableFactory.addTable("table2", new AbstractTable() {
            @Override
            public RelDataType getRowType(RelDataTypeFactory typeFactory) {
                RelDataTypeFactory.Builder builder = typeFactory.builder();
                RelDataType t0 = typeFactory.createTypeWithNullability(typeFactory.createSqlType(SqlTypeName.VARCHAR), true);
                RelDataType t1 = typeFactory.createTypeWithNullability(typeFactory.createSqlType(SqlTypeName.VARCHAR), true);
                RelDataType t2 = typeFactory.createTypeWithNullability(typeFactory.createSqlType(SqlTypeName.VARCHAR), true);
                builder.add("a", t0);
                builder.add("b", t1);
                builder.add("c", t2);
                return builder.build();
            }
        });
        Planner planner = abstractTableFactory.buildPlanner();
        SqlNode parse = planner.parse(sql);
        SqlNode validate = planner.validate(parse);
        RelRoot root = planner.rel(validate);
        System.out.println("sql：" + sql);
        System.out.println(RelOptUtil.toString(root.rel));
    }
}
