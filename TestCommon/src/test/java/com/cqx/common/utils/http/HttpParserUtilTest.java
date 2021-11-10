package com.cqx.common.utils.http;

import com.cqx.common.utils.file.FileUtil;
import com.cqx.common.utils.list.ListHelper;
import org.jsoup.nodes.Element;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class HttpParserUtilTest {
    private static final Logger logger = LoggerFactory.getLogger(HttpParserUtilTest.class);

    @Test
    public void parserJsoupOrg() throws IOException {
        new HttpParserUtil().parser("https://jsoup.org/"
                , ".nav-sections li"
                , ListHelper.getInstance(String.class).add("class").get());
    }

    @Test
    public void parserFusionInsightManagerAPI() throws IOException {
        Map<Integer, APIBean> apiBeanMap = new HashMap<>();
        new HttpParserUtil().parser("file:///D:/tmp/chm/html/FusionInsightAPI8.0.0.html"
                , 5000
                , ".sect2"
                , ListHelper.getInstance(String.class).add("h3").add("th").add("td").get()
                , null
                , new AbstractHttpParserUtilDeal() {
                    @Override
                    public void deal(Element parent, Element child, String childCcsQuery, boolean isEnd) {
                        APIBean apiBean = apiBeanMap.get(parent.siblingIndex());
                        if (apiBean == null && childCcsQuery.equals("h3")) {
                            String className = child.text();
                            if (className.startsWith("3.")) {
                                apiBean = new APIBean();
                                apiBean.setClassNameBySplit(className);
                                apiBeanMap.put(parent.siblingIndex(), apiBean);
                                logger.debug("className：{}", className);
                            }
                        } else if (apiBean != null && childCcsQuery.equals("th")) {
                            apiBean.thCount();
                        } else if (apiBean != null && childCcsQuery.equals("td")) {
                            apiBean.addAPIBeanStruct(child.text());
                        }
                    }
                }
        );
        logger.info("first count：{}", apiBeanMap.size());
        // < xxx > array 换成 List< xxx >
        // enum (ENABLE, DISABLE)，加类名加字段名加枚举常量，变成枚举
        // integer (int32) 变成 int
        // integer (int64) 变成 long
        // boolean 不变
        // < string, string > map 变成 Map< String, String >
        // string 变成 String
        // object 变成 Object
        // 先替换，再找出 enum，最后处理 array 和 map
        // default 变成 Default
        Map<String, APIBean> beanMap = new HashMap<>();
        Map<String, APIBean> enumMap = new HashMap<>();
        for (APIBean apiBean : apiBeanMap.values()) {
            for (APIBeanStruct apiBeanStruct : apiBean.getApiBeanStructList()) {
                String _fieldType = apiBeanStruct.getFieldType();
                // 类型处理
                apiBeanStruct.setFieldType(replaceVal(_fieldType));
                // 剥离出enum
                APIBean apiEnum = getEnum(apiBean.getClassName(), apiBeanStruct);
                if (apiEnum != null) {
                    // 使用枚举内容进行匹配，防止枚举内容一致，都统一成大写
                    APIBean getApiEnum = enumMap.get(apiEnum.getTmpEnumContent().toUpperCase());
                    if (getApiEnum == null) {
                        getApiEnum = apiEnum;
                        beanMap.put(apiEnum.getClassName(), apiEnum);
                        enumMap.put(apiEnum.getTmpEnumContent().toUpperCase(), apiEnum);
                    }
                    String _lastFieldType = apiBeanStruct.getFieldType();
                    _lastFieldType = _lastFieldType.replace(apiEnum.getTmpEnum(), getApiEnum.getClassName());
                    apiBeanStruct.setFieldType(_lastFieldType);
                }
            }
            beanMap.put(apiBean.getClassName(), apiBean);
        }
        //
        logger.info("last count：{}", beanMap.size());
        FileUtil fileUtil = new FileUtil();
        String path = "D:\\Document\\Workspaces\\Git\\FujianBI\\edc-rsmgr\\edc-resource-manage-service\\src\\main\\java\\com\\newland\\bi\\resourcemanage\\model\\huawei\\";
        for (APIBean apiBean : beanMap.values()) {
            try {
                fileUtil.createFile(path + apiBean.getClassName() + ".java");
                fileUtil.write(generateClass(apiBean));
            } finally {
                fileUtil.closeWrite();
            }
        }
    }

    @Test
    public void replaceTest() {
        String val = "< string, < APIHost > array > map";
        logger.info("{}", replaceVal(val));
    }

    @Test
    public void classTest() {
        APIBean apiBean = new APIBean();
        apiBean.setClassName("ClassTest1");
        apiBean.thCount();
        apiBean.thCount();
        apiBean.thCount();
        apiBean.addAPIBeanStruct("name");
        apiBean.addAPIBeanStruct("名字");
        apiBean.addAPIBeanStruct("String");
        logger.info("apiBean：{}", generateClass(apiBean));
    }

    @Test
    public void enumTest() {
        String valArray = "< enum (aaa-c,bbb-d) > array";
        String valMap = "< enum (aaa-c,bbb-d) > map";
        String valNum = "< enum (1,2,3) > map";
        String _val = replaceVal(valMap);
        logger.info("{}", _val);
        APIBeanStruct apiBeanStruct = new APIBeanStruct("EnumField 可选", "无", _val);
        // 剥离出enum
        APIBean apiEnum = getEnum("ClassName", apiBeanStruct);
        if (apiEnum != null) {
            String _lastFieldType = apiBeanStruct.getFieldType();
            _lastFieldType = _lastFieldType.replace(apiEnum.getTmpEnum(), apiEnum.getClassName());
            logger.info("_lastFieldType：{}", _lastFieldType);
            logger.info("TmpEnum：{}", apiEnum.getTmpEnum());
            logger.info("TmpEnumContent：{}", apiEnum.getTmpEnumContent());
            logger.info("apiEnum：{}", generateClass(apiEnum));
        }
    }

    /**
     * 生成类结构
     *
     * @param apiBean
     */
    private String generateClass(APIBean apiBean) {
        final String packageStr = "com.newland.bi.resourcemanage.model.huawei";
        StringBuilder sb = new StringBuilder();
        if (apiBean.isClass()) {
            final String classMode = "package " + packageStr + ";\n\n"// 包名
                    + "%s"// 导入
                    + "public class %s {\n"// 类名
                    + "%s\n"// 字段
                    + "%s\n"// 字段get、set
                    + "}";
            final String fieldMode = "    private %s %s; // %s\n";// 类型 字段名; // 字段说明
            final String fieldGetMode = "    public %s get%s() {return %s;}\n";// 类型 字段名(首字母大写) 字段名
            final String fieldSetMode = "    public void set%s(%s %s) {this.%s=%s;}\n";// 字段名(首字母大写) 类型 字段名 字段名 字段名
            // 字段
            StringBuilder fieldSb = new StringBuilder();
            // 字段get、set
            StringBuilder fieldGetSetSb = new StringBuilder();
            for (APIBeanStruct apiBeanStruct : apiBean.getApiBeanStructList()) {
                // 字段
                fieldSb.append(String.format(fieldMode
                        , apiBeanStruct.getFieldType()
                        , apiBeanStruct.getFieldName()
                        , apiBeanStruct.getFieldDesc()));
                // get
                fieldGetSetSb.append(String.format(fieldGetMode
                        , apiBeanStruct.getFieldType()
                        , firstUpper(apiBeanStruct.getFieldName())
                        , apiBeanStruct.getFieldName()));
                // set
                fieldGetSetSb.append(String.format(fieldSetMode
                        , firstUpper(apiBeanStruct.getFieldName())
                        , firstUpper(apiBeanStruct.getFieldType())
                        , apiBeanStruct.getFieldName()
                        , apiBeanStruct.getFieldName()
                        , apiBeanStruct.getFieldName()));
            }
            // 判断是否要导入List或者Map
            StringBuilder importSb = new StringBuilder();
            if (fieldSb.toString().contains("List<")) {
                importSb.append("import java.util.List;\n");
            }
            if (fieldSb.toString().contains("Map<")) {
                importSb.append("import java.util.Map;\n");
            }
            if (importSb.length() > 0) {
                importSb.append("\n");
            }
            sb.append(String.format(classMode, importSb, apiBean.getClassName(), fieldSb, fieldGetSetSb));
        } else if (apiBean.isEnum()) {
            final String classMode = "package " + packageStr + ";\n\n"// 包名
                    + "public enum %s {\n"// 枚举类名
                    + "    %s;\n"// 所有枚举
                    + "}";
            StringBuilder fieldSb = new StringBuilder();
            for (APIBeanStruct apiBeanStruct : apiBean.getApiBeanStructList()) {
                fieldSb.append(apiBeanStruct.getFieldName())
                        .append(",");
            }
            // 去掉末尾的逗号
            if (fieldSb.length() > 0) fieldSb.deleteCharAt(fieldSb.length() - 1);
            sb.append(String.format(classMode, apiBean.getClassName(), fieldSb));
        }
        return sb.toString();
    }

    /**
     * 生成enum
     *
     * @param className
     * @param apiBeanStruct
     * @return
     */
    private APIBean getEnum(String className, APIBeanStruct apiBeanStruct) {
        APIBean ret = null;
        String _fieldName = apiBeanStruct.getFieldName();
        String _fieldType = apiBeanStruct.getFieldType();
        if (_fieldType.contains("enum (")) {
            ret = new APIBean();
            // 从中找到完整的 enum ( xxx, xxx)，并设置到tmpEnum中，方便后续替换
            int startIndex = _fieldType.indexOf("enum (");
            int endIndex = _fieldType.indexOf(")");
            String _tmpEnumContent = _fieldType.substring(startIndex, endIndex + 1);
            // 设置完整内容
            ret.setTmpEnum(_tmpEnumContent);

            // 构造enum
            // 替换，去空格
            String _enum = _tmpEnumContent.trim().replace("enum (", "");
            _enum = _enum.replace(")", "");
            _enum = _enum.replace(" ", "");
            // 设置枚举内容
            ret.setTmpEnumContent(_enum);

            // 分割
            String[] _enumArray = _enum.split(",");
            // 字段首字母要大写
            ret.setClassName(className + firstUpper(_fieldName) + "Enum");
            ret.setClassType("enum");
            for (String _enumField : _enumArray) {
                // 不能有-，要替换成_
                // 如果是数字开头，要带上字段名
                _enumField = _enumField.replace("-", "_");
                if (Character.isDigit(_enumField.charAt(0))) {
                    _enumField = _fieldName + _enumField;
                }
                // 内容大写
                ret.addEnumAPIBeanStruct(_enumField.toUpperCase());
            }
        }
        return ret;
    }

    /**
     * 首字母大写
     *
     * @param val
     * @return
     */
    private String firstUpper(String val) {
        StringBuilder sb = new StringBuilder();
        char[] valChars = val.toCharArray();
        for (int i = 0; i < valChars.length; i++) {
            if (i == 0) {
                sb.append(Character.toUpperCase(valChars[i]));
            } else {
                sb.append(valChars[i]);
            }
        }
        return sb.toString();
    }

    /**
     * 类型处理
     *
     * @param val
     * @return
     */
    private String replaceVal(String val) {
        String ret = val.replace("integer (int32)", "Integer");
        ret = ret.replace("integer (int64)", "Long");
        ret = ret.replace("string", "String");
        ret = ret.replace("object", "Object");
        ret = ret.replace("number (float)", "Float");
        ret = ret.replace("number (double)", "Double");
        ret = ret.replace("number", "Long");
        if (ret.contains("> array") && ret.contains("> map")) {
            ret = ret.replace("> ", ">");
            ret = ret.replace("< ", "<");
            // 按空格切分
            String[] _retArray = ret.split(" ");
            // 左栈，先进后出
            Stack<String> leftStack = new Stack<>();
            // 右堆，先进先出
            Queue<String> rightQueue = new LinkedBlockingQueue<>();
            for (String _s : _retArray) {
                if (_s.contains("<")) {// 左栈
                    leftStack.push(_s);
                } else {// 右堆
                    rightQueue.add(_s);
                }
            }
            // 结果左栈
            Stack<String> retLeftStack = new Stack<>();
            // 结果右队列
            Queue<String> retRightQueue = new LinkedBlockingQueue<>();
            while (!rightQueue.isEmpty()) {
                String left = leftStack.pop();
                String right = rightQueue.poll();
                if (right != null && right.contains(">array")) {
                    left = left.replace("<", "List<");
                    right = right.replace(">array", ">");
                    retLeftStack.push(left);
                    retRightQueue.add(right);
                } else if (right != null && right.contains(">map")) {
                    left = left.replace("<", "Map<");
                    right = right.replace(">map", ">");
                    retLeftStack.push(left);
                    retRightQueue.add(right);
                }
            }
            // 拼接结果
            StringBuilder _tmpRet = new StringBuilder();
            while (!retLeftStack.isEmpty()) {
                _tmpRet.append(retLeftStack.pop());
            }
            while (!retRightQueue.isEmpty()) {
                _tmpRet.append(retRightQueue.poll());
            }
            ret = _tmpRet.toString();
        } else {
            if (ret.contains("> array")) {
                ret = ret.replace("> array", ">");
                ret = ret.replace("<", "List<");
            } else if (ret.contains("> map")) {
                ret = ret.replace("> map", ">");
                ret = ret.replace("<", "Map<");
            }
        }
        return ret;
    }

    class APIBean {
        String className;
        String classType = "class";
        List<APIBeanStruct> apiBeanStructList = new ArrayList<>();
        String tmpFieldName;
        String tmpFieldDesc;
        String tmpFieldType;
        String tmpEnum;
        String tmpEnumContent;
        boolean isSkipDesc;
        int count = 0;
        int thCount = 0;

        public void thCount() {
            thCount++;
        }

        @Override
        public String toString() {
            return "【" + classType + "】" + className + "，" + apiBeanStructList;
        }

        public boolean addAPIBeanStruct(String value) {
            if (thCount == 2) {
                isSkipDesc = true;
            }
            count++;
            switch (count) {
                case 1:
                    tmpFieldName = value;
                    if (isSkipDesc) count++;
                    return false;
                case 2:
                    tmpFieldDesc = value;
                    return false;
                case 3:
                    tmpFieldType = value;
                    addAPIBeanStruct(tmpFieldName, tmpFieldDesc, tmpFieldType);
                    tmpFieldName = "";
                    tmpFieldDesc = "";
                    tmpFieldType = "";
                    count = 0;
                    return true;
            }
            return false;
        }

        public void addAPIBeanStruct(String fieldName, String fieldDesc, String fieldType) {
            apiBeanStructList.add(new APIBeanStruct(fieldName, fieldDesc, fieldType));
        }

        public void addEnumAPIBeanStruct(String fieldName) {
            apiBeanStructList.add(new APIBeanStruct(fieldName));
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public void setClassNameBySplit(String className) {
            String[] classNameArray = className.split(" ", -1);
            this.className = classNameArray[1];
        }

        public List<APIBeanStruct> getApiBeanStructList() {
            return apiBeanStructList;
        }

        public void setClassType(String classType) {
            this.classType = classType;
        }

        public boolean isClass() {
            return classType.equals("class");
        }

        public boolean isEnum() {
            return classType.equals("enum");
        }

        public String getTmpEnum() {
            return tmpEnum;
        }

        public void setTmpEnum(String tmpEnum) {
            this.tmpEnum = tmpEnum;
        }

        public String getTmpEnumContent() {
            return tmpEnumContent;
        }

        public void setTmpEnumContent(String tmpEnumContent) {
            this.tmpEnumContent = tmpEnumContent;
        }
    }

    class APIBeanStruct {
        String fieldName;
        String fieldDesc;
        String fieldType;

        /**
         * 用于enum
         *
         * @param fieldName
         */
        APIBeanStruct(String fieldName) {
            this.fieldName = fieldName;
        }

        /**
         * 用于class
         *
         * @param fieldName
         * @param fieldDesc
         * @param fieldType
         */
        APIBeanStruct(String fieldName, String fieldDesc, String fieldType) {
            String[] fieldNameArray = fieldName.split(" ");
            // 如果是default为字段，首字母要大写即可以避免java关键字
            this.fieldName = fieldNameArray[0].equals("default") ? firstUpper(fieldNameArray[0]) : fieldNameArray[0];
            this.fieldDesc = (fieldNameArray.length == 2 ? (fieldNameArray[1] + " " + fieldDesc) : fieldDesc);
            this.fieldType = fieldType;
        }

        @Override
        public String toString() {
            return fieldName + "，" + fieldDesc + "，" + fieldType;
        }

        public String getFieldName() {
            return fieldName;
        }

        public String getFieldDesc() {
            return fieldDesc;
        }

        public String getFieldType() {
            return fieldType;
        }

        public void setFieldType(String fieldType) {
            this.fieldType = fieldType;
        }
    }
}