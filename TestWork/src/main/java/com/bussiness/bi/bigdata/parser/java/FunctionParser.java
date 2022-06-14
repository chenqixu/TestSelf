package com.bussiness.bi.bigdata.parser.java;

import com.cqx.common.utils.file.FileResult;
import com.cqx.common.utils.file.FileUtil;
import com.cqx.common.utils.log.MyLogger;
import com.cqx.common.utils.log.MyLoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * FunctionParser
 *
 * @author chenqixu
 */
public class FunctionParser {
    private static final MyLogger logger = MyLoggerFactory.getLogger(FunctionParser.class);
    private String filename;

    public FunctionParser(String filename) {
        this.filename = filename;
    }

    public void run() throws Exception {
        FileUtil fileUtil = new FileUtil();
        fileUtil.setReader(filename);
        //读取文件
        FileResult<FunctionParserBean> fileResult = new FileResult<FunctionParserBean>() {
            @Override
            public void run(String content) throws IOException {
                //访问修饰符 返回结果 方法名称(参数类型 参数名称...)抛出异常{
                //解析每一行，找出符合上面条件的内容
                //先按左括号分隔，[1]访问修饰符 返回结果 方法名称 [2]参数类型 参数名称...)抛出异常{
                String[] step1_arr = content.split("\\(", -1);
                //步骤1需要符合长度2
                if (step1_arr.length == 2) {
                    //步骤1结果1按空格分隔，[1]访问修饰符 返回结果 方法名称
                    String[] step2_arr = step1_arr[0].trim().split(" ", -1);
                    //步骤1结果1需要符合长度3
                    if (step2_arr.length == 3) {
                        //开头必须符合访问修饰符
                        if (isAccessModifiers(step2_arr[0])) {
                            FunctionParserBean functionParserBean = new FunctionParserBean();
                            functionParserBean.setAccess_modifiers(step2_arr[0]);
                            functionParserBean.setFunction_result(step2_arr[1]);
                            functionParserBean.setFunction_name(step2_arr[2]);
                            //步骤1结果2，[2]参数类型 参数名称...)抛出异常{
                            //步骤1结果2按右括号分隔，[1]参数类型 参数名称... [2]抛出异常{
                            String[] step3_arr = step1_arr[1].trim().split("\\)", -1);
                            functionParserBean.setFunction_param(step3_arr[0]);
                            //步骤3按逗号分隔，去左右空格后，再按空格分隔，如果是单数则需要向前进一位
                            List<String> paramTypes = paramsParser(step3_arr[0]);
                            functionParserBean.setFunction_param_type(paramTypes);
                            addFileresult(functionParserBean);
                        }
                    }
                }
            }
        };
        fileUtil.read(fileResult);
        //截留方法
        List<FunctionParserBean> functionParserBeanList = fileResult.getFileresult();
        for (FunctionParserBean functionParserBean : functionParserBeanList) {
            if (functionParserBean.getFunction_name().toLowerCase().contains("query"))
                logger.info("{}({}) {}",
                        functionParserBean.getFunction_name(),
                        functionParserBean.getFunction_param(),
                        functionParserBean.getFunction_result());
        }
    }

    private boolean isAccessModifiers(String msg) {
        if (msg != null) {
            return msg.contains("public") || msg.contains("private") || msg.contains("protected");
        }
        return false;
    }

    private List<String> paramsParser(String msg) {
        List<String> result_list = new ArrayList<>();
        if (msg != null && msg.trim().length() > 0) {
            //按逗号分隔，去左右空格后，再按空格分隔，如果是单数则需要向前进一位
            String[] step1_arr = msg.trim().split(",", -1);
            Iterator<String> iterator = Arrays.asList(step1_arr).iterator();
            while (iterator.hasNext()) {
                String value = iterator.next().trim();
                String[] arr = value.split(" ", -1);
                String type = arr[0];
                if (arr.length != 2) {
                    //把后面的拼接上来
                    String next_value = iterator.next().trim();
                    String[] arr1 = next_value.split(" ", -1);
                    type = value + ", " + arr1[0];
                }
                result_list.add(type);
            }
        }
        return result_list;
    }
}
