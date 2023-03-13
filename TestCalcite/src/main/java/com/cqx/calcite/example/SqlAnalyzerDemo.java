package com.cqx.calcite.example;

import com.cqx.common.utils.jdbc.JDBCUtil;
import org.apache.calcite.config.Lex;
import org.apache.calcite.plan.RelOptUtil;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.RelRoot;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.sql.*;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql.test.SqlTestFactory;
import org.apache.calcite.sql.test.SqlTester;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.calcite.test.SqlToRelTestBase;
import org.apache.calcite.test.catalog.MockCatalogReader;
import org.apache.calcite.test.catalog.MockCatalogReaderSimple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 血缘分析
 *
 * @author chenqixu
 */
public class SqlAnalyzerDemo extends SqlToRelTestBase {
    private static final Logger logger = LoggerFactory.getLogger(SqlAnalyzerDemo.class);
    protected SqlTester tester;
    private JDBCUtil jdbcUtil;
    private Map<String, String> map = new HashMap<>();
    private AtomicInteger seq = new AtomicInteger(0);
    private List<TableBean> tableBeanList = new ArrayList<>();
    private Map<String, TableBean> mapTableBean = new HashMap<>();

    public SqlAnalyzerDemo() {
        this.tester = getTester();
    }

    public SqlTester getTester() {
        return new MySqlValidatorTester(SqlTestFactory.INSTANCE);
    }

    public void testFieldOrigin(String sql) {
        tester.checkFieldOrigin(sql, "");
    }

    public void analyzerV1(String sql) throws SqlParseException {
        // 解析
        SqlParser.Config config = SqlParser.configBuilder().setLex(Lex.ORACLE).build();
        SqlParser parser = SqlParser.create(sql, config);
        SqlNode node = parser.parseStmt();
        switch (node.getKind()) {
            case SELECT:
                parserSelect(node);
                break;
            case INSERT:
                parserInsert(node);
                break;
            case MERGE:
                parserMerge(node);
                break;
            default:
                logger.info("[analyzerV1 Other] kind={}, class={}", node.getKind(), node.getClass());
                break;
        }
        logger.info("map={}", map);
        logger.info("tableBeanList={}", tableBeanList);
    }

    public void analyzerV2(String sql) throws SqlParseException {
        // 解析
        SqlParser.Config config = SqlParser.configBuilder().setLex(Lex.ORACLE).build();
        SqlParser parser = SqlParser.create(sql, config);
        SqlNode node = parser.parseStmt();
        parserSqlNodeV2(node, null);
        logger.info("map={}", map);
        logger.info("===tableBeanList===");
        for (TableBean tableBean : tableBeanList) {
            logger.info("tableBean={}", tableBean);
        }
//        logger.info("===血缘查询===");
//        String findField = "name_request_source".toUpperCase();
//        logger.info("find={}", findField);
//        for (TableBean tableBean : tableBeanList) {
//            if (tableBean.getTableLevel() == 0) {
//                for (FieldBean fieldBean : tableBean.getFieldBeanList()) {
//                    if (fieldBean.getAlias().contains(findField)) {
//                        logger.info("name_request_source={}", fieldBean.getFromList());
//                        for (FieldBean from : fieldBean.getFromList()) {
//                            String _findField = from.getFieldName();
//                            TableBean _tableBean = mapTableBean.get(from.getSourceTableAlias());
//                            if (_tableBean != null) {
//                                for (FieldBean _from : _tableBean.getFieldBeanList()) {
//                                    if (_from.getAlias().contains(_findField)) {
//                                        logger.info("_from.SourceTableAlias={}, _from.FieldName={}, _from.Alias={}, _from.FromList={}"
//                                                , _from.getSourceTableAlias(), _from.getFieldName(), _from.getAlias(), _from.getFromList());
//                                        break;
//                                    }
//                                }
//                            } else {
//                                logger.info("from={}", from);
//                            }
//                        }
//                    }
//                }
//            }
//        }
    }

    private void parserSqlNodeV2(SqlNode sqlNode, TableBean parentBean) {
        logger.info("[parserSqlNodeV2] kind={}, kindClass={}", sqlNode.getKind(), sqlNode.getClass());
        switch (sqlNode.getKind()) {
            case INSERT:
                SqlInsert sqlInsert = (SqlInsert) sqlNode;
                // 目标 targetTable
                String targetTableName = sqlInsert.getTargetTable().toString();
                TableBean targetTable = createTableBean(targetTableName);
                targetTable.setTableType(EnumTableType.INSERT);
                // 设置级别
                if (parentBean == null) {// 根目录
                    targetTable.setTableLevel(0);
                } else {
                    targetTable.setTableLevel(parentBean.getTableLevel() + 1);
                }
                // 目标字段 columnList
                for (SqlNode targetColumn : sqlInsert.getTargetColumnList()) {
                    targetTable.addField(targetColumn);
                }
                // 来源 source
                parserSqlNodeV2(sqlInsert.getSource(), targetTable);
                break;
            case SELECT:
                // 查询校验，查询不能为父级
                if (parentBean == null) throw new NullPointerException("查询不能为父级!");
                SqlSelect sqlSelect = (SqlSelect) sqlNode;
                // 创建查询表
                String _selectTableName = "_select_" + seq.incrementAndGet();
                TableBean selectTable = createTableBean(_selectTableName);
                selectTable.setTableType(EnumTableType.SELECT);
                // 设置级别
                selectTable.setTableLevel(parentBean.getTableLevel() + 1);
                // 是否补充父表字段
                boolean isSupplementaryParentField = (parentBean.getFieldBeanList().size() == 0);
                // 查询字段 selectList
                for (int i = 0; i < sqlSelect.getSelectList().size(); i++) {
                    FieldBean addFieldBean = selectTable.addField(sqlSelect.getSelectList().get(i));
                    // 父表可能没有字段，需要按需补充
                    if (isSupplementaryParentField) {
                        parentBean.addField(addFieldBean.getAlias(), addFieldBean.getAlias(), _selectTableName);
                    }
                    // 按顺序给父表添加来源
                    parentBean.getFieldBeanList().get(i).addFrom(addFieldBean);
                }
                // 来源 from, 可能是JOIN(left 和 right), 也可以是AS
                parserSqlNodeV2(sqlSelect.getFrom(), selectTable);
                break;
            case JOIN:
                // JOIN分为 left 和 right
                // left是主表, right是关联表
                // 主表可能是 join, 分为 left 和 right
                // left 和 right 也可能是AS，然后再分 select
                SqlJoin sqlJoin = (SqlJoin) sqlNode;
                // 左表是主表
                parserSqlNodeV2(sqlJoin.getLeft(), parentBean);
                // 右表是新表
                String _rightSelectTableName = "_right_select_" + seq.incrementAndGet();
                TableBean rightSelectTable = createTableBean(_rightSelectTableName);
                rightSelectTable.setTableType(EnumTableType.SELECT);
                // 设置级别
                rightSelectTable.setTableLevel(parentBean.getTableLevel());
                parserSqlNodeV2(sqlJoin.getRight(), rightSelectTable);
                break;
            case UNION:
                // 可能是多个select的组合
                SqlBasicCall union = (SqlBasicCall) sqlNode;
                for (SqlNode operator : union.getOperandList()) {
                    parserSqlNodeV2(operator, parentBean);
                }
                break;
            case AS:
                // 先判断有没别名
                SqlBasicCall as = (SqlBasicCall) sqlNode;
                int operandSize = as.getOperandList().size();
                if (operandSize == 1) {// 没有别名
                    String AS_tableName = as.getOperandList().get(0).toString();
                    parentBean.setTableName(AS_tableName);
                } else if (operandSize == 2) {// 有别名
                    // AS的第一个操作可能是SELECT、UNION
                    SqlNode firstSqlNode = as.getOperandList().get(0);
                    SqlNode secondSqlNode = as.getOperandList().get(1);
                    String AS_alias = secondSqlNode.toString();
                    parentBean.setAlias(AS_alias);
                    switch (firstSqlNode.getKind()) {
                        case SELECT:
                        case UNION:
                            parserSqlNodeV2(firstSqlNode, parentBean);
                            break;
                        default:
                            String AS_tableName = firstSqlNode.toString();
                            parentBean.setTableName(AS_tableName);
                            // 把属于自己的字段的别名换成自己
                            for (FieldBean fieldBean : parentBean.getFieldBeanList()) {
                                if (fieldBean.getSourceTableAlias() != null
                                        && fieldBean.getSourceTableAlias().contains(AS_alias)) {
                                    fieldBean.setSourceTableAlias(AS_tableName);
                                }
                            }
                            break;
                    }
                } else {
                    throw new RuntimeException("[parserFrom] AS操作对象大于2个！");
                }
                break;
            case IDENTIFIER:
                // 到这一步已经没有别名，只有表名了
                SqlIdentifier sqlIdentifier = (SqlIdentifier) sqlNode;
                String IDENTIFIER_tableName = sqlIdentifier.toString();
                parentBean.setTableName(IDENTIFIER_tableName);
                break;
        }
    }

    private String parserSqlNode(SqlNode sqlNode) {
//        logger.info("[parserSqlNode] kind={}, kindClass={}", sqlNode.getKind(), sqlNode.getClass());
        switch (sqlNode.getKind()) {
            case IDENTIFIER:
            case LITERAL:
                return sqlNode.toString();
            case AS:
                // 别名，分为name和alias
                // 要确认name是否还能分解
                SqlBasicCall as = (SqlBasicCall) sqlNode;
                if (as.getOperandList().size() == 2) {
                    // name
                    SqlNode name = as.operand(0);
                    // alias
                    SqlNode alias = as.operand(1);
                    return name.toString();
                } else if (as.getOperandList().size() == 1) {
                    // 没有别名的情况
                    // name
                    SqlNode name = as.operand(0);
                    return name.toString();
                } else {
                    throw new RuntimeException("[parserSqlNode] AS操作对象大于2个！");
                }
            default:
                throw new RuntimeException(String.format("[parserSqlNode] 不支持的[%s]解析操作！", sqlNode.getKind()));
        }
    }

    private String parserFrom(SqlNode sqlNode) {
        logger.info("[parserFrom] kind={}, kindClass={}", sqlNode.getKind(), sqlNode.getClass());
        switch (sqlNode.getKind()) {
            case IDENTIFIER:
                // 没有别名的JOIN.FROM也会走到这里
                logger.info("[parserFrom IDENTIFIER] {}", sqlNode.toString());
                createTableBean(sqlNode.toString());
                return sqlNode.toString();
            case AS:
                // 别名，分为name和alias
                // 要确认name是否还能分解
                SqlBasicCall as = (SqlBasicCall) sqlNode;
//                StringBuilder sb = new StringBuilder();
//                for (SqlNode operand : as.getOperandList()) {
//                    sb.append(parserFrom(operand)).append("#####");
//                }
//                return sb.toString();
                if (as.getOperandList().size() == 2) {
                    // name
                    SqlNode name = as.operand(0);
                    // alias
                    SqlNode alias = as.operand(1);
                    logger.info("[parserFrom AS] name.kind={}, name.kindClass={}", name.getKind(), name.getClass());
                    switch (name.getKind()) {
                        case UNION:
                            String tableName = "union_" + seq.incrementAndGet();
                            createTableBean(tableName, alias.toString());
                            // 解析
                            parserFrom(name);
                            return tableName;
                        case IDENTIFIER:
                            logger.info("[parserFrom AS] name={}, alias={}", name.toString(), alias.toString());
                            createTableBean(name.toString(), alias.toString());
                            return name.toString();
                        case AS:
                        case SELECT:
                            return parserFrom(name);
                        default:
                            throw new RuntimeException(String.format("[parserFrom] AS.name[%s]类型无法识别！不属于[SqlBasicCall,SqlIdentifier]", name.getClass()));
                    }
                } else if (as.getOperandList().size() == 1) {
                    // 没有别名的情况
                    // name
                    SqlNode name = as.operand(0);
                    if (name instanceof SqlIdentifier) {
                        logger.info("[parserFrom AS] name={}", name.toString());
                        return name.toString();
                    } else {
                        throw new RuntimeException(String.format("[parserFrom] AS.name[%s]类型无法识别！不属于[SqlIdentifier]", name.getClass()));
                    }
                } else {
                    throw new RuntimeException("[parserFrom] AS操作对象大于2个！");
                }
            case JOIN:
                // 关联分为左边和右边
                SqlJoin join = (SqlJoin) sqlNode;
                // 判断是直接到表名还是AS、UNION、SELECT操作
                // 先以AS为主，然后确认AS下面到底是UNION还是SELECT

                // 左表
                String leftName = parserFrom(join.getLeft());
                // 右表
                String rightName = parserFrom(join.getRight());
                return leftName;
            case UNION:
                parserUnion(sqlNode);
                return "union#####";
            case SELECT:
                return parserSelect(sqlNode).getTableName();
            default:
                throw new RuntimeException(String.format("[parserFrom] 不支持的[%s]解析操作！", sqlNode.getKind()));
        }
    }

    private void parserMerge(SqlNode node) {
        SqlMerge sqlMerge = (SqlMerge) node;
        //目标表
        sqlMerge.getTargetTable();
        //条件
        sqlMerge.getCondition();
        //源
        sqlMerge.getSourceTableRef();
        //插入操作
        SqlNode insert = sqlMerge.getInsertCall();
        parserInsert(insert);
        //更新操作
        SqlNode update = sqlMerge.getUpdateCall();
        parserUpdate(update);
    }

    private void parserInsert(SqlNode node) {
        SqlInsert sqlInsert = (SqlInsert) node;
        System.out.println("=======source=======");
        SqlNode source = sqlInsert.getSource();
        switch (source.getKind()) {
            case UNION:
                parserUnion(source);
                break;
            case SELECT:
                parserSelect(source);
                break;
            case VALUES:
                parserValues(source);
                break;
            default:
                logger.info("[Insert Other] kind={}, class={}", node.getKind(), node.getClass());
                break;
        }
        System.out.println("=======target=======");
        SqlNode targetTable = sqlInsert.getTargetTable();
        logger.info("[Insert targetTable] {}", targetTable);
        TableBean tableBean = createTableBean(targetTable.toString());
        SqlNodeList targetColumnList = sqlInsert.getTargetColumnList();
        if (targetColumnList != null) {
            for (SqlNode targetColumn : targetColumnList) {
                logger.info("[Insert targetColumn] {}", targetColumn);
                tableBean.addField(targetColumn.toString());
            }
        }
    }

    private TableBean createTableBean(String tableName) {
        return createTableBean(tableName, tableName);
    }

    private TableBean createTableBean(String tableName, String alias) {
        TableBean tableBean = new TableBean(tableName, alias);
        tableBeanList.add(tableBean);
        mapTableBean.put(tableBean.getAlias(), tableBean);
        map.put(alias, tableName);
        return tableBean;
    }

    private void modifyTableName(String alias, String tableName) {
        map.put(alias, tableName);
    }

    private void modifyTableAlias(String oldAlias, TableBean tableBean) {
        mapTableBean.remove(oldAlias);
        map.remove(oldAlias);
        mapTableBean.put(tableBean.getAlias(), tableBean);
        map.put(tableBean.getAlias(), tableBean.getTableName());
    }

    private void parserUpdate(SqlNode node) {
        SqlUpdate sqlUpdate = (SqlUpdate) node;
        System.out.println("=======target=======");
        SqlNode targetTable = sqlUpdate.getTargetTable();
        System.out.println("targetTable：" + targetTable);
        SqlNodeList targetColumnList = sqlUpdate.getTargetColumnList();
        if (targetColumnList != null)
            for (SqlNode targetColumn : targetColumnList) {
                System.out.println("targetColumn：" + targetColumn);
            }
        SqlNodeList sourceExpressionList = sqlUpdate.getSourceExpressionList();
        if (sourceExpressionList != null)
            for (SqlNode sourceExpression : sourceExpressionList) {
                System.out.println("sourceExpression：" + sourceExpression);
            }
        System.out.println("=======condition=======");
        SqlNode condition = sqlUpdate.getCondition();
        parserCondition(condition);
    }

    private void parserCondition(SqlNode node) {
        throw new UnsupportedOperationException("不支持的操作！");
    }

    private void parserUnion(SqlNode node) {
        SqlBasicCall sqlBasicCall = (SqlBasicCall) node;
        for (SqlNode sqlNode : sqlBasicCall.getOperandList()) {
            parserSelect(sqlNode);
        }
    }

    private TableBean parserSelect(SqlNode node) {
        SqlSelect sqlSelect = (SqlSelect) node;

        // query fields
        for (SqlNode selectNode : sqlSelect.getSelectList()) {
//            logger.info("[Select query fields] node={}, nodeClass={}", selectNode, selectNode.getClass());
            String field = parserSqlNode(selectNode);
            logger.info("[Select query field] field={}", field);
        }

        // from
        SqlNode sqlFrom = sqlSelect.getFrom();
//        logger.info("[Select getFrom] kind={}", sqlFrom.getKind());
        // 可以是AS，也可以是JOIN，如果是IDENTIFIER就是表名
        String fromName = parserFrom(sqlFrom);
        logger.info("[Select parserFrom] fromName={}", fromName);
        return createTableBean(fromName);
    }

    private void parserValues(SqlNode node) {
//        SqlBasicCall sqlBasicCall = (SqlBasicCall) node;
//        for (SqlNode sqlNode : sqlBasicCall.getOperandList()) {
//        }
        throw new UnsupportedOperationException("不支持的操作！");
    }

    public void analyzer(String sql) {
        Tester tester = createTester();
        SqlTestFactory.MockCatalogReaderFactory factory = (typeFactory, caseSensitive) -> {
            CompositeKeysCatalogReader catalogReader =
                    new CompositeKeysCatalogReader(typeFactory, false);
            catalogReader.init();
            return catalogReader;
        };
        Tester newTester = tester.withCatalogReaderFactory(factory);
        RelRoot relRoot = newTester.convertSqlToRel(sql);
        RelNode relNode = relRoot.project();
        System.out.print(RelOptUtil.toString(relNode));
    }

    enum EnumTableType {
        INSERT, SELECT, JOIN, UNION;
    }

    private class CompositeKeysCatalogReader extends MockCatalogReaderSimple {
        CompositeKeysCatalogReader(RelDataTypeFactory typeFactory, boolean caseSensitive) {
            super(typeFactory, caseSensitive);
        }

        public MockCatalogReader init() {
            super.init();
            MockSchema tSchema = new MockSchema("nmc_app_nl_loc_mix_v1");
            this.registerSchema(tSchema);
            MockTable t1 = MockTable.create(this, tSchema, "nmc_app_nl_loc_mix_v1", false, 7.0D, (ColumnResolver) null);
            t1.addColumn("key1", this.typeFactory.createSqlType(SqlTypeName.VARCHAR), true);
            t1.addColumn("key2", this.typeFactory.createSqlType(SqlTypeName.VARCHAR), true);
            t1.addColumn("value1", this.typeFactory.createSqlType(SqlTypeName.INTEGER));
            this.registerTable(t1);
            return this;
        }
    }

    class TableBean {
        String tableName;
        String alias;
        EnumTableType tableType;
        int tableLevel;
        List<FieldBean> fieldBeanList = new ArrayList<>();

        TableBean(String tableName, String alias) {
            this.tableName = tableName;
            this.alias = alias;
        }

        TableBean(String tableName) {
            this(tableName, tableName);
        }

        @Override
        public String toString() {
            return String.format("tableName=%s, alias=%s, tableType=%s, tableLevel=%s, fieldBeanList=%s", tableName, alias, tableType, tableLevel, fieldBeanList);
        }

        public FieldBean addField(String fieldName) {
            FieldBean fieldBean = new FieldBean(fieldName);
            this.fieldBeanList.add(fieldBean);
            return fieldBean;
        }

        public FieldBean addField(String fieldName, String alias, String sourceTableAlias) {
            FieldBean fieldBean = new FieldBean(fieldName, alias, sourceTableAlias);
            this.fieldBeanList.add(fieldBean);
            return fieldBean;
        }

        public FieldBean addField(SqlNode sqlNode) {
            FieldBean fieldBean = new FieldBean(sqlNode, getTableName());
            this.fieldBeanList.add(fieldBean);
            return fieldBean;
        }

        public String getTableName() {
            return tableName;
        }

        public void setTableName(String tableName) {
            this.tableName = tableName;
            modifyTableName(getAlias(), tableName);
        }

        public String getAlias() {
            return alias;
        }

        public void setAlias(String alias) {
            String oldAlias = getAlias();
            this.alias = alias;
            modifyTableAlias(oldAlias, this);
        }

        public List<FieldBean> getFieldBeanList() {
            return fieldBeanList;
        }

        public void setFieldBeanList(List<FieldBean> fieldBeanList) {
            this.fieldBeanList = fieldBeanList;
        }

        public EnumTableType getTableType() {
            return tableType;
        }

        public void setTableType(EnumTableType tableType) {
            this.tableType = tableType;
        }

        public int getTableLevel() {
            return tableLevel;
        }

        public void setTableLevel(int tableLevel) {
            this.tableLevel = tableLevel;
        }
    }

    class FieldBean {
        String sourceTableAlias;
        String fieldName;
        String alias;
        List<FieldBean> fromList = new ArrayList<>();

        FieldBean(SqlNode sqlNode) {
            this(sqlNode, null);
        }

        FieldBean(SqlNode sqlNode, String _sourceTableAlias) {
            String val;
            switch (sqlNode.getKind()) {
                case IDENTIFIER:
                    val = sqlNode.toString();
                    if (val.contains(".")) {
                        String[] vals = val.split("\\.", -1);
                        this.sourceTableAlias = vals[0];
                        this.fieldName = vals[1];
                    } else {
                        this.sourceTableAlias = _sourceTableAlias;
                        this.fieldName = val;
                    }
                    this.alias = this.fieldName;
                    break;
                case AS:
                    SqlBasicCall as = (SqlBasicCall) sqlNode;
                    val = as.getOperandList().get(0).toString();
                    if (val.contains(".")) {
                        String[] vals = val.split("\\.", -1);
                        this.sourceTableAlias = vals[0];
                        this.fieldName = vals[1];
                    } else {
                        this.sourceTableAlias = _sourceTableAlias;
                        this.fieldName = val;
                    }
                    this.alias = as.getOperandList().get(1).toString();
                    break;
                default:
                    this.fieldName = sqlNode.toString();
                    this.alias = fieldName;
                    this.sourceTableAlias = _sourceTableAlias;
                    break;
            }
        }

        FieldBean(String fieldName) {
            this(fieldName, fieldName);
        }

        FieldBean(String fieldName, String alias) {
            this.fieldName = fieldName;
            this.alias = alias;
        }

        FieldBean(String fieldName, String alias, String sourceTableAlias) {
            this.fieldName = fieldName;
            this.alias = alias;
            this.sourceTableAlias = sourceTableAlias;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("fieldName=").append(fieldName).append(", ");
            if (sourceTableAlias != null) {
                sb.append("sourceTableAlias=").append(sourceTableAlias).append(", ");
            }
            if (alias != null) {
                sb.append("alias=").append(alias).append(", ");
            }
            if (fromList.size() > 0) {
                sb.append("fromList=").append(fromList);
            }
            return sb.toString();
        }

        public FieldBean addFrom(FieldBean from) {
            fromList.add(from);
            return this;
        }

        public String getFieldName() {
            return fieldName;
        }

        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }

        public String getSourceTableAlias() {
            return sourceTableAlias;
        }

        public void setSourceTableAlias(String sourceTableAlias) {
            this.sourceTableAlias = sourceTableAlias;
        }

        public String getAlias() {
            return alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }

        public List<FieldBean> getFromList() {
            return fromList;
        }

        public void setFromList(List<FieldBean> fromList) {
            this.fromList = fromList;
        }
    }
}
