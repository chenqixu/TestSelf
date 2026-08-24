package com.cqx.common.utils.xml;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * opml工具
 *
 * @author chenqixu
 */
public class OpmlConverter {

    // 生成指定层级的缩进（每层4个空格，兼容Java 8及以下）
    private static String getIndent(int level) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < level; i++) {
            sb.append("    ");
        }
        return sb.toString();
    }

    // XML 特殊字符转义，避免文本含特殊字符导致XML格式错误
    private static String escapeXml(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    // 测试入口
    public static void main(String[] args) {
        List<String> data = Arrays.asList(
                "a1,b1,c1,d1",
                "a1,b1,c1,d2",
                "a1,b1,c2,d3",
                "a1,b2,c3,d4"
        );
        StringBuilder result = new StringBuilder();
        new OpmlConverter().generateOutlineXml(data, 0, result);
        System.out.println(result);
    }

    /**
     * 从字符串列表构建嵌套树
     *
     * @param lines 每行逗号分隔的字符串列表
     * @return 虚拟根节点（不输出，仅用于承载第一层节点）
     */
    public Node buildTree(List<String> lines) {
        Node root = new Node(""); // 虚拟根节点，统一遍历逻辑
        if (lines == null || lines.isEmpty()) {
            return root;
        }

        for (String line : lines) {
            if (line == null || line.trim().isEmpty()) {
                continue; // 跳过空行
            }
            // 按逗号分割，去除首尾空格
            String[] parts = line.split(",");
            Node current = root;
            // 逐层向下遍历/创建节点
            for (String part : parts) {
                current = current.addChildIfAbsent(part.trim());
            }
        }
        return root;
    }

    /**
     * 递归生成带缩进的 outline XML
     *
     * @param data
     * @param level
     * @param sb
     */
    public void generateOutlineXml(List<String> data, int level, StringBuilder sb) {
        generateOutlineXml(buildTree(data), level, sb);
    }

    /**
     * 递归生成带缩进的 outline XML
     *
     * @param node  当前节点
     * @param level 当前层级（控制缩进）
     * @param sb    结果拼接容器
     */
    public void generateOutlineXml(Node node, int level, StringBuilder sb) {
        String indent = getIndent(level);

        // 虚拟根节点不输出标签，直接处理子节点
        if (node.getText().isEmpty()) {
            for (Node child : node.getChildren().values()) {
                generateOutlineXml(child, level, sb);
            }
            return;
        }

        String escapedText = escapeXml(node.getText());

        // 叶子节点：输出自闭合标签
        if (node.getChildren().isEmpty()) {
            sb.append(indent)
                    .append("<outline text=\"").append(escapedText).append("\"></outline>\n");
        }
        // 非叶子节点：输出开始标签 + 子节点 + 结束标签
        else {
            sb.append(indent)
                    .append("<outline text=\"").append(escapedText).append("\">\n");
            for (Node child : node.getChildren().values()) {
                generateOutlineXml(child, level + 1, sb);
            }
            sb.append(indent).append("</outline>\n");
        }
    }

    // 树节点结构：存储当前文本 + 有序子节点映射
    static class Node {
        private final String text;
        // LinkedHashMap 保持插入顺序，避免节点顺序混乱
        private final Map<String, Node> children = new LinkedHashMap<>();

        public Node(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }

        public Map<String, Node> getChildren() {
            return children;
        }

        // 添加子节点：已存在则直接返回已有节点，不存在则新建
        public Node addChildIfAbsent(String childText) {
            return children.computeIfAbsent(childText, Node::new);
        }
    }
}
