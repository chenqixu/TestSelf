package com.cqx.calcite.parser;

import com.cqx.calcite.parser.bean.I_clause;
import com.cqx.calcite.parser.bean.operationElement;
import com.cqx.calcite.parser.bean.valElement;
import com.cqx.calcite.parser.operation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * SqlParser
 *
 * @author chenqixu
 */
public class SqlParser {
    private static final Logger logger = LoggerFactory.getLogger(SqlParser.class);
    private Map<String, I_operation> operationMap = new HashMap<>();
    private List<I_operation> operationList = new ArrayList<>();
    private operationElement firstPoint;

    public void parser(String sql) {
        I_operation and = new AND();
        I_operation or = new OR();
        I_operation in = new IN();
        I_operation equal = new EQUAL();
        I_operation not_in = new NOT_IN();
        I_operation not_equal = new NOT_EQUAL();

        operationMap.put(and.getKey(), and);
        operationMap.put(or.getKey(), or);
        operationMap.put(in.getKey(), in);
        operationMap.put(equal.getKey(), equal);
        operationMap.put(not_in.getKey(), not_in);
        operationMap.put(not_equal.getKey(), not_equal);

        operationList.add(and);
        operationList.add(or);
        operationList.add(in);
        operationList.add(equal);
        operationList.add(not_in);
        operationList.add(not_equal);

        //词法分析
        List<String> wordList = lexical_analysis(sql);
        //语法分析
        grammatical_analysis(wordList, "or", null);
        //打印分析的语法树
        logger.info("\n{}", tree_print(firstPoint));

        //1.查询的输出字段(紧接关键字SELECT之后)
        //2.查询的表(紧接关键字FROM之后)
        //3.过滤条件(紧接关键字WHERE之后)

        //query
        //- distinct
        //- select_list
        //- tbl_list
        //- where_clause

        //3、优化
        //4、执行代码生成

    }

    public void parser_v2(String sql) {
        lexical_analysis_v2(sql);
    }

    private List<String> lexical_analysis_v2(String sql) {
        LinkedList<String> linkedList = new LinkedList<>();
        String[] words = sql.trim().split(" ", -1);
        String[] keys = {"(", ")"};
        for (String word : words) {
            logger.info("word：{}", word);
            //判断有没关键字
            for (String key : keys) {
                if (word.contains(key)) {

                    break;
                }
            }
        }
        return null;
    }

    private List<String> lexical_analysis(String sql) {
        //1、词法分析
        logger.info("===step1【词法分析】===");
        String[] words = sql.trim().split(" ", -1);
        List<String> wordList = new ArrayList<>();
        String findKey = "";
        boolean isKey = false;
        boolean isMu = false;
        for (String word : words) {
            logger.info("word：{}", word);
            //关键字匹配
            for (I_operation operation : operationList) {
                //接上一次部分匹配
                if (findKey.length() > 0) {
                    findKey = findKey + word;
                    logger.info("findKey：{}", findKey);
                    //查看是否完全匹配
                    if (operationMap.get(findKey) != null) {
                        logger.info("key add：{}", findKey);
                        wordList.add(findKey);
                        findKey = "";
                        isKey = true;
                        break;
                    }
                }
                //完全匹配
                else if (operation.getKey().equals(word)) {
                    logger.info("key add：{}", word);
                    wordList.add(word);
                    isKey = true;
                    break;
                }
                //部分匹配
                else if (operation.getKey().contains(word) && operation.getKey().length() <= word.length()) {
                    findKey = findKey + word;
                    logger.info("operation.contains：{}，operation：{}，findKey：{}",
                            word, operation.getKey(), findKey);
                    isKey = true;
                    break;
                }
                //有关键字，需要拆分
                else if (word.contains(operation.getKey())) {
                    String[] new_word = word.split(operation.getKey(), -1);
//                    logger.info("new_word：{}", Arrays.asList(new_word));
                    for (int i = 0; i < new_word.length; i++) {
                        if (i % 2 == 1) {
                            logger.info("word add：{}", operation.getKey());
                            wordList.add(operation.getKey());
                            if (new_word[i].trim().length() > 0) {
                                logger.info("word add：{}", new_word[i]);
                                wordList.add(new_word[i]);
                            }
                        } else if (new_word[i].trim().length() > 0) {
                            logger.info("word add：{}", new_word[i]);
                            wordList.add(new_word[i]);
                        }
                    }
                    isMu = true;
                } else {
                    isKey = false;
                }
            }
            if (!isKey && !isMu) {
                logger.info("word add：{}", word);
                wordList.add(word);
            } else if (isMu) {
                isMu = false;
            }
        }
        logger.info("词法分析完成，wordList：{}", wordList);
        return wordList;
    }

    private void grammatical_analysis(List<String> wordList, String key, operationElement parent) {
        //2、语法分析
        logger.info("===step2【语法分析.{}】===", key);
        //拆分到最小粒度，遇到AND、OR就拆分
        operationElement clause = null;
        Iterator<String> it = wordList.iterator();
        List<String> leftList = new ArrayList<>();
        List<String> rightList = new ArrayList<>();
        while (it.hasNext()) {
            String word = it.next();
            logger.info("[grammatical_analysis] word：{}", word);
            //判断
            if (clause == null && word.equals(key)) {
                clause = new operationElement(operationMap.get(word));
                if (parent != null) {
                    clause.setParent(parent);
                } else {
                    firstPoint = clause;
                }
            } else {
                if (clause == null) leftList.add(word);
                else rightList.add(word);
            }
        }
//        logger.info("grammatical_analysis] leftList：{}，clause：{}", leftList, clause);
//        logger.info("grammatical_analysis] rightList：{}", rightList);
        //处理左边
        if (leftList.size() <= 3) {
            //左边拆解完成
            logger.info("[grammatical_analysis] 左边拆解{}完成，{}", key, leftList);
            //处理最小粒度
            operationElement min = processing_minimum_granularity(leftList);
            if (min != null) min.setParent(clause);
        } else {
            //左边可以继续拆
            grammatical_analysis(leftList, "and", clause);
        }
        //处理右边
        if (rightList.size() <= 3) {
            //右边拆解完成
            operationElement endPoint = clause;
            if (endPoint == null) endPoint = parent;
            if (endPoint == null) {
                //刚开始就是最小粒度，没有key
                logger.info("[grammatical_analysis] 右边拆解{}完成，{}", key, rightList);
            } else {
                logger.info("[grammatical_analysis] 右边拆解{}完成，{}", key, rightList);
            }
            //处理最小粒度
            operationElement min = processing_minimum_granularity(rightList);
            if (min != null) min.setParent(clause);
        } else {
            //右边还可以拆解
            grammatical_analysis(rightList, key, clause);
        }
    }

    private operationElement processing_minimum_granularity(List<String> wordList) {
        //处理最小粒度
        operationElement clause = null;
        Iterator<String> it = wordList.iterator();
        while (it.hasNext()) {
            String word = it.next();
            logger.info("[processing_minimum_granularity] word：{}", word);
            //操作符
            if (operationMap.get(word) != null) {
                clause = new operationElement(operationMap.get(word));
                it.remove();
                it = wordList.iterator();
                logger.info("[processing_minimum_granularity] new operationElement：{}", word);
            } else {
                if (clause != null) {
                    clause.addChild(new valElement(word));
                    it.remove();
                    it = wordList.iterator();
                    logger.info("[processing_minimum_granularity] addChild：{}", word);
                }
            }
        }
        return clause;
    }

    private String getTreeByLevel(operationElement oe, int level) {
        StringBuilder print = new StringBuilder();
        List<I_clause> childs = oe.getChilds();
        if (childs != null) {
            while (level > 0) {
                List<I_clause> result = new ArrayList<>();
                for (I_clause child : childs) {
                    result.add(child);
                }
                level--;
                if (level == 0) {

                }
            }
        }
        return print.toString();
    }

    private String tree_print(operationElement oe) {
        StringBuilder print = new StringBuilder();
        if (oe != null) {
            print.append(String.format("      %s\n", oe.getVal().getKey()));
            if (oe.getChilds().size() > 0) print.append("     /  \\\n");
            for (I_clause child : oe.getChilds()) {
                if (child instanceof operationElement) {
                    operationElement op = (operationElement) child;
//                    print.append(op.getVal().getKey());
                    if (op.getChilds().size() > 0) {
                        print.append(tree_print(op));
                    }
                } else {
                    print.append(child.getVal());
                }
                print.append("    ");
            }
        }
        return print.toString();
    }
}
