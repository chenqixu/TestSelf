package com.bussiness.bi.bigdata.label.nm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * MongoDB 查询转 AST
 *
 * @author chenqixu
 */
public class MongoAstParser {
//    // AST 节点基类
//    abstract static class AstNode {
//        String type;
//        abstract Object evaluate(Document doc); // 节点求值方法
//    }
//
//    // 逻辑操作符节点 (AND/OR/NOT)
//    static class LogicalNode extends AstNode {
//        List<AstNode> children = new ArrayList<>();
//
//        LogicalNode(String operator) {
//            this.type = operator;
//        }
//    }
//
//    // 比较操作符节点 (GT/GTE/LT/LTE/NE/EQ)
//    static class ComparisonNode extends AstNode {
//        String field;
//        Object value;
//
//        ComparisonNode(String operator, String field, Object value) {
//            this.type = operator;
//            this.field = field;
//            this.value = value;
//        }
//    }
//
//    // 主解析方法
//    public static AstNode parseQuery(Document query) {
//        // 处理顶层 AND 逻辑（MongoDB 隐式AND）
//        LogicalNode root = new LogicalNode("AND");
//
//        // 遍历查询的每个字段
//        for (Map.Entry<String, Object> entry : query.entrySet()) {
//            String key = entry.getKey();
//            Object value = entry.getValue();
//
//            // 1. 解析逻辑操作符 ($and, $or)
//            if (key.startsWith("$")) {
//                root.children.add(parseOperator(key, value));
//            }
//            // 2. 解析字段条件
//            else {
//                // 字段条件可能是直接值或嵌套操作符
//                if (value instanceof Document) {
//                    Document conditions = (Document) value;
//                    for (Map.Entry<String, Object> cond : conditions.entrySet()) {
//                        root.children.add(
//                                parseOperator(cond.getKey(), cond.getValue(), key)
//                        );
//                    }
//                } else {
//                    // 直接等值查询 {field: value}
//                    root.children.add(
//                            new ComparisonNode("EQ", key, value)
//                    );
//                }
//            }
//        }
//
//        return root.children.size() == 1 ? root.children.get(0) : root;
//    }
//
//    // 解析操作符
//    private static AstNode parseOperator(String operator, Object value) {
//        return parseOperator(operator, value, null);
//    }
//
//    private static AstNode parseOperator(String operator, Object value, String field) {
//        // 解析逻辑运算符
//        if ("$and".equals(operator) || "$or".equals(operator)) {
//            LogicalNode node = new LogicalNode(operator.substring(1).toUpperCase());
//            for (Document subQuery : (List<Document>) value) {
//                node.children.add(parseQuery(subQuery));
//            }
//            return node;
//        }
//        // 解析比较运算符
//        else if (operator.startsWith("$")) {
//            return new ComparisonNode(
//                    operator.substring(1).toUpperCase(),
//                    field,
//                    value
//            );
//        }
//        // 其他操作符（如 $elemMatch, $geoWithin）
//        else {
//            // 特殊操作符处理...
//            return new CustomOperatorNode(operator, field, value);
//        }
//    }
}
