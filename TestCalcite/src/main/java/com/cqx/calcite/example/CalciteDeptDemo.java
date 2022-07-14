package com.cqx.calcite.example;

import com.cqx.calcite.bean.HrSchema;
import org.apache.calcite.adapter.java.ReflectiveSchema;
import org.apache.calcite.jdbc.CalciteConnection;
import org.apache.calcite.plan.RelOptUtil;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.RelRoot;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.schema.impl.AbstractTable;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.calcite.tools.FrameworkConfig;
import org.apache.calcite.tools.Frameworks;
import org.apache.calcite.tools.Planner;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * calcite 部门、人员 示例
 *
 * @author chenqixu
 */
public class CalciteDeptDemo extends AbstractCalciteDemo {

    private static final String SQL1 = "select d.deptno, min(e.empid) as min_empid\n"
            + "from hr.emps as e\n"
            + "join hr.depts as d\n"
            + "  on e.deptno = d.deptno\n"
            + "group by d.deptno\n"
            + "having count(*) > 1";

    public static void main(String[] args) throws Exception {
        CalciteDeptDemo calciteDeptDemo = new CalciteDeptDemo();
        calciteDeptDemo.init();
        calciteDeptDemo.queryBySQL(SQL1);
        calciteDeptDemo.parser();
        calciteDeptDemo.abstractTable();
    }

    public void queryBySQL(String sql) throws Exception {
        ResultSet resultSet = null;
        Statement statement = null;
        try (Connection connection = getConn()) {
            // 把表定义写入根路径下的指定模式
            addSchema(connection, "hr", new HrSchema());
            // 创建statement
            statement = connection.createStatement();
            // 执行查询
            resultSet = statement.executeQuery(sql);
            // 结果打印
            getResultSet(resultSet);
        } finally {
            // 资源释放
            closeRS(resultSet, statement);
        }
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
