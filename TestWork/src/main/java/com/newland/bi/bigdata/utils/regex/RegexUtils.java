package com.newland.bi.bigdata.utils.regex;


import com.cqx.process.LogInfoFactory;

import java.util.regex.Pattern;

/**
 * 正则表达式工具类
 *
 * @author chenqixu
 */
public class RegexUtils {

    private LogInfoFactory logger = LogInfoFactory.getInstance(RegexUtils.class);

    /**
     * 更新规则
     *
     * @param rule 规则
     * @return
     */
    private String updateRule(String rule) {
        rule = rule.replace('.', '#');
        rule = rule.replaceAll("#", "\\\\.");
        rule = rule.replace('*', '#');
        rule = rule.replaceAll("#", ".*");
        rule = rule.replace('?', '#');
        rule = rule.replaceAll("#", ".?");
        rule = "^" + rule + "$";
        logger.debug("updateRule：{}", rule);
        return rule;
    }

    /**
     * 正则匹配-matches
     *
     * @param rule       规则
     * @param expression 表达式
     * @return 是否匹配
     */
    public boolean match(String rule, String expression) {
        Pattern p = Pattern.compile(rule);
        boolean matches = p.matcher(expression).matches();
        logger.info("rule：{}，expression：{}，matches：{}", rule, expression, matches);
        return matches;
    }

    /**
     * 正则匹配-matches
     *
     * @param rule         规则
     * @param expression   表达式
     * @param isUpdateRule 是否需要转换规则
     * @return
     */
    public boolean match(String rule, String expression, boolean isUpdateRule) {
        if (isUpdateRule)
            rule = updateRule(rule);
        return match(rule, expression);
    }

    /**
     * 正则匹配-find
     *
     * @param rule       规则
     * @param expression 表达式
     * @return 是否匹配
     */
    public boolean find(String rule, String expression) {
        Pattern p = Pattern.compile(rule);
        boolean find = p.matcher(expression).find();
        logger.info("rule：{}，expression：{}，find =：{}", rule, expression, find);
        return find;
    }

    /**
     * 正则匹配-match && find
     *
     * @param rule       规则
     * @param expression 表达式
     * @return 是否匹配
     */
    public boolean matchAndFind(String rule, String expression) {
        return match(rule, expression) && find(rule, expression);
    }

    public static void main(String args[]) {
        RegexUtils regexUtils = new RegexUtils();
        regexUtils.match("[^a-zA-Z0-9]", "123254fasdADFsf12312=-=");
        regexUtils.match("[(1[0-9]{0,1})(,)]", "1,15");
        regexUtils.match("y\\\\\\\\", "i say\\\\i\\\\love\\\\java ");
        regexUtils.match("\\\"", "for my\\\" money\\");
        regexUtils.match("[A-Za-z0-9\\\\-\\\\.]+.jar", "commons-codec-1.9.jar");
        regexUtils.match("lib", "lib");
        regexUtils.match("A0668420170921000001.CHK", "A0668420170921[0-9]{6}.CHK");
        regexUtils.match("LTE_UU_*_20[0-9]{6}(09|10|18|20)[0-9]{4}*.txt.chk", "LTE_UU_012344819008_20181203101100.txt.chk", true);
        regexUtils.match("LTE_UU_*_20[0-9]{6}(?!09|10|18|20)[0-9]{4}*.txt.chk", "LTE_UU_012344819008_20181203101100.txt.chk", true);
        regexUtils.match("LTE_UU_.*_20[0-9]{6}(?!09|10|18|20)[0-9]{4}.*.txt.chk", "LTE_UU_012344819008_20181203101100.txt.chk");
        regexUtils.match("LTE_UU_*_20[0-9]{6}(00|01|02|03|04|05|06|07|08|11|12|13|14|15|16|17|19|21|22|23)[0-9]{4}*.txt.chk", "LTE_UU_012344819008_20181203001100.txt.chk", true);
    }
}
