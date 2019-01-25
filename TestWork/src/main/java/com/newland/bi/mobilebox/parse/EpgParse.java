package com.newland.bi.mobilebox.parse;

import com.alibaba.fastjson.JSON;
import com.cqx.process.LogInfoFactory;
import com.cqx.process.Logger;
import com.newland.bi.mobilebox.bean.EpgTag;
import com.newland.bi.mobilebox.bean.EpgTagInfo;

import java.util.ArrayList;
import java.util.List;

import static com.newland.bi.bigdata.utils.string.StringUtils.println;

/**
 * epg解析
 *
 * @author chenqixu
 */
public class EpgParse {

    public static final Logger logger = LogInfoFactory.getInstance(EpgParse.class);
    public static final String newLine = System.getProperty("line.separator");
    /**
     * 是否需要打印树形结构
     */
    public static boolean isPrintTree = false;

    private static List<EpgTagInfo> epgTagInfos = new ArrayList<>();

    /**
     * 循环迭代打印对象
     *
     * @param epgTagList
     * @param subtag
     */
    public static void printEpgList(List<EpgTag> epgTagList, String subtag) {
        if (!isPrintTree) subtag = "";
        for (EpgTag epgTag : epgTagList) {
            if (epgTag.getSubCatgs() != null && epgTag.getSubCatgs().size() > 0) {
                println(epgTag, subtag);
                if (isPrintTree) subtag = subtag + "-";
                printEpgList(epgTag.getSubCatgs(), subtag);
            } else {
                println(epgTag, subtag);
            }
        }
    }

    public static String parserEpgList(List<EpgTag> epgTagList) {
        StringBuffer sb = new StringBuffer();
        for (EpgTag epgTag : epgTagList) {
            if (epgTag.getSubCatgs() != null && epgTag.getSubCatgs().size() > 0) {
//                sb.append(epgTag).append(newLine);
                epgTagInfos.add(EpgTagInfo.newbuilder(epgTag));
//                sb.append(parserEpgList(epgTag.getSubCatgs()));
                parserEpgList(epgTag.getSubCatgs());
            } else {
//                sb.append(epgTag).append(newLine);
                epgTagInfos.add(EpgTagInfo.newbuilder(epgTag));
            }
        }
//        return sb.toString();
        return JSON.toJSONString(epgTagInfos);
    }

    /**
     * json转换成对象
     *
     * @param str
     */
    public static void parseJson(String str) {
        List<EpgTag> epgTagList = JSON.parseArray(str, EpgTag.class);
//        println("#START#");
//        printEpgList(epgTagList, "root");
        StringBuffer a = new StringBuffer();
        StringBuffer value = new StringBuffer(parserEpgList(epgTagList));
//        StringBuffer value = new StringBuffer(a);
//        if (value != null && value.length() > 0) value.deleteCharAt(value.length() - 1);
        println(value);
//        println("#END#");
    }

    /**
     * 对象转化成json
     *
     * @param epgTagList
     */
    public static void toJson(List<EpgTag> epgTagList) {
        println(JSON.toJSONString(epgTagList));
    }

    public static void main(String[] args) {
//        EpgParse.parseJson(createTestStrData());
//        EpgParse.toJson(createTestListData());
        logger.info("test：{}", null);
    }

    /**
     * 创建测试数据
     *
     * @return
     */
    public static List<EpgTag> createTestListData() {
        List<EpgTag> epgTagList = new ArrayList<>();
        epgTagList.add(EpgTag.newbuilder().setCatgId("2220170").setfId("1").setCatgName("最新").setContentType("").build());
        return epgTagList;
    }

    /**
     * 创建测试数据
     *
     * @return
     */
    public static String createTestStrData() {
        return "[\n" +
                "  {\n" +
                "    \"catgId\": 2220170,\n" +
                "    \"fId\": 1,\n" +
                "    \"catgName\": \"最新\",\n" +
                "    \"actionType\": null,\n" +
                "    \"contentType\": \"\",\n" +
                "    \"subCatgs\": []\n" +
                "  },\n" +
                "  {\n" +
                "    \"catgId\": 2221516,\n" +
                "    \"fId\": 1,\n" +
                "    \"catgName\": \"付费影视\",\n" +
                "    \"actionType\": \"\",\n" +
                "    \"contentType\": \"\",\n" +
                "    \"subCatgs\": [\n" +
                "      {\n" +
                "        \"catgId\": 2221810,\n" +
                "        \"fId\": 2221516,\n" +
                "        \"catgName\": \"家庭影院\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2222957,\n" +
                "        \"fId\": 2221516,\n" +
                "        \"catgName\": \"独播剧场\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"catgId\": 2221003,\n" +
                "    \"fId\": 1,\n" +
                "    \"catgName\": \"电影\",\n" +
                "    \"actionType\": \"\",\n" +
                "    \"contentType\": \"\",\n" +
                "    \"subCatgs\": [\n" +
                "      {\n" +
                "        \"catgId\": 2221004,\n" +
                "        \"fId\": 2221003,\n" +
                "        \"catgName\": \"全部电影\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2222859,\n" +
                "        \"fId\": 2221003,\n" +
                "        \"catgName\": \"精品电影\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2221522,\n" +
                "        \"fId\": 2221003,\n" +
                "        \"catgName\": \"华语剧场\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2221523,\n" +
                "        \"fId\": 2221003,\n" +
                "        \"catgName\": \"精彩好莱坞\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2222040,\n" +
                "        \"fId\": 2221003,\n" +
                "        \"catgName\": \"悬疑惊悚\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2221525,\n" +
                "        \"fId\": 2221003,\n" +
                "        \"catgName\": \"4k专区\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2221025,\n" +
                "        \"fId\": 2221003,\n" +
                "        \"catgName\": \"极光电影\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2223036,\n" +
                "        \"fId\": 2221003,\n" +
                "        \"catgName\": \"革命老片\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"catgId\": 2221005,\n" +
                "    \"fId\": 1,\n" +
                "    \"catgName\": \"电视剧\",\n" +
                "    \"actionType\": \"\",\n" +
                "    \"contentType\": \"\",\n" +
                "    \"subCatgs\": [\n" +
                "      {\n" +
                "        \"catgId\": 2221028,\n" +
                "        \"fId\": 2221005,\n" +
                "        \"catgName\": \"热播剧集\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2221036,\n" +
                "        \"fId\": 2221005,\n" +
                "        \"catgName\": \"华语剧场\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2223047,\n" +
                "        \"fId\": 2221005,\n" +
                "        \"catgName\": \"海外热剧\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"video\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2221047,\n" +
                "        \"fId\": 2221005,\n" +
                "        \"catgName\": \"古装历史\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2222041,\n" +
                "        \"fId\": 2221005,\n" +
                "        \"catgName\": \"军旅谍战\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2221045,\n" +
                "        \"fId\": 2221005,\n" +
                "        \"catgName\": \"都市家庭\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2221042,\n" +
                "        \"fId\": 2221005,\n" +
                "        \"catgName\": \"青春偶像\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2221049,\n" +
                "        \"fId\": 2221005,\n" +
                "        \"catgName\": \"极光电视剧\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2221027,\n" +
                "        \"fId\": 2221005,\n" +
                "        \"catgName\": \"全部电视\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"catgId\": 2221006,\n" +
                "    \"fId\": 1,\n" +
                "    \"catgName\": \"综艺\",\n" +
                "    \"actionType\": \"\",\n" +
                "    \"contentType\": \"\",\n" +
                "    \"subCatgs\": [\n" +
                "      {\n" +
                "        \"catgId\": 2221051,\n" +
                "        \"fId\": 2221006,\n" +
                "        \"catgName\": \"热播综艺\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2221288,\n" +
                "        \"fId\": 2221006,\n" +
                "        \"catgName\": \"真人秀\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2221290,\n" +
                "        \"fId\": 2221006,\n" +
                "        \"catgName\": \"央视综艺\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2221289,\n" +
                "        \"fId\": 2221006,\n" +
                "        \"catgName\": \"选秀\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2222862,\n" +
                "        \"fId\": 2221006,\n" +
                "        \"catgName\": \"宠物频道\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2221057,\n" +
                "        \"fId\": 2221006,\n" +
                "        \"catgName\": \"极光综艺\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2221050,\n" +
                "        \"fId\": 2221006,\n" +
                "        \"catgName\": \"全部综艺\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"catgId\": 2221008,\n" +
                "    \"fId\": 1,\n" +
                "    \"catgName\": \"体育\",\n" +
                "    \"actionType\": \"\",\n" +
                "    \"contentType\": \"\",\n" +
                "    \"subCatgs\": [\n" +
                "      {\n" +
                "        \"catgId\": 2222344,\n" +
                "        \"fId\": 2221008,\n" +
                "        \"catgName\": \"篮球\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2222345,\n" +
                "        \"fId\": 2221008,\n" +
                "        \"catgName\": \"足球\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2222346,\n" +
                "        \"fId\": 2221008,\n" +
                "        \"catgName\": \"格斗摔跤\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2221064,\n" +
                "        \"fId\": 2221008,\n" +
                "        \"catgName\": \"名牌栏目\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2221065,\n" +
                "        \"fId\": 2221008,\n" +
                "        \"catgName\": \"赛事精选\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2223038,\n" +
                "        \"fId\": 2221008,\n" +
                "        \"catgName\": \"竞速世界\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"catgId\": 221151,\n" +
                "    \"fId\": 1,\n" +
                "    \"catgName\": \"搏击世界\",\n" +
                "    \"actionType\": \"GetVPosterList\",\n" +
                "    \"contentType\": \"\",\n" +
                "    \"subCatgs\": [\n" +
                "      {\n" +
                "        \"catgId\": 221152,\n" +
                "        \"fId\": 221151,\n" +
                "        \"catgName\": \"我就是拳王\",\n" +
                "        \"actionType\": \"GetVPosterList\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 221153,\n" +
                "        \"fId\": 221151,\n" +
                "        \"catgName\": \"拳王争霸赛\",\n" +
                "        \"actionType\": \"GetVPosterList\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 221154,\n" +
                "        \"fId\": 221151,\n" +
                "        \"catgName\": \"功夫世界杯\",\n" +
                "        \"actionType\": \"GetVPosterList\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 221155,\n" +
                "        \"fId\": 221151,\n" +
                "        \"catgName\": \"搏击风云\",\n" +
                "        \"actionType\": \"GetVPosterList\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 221156,\n" +
                "        \"fId\": 221151,\n" +
                "        \"catgName\": \"拳坛经典\",\n" +
                "        \"actionType\": \"GetVPosterList\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 221157,\n" +
                "        \"fId\": 221151,\n" +
                "        \"catgName\": \"紫禁之巅\",\n" +
                "        \"actionType\": \"GetVPosterList\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 221158,\n" +
                "        \"fId\": 221151,\n" +
                "        \"catgName\": \"体院大奖赛\",\n" +
                "        \"actionType\": \"GetVPosterList\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 221159,\n" +
                "        \"fId\": 221151,\n" +
                "        \"catgName\": \"搏击英雄榜\",\n" +
                "        \"actionType\": \"GetVPosterList\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"catgId\": 2221408,\n" +
                "    \"fId\": 1,\n" +
                "    \"catgName\": \"动画\",\n" +
                "    \"actionType\": \"\",\n" +
                "    \"contentType\": \"\",\n" +
                "    \"subCatgs\": [\n" +
                "      {\n" +
                "        \"catgId\": 2221409,\n" +
                "        \"fId\": 2221408,\n" +
                "        \"catgName\": \"热播动画\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2223008,\n" +
                "        \"fId\": 2221408,\n" +
                "        \"catgName\": \"国产动画\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2221411,\n" +
                "        \"fId\": 2221408,\n" +
                "        \"catgName\": \"海外动画\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2223007,\n" +
                "        \"fId\": 2221408,\n" +
                "        \"catgName\": \"动画电影\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2221413,\n" +
                "        \"fId\": 2221408,\n" +
                "        \"catgName\": \"全部动画\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2221414,\n" +
                "        \"fId\": 2221408,\n" +
                "        \"catgName\": \"动漫番剧\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"catgId\": 2221009,\n" +
                "    \"fId\": 1,\n" +
                "    \"catgName\": \"少儿\",\n" +
                "    \"actionType\": \"\",\n" +
                "    \"contentType\": \"\",\n" +
                "    \"subCatgs\": [\n" +
                "      {\n" +
                "        \"catgId\": 2221415,\n" +
                "        \"fId\": 2221009,\n" +
                "        \"catgName\": \"益智早教\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2221809,\n" +
                "        \"fId\": 2221009,\n" +
                "        \"catgName\": \"启蒙英语\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2221416,\n" +
                "        \"fId\": 2221009,\n" +
                "        \"catgName\": \"央视少儿\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2221407,\n" +
                "        \"fId\": 2221009,\n" +
                "        \"catgName\": \"少儿剧场\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2222855,\n" +
                "        \"fId\": 2221009,\n" +
                "        \"catgName\": \"儿歌童谣\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"catgId\": 2221010,\n" +
                "    \"fId\": 1,\n" +
                "    \"catgName\": \"教育\",\n" +
                "    \"actionType\": \"\",\n" +
                "    \"contentType\": \"\",\n" +
                "    \"subCatgs\": [\n" +
                "      {\n" +
                "        \"catgId\": 2221086,\n" +
                "        \"fId\": 2221010,\n" +
                "        \"catgName\": \"早教启蒙\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2222858,\n" +
                "        \"fId\": 2221010,\n" +
                "        \"catgName\": \"才智小天地\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2222861,\n" +
                "        \"fId\": 2221010,\n" +
                "        \"catgName\": \"学而思\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2221084,\n" +
                "        \"fId\": 2221010,\n" +
                "        \"catgName\": \"我的小学\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2223031,\n" +
                "        \"fId\": 2221010,\n" +
                "        \"catgName\": \"我的中学\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2222485,\n" +
                "        \"fId\": 2221010,\n" +
                "        \"catgName\": \"微课堂\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2221518,\n" +
                "        \"fId\": 2221010,\n" +
                "        \"catgName\": \"大学职场\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2221519,\n" +
                "        \"fId\": 2221010,\n" +
                "        \"catgName\": \"育儿心得\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2223042,\n" +
                "        \"fId\": 2221010,\n" +
                "        \"catgName\": \"国学开讲\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2221096,\n" +
                "        \"fId\": 2221010,\n" +
                "        \"catgName\": \"公开课\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2221085,\n" +
                "        \"fId\": 2221010,\n" +
                "        \"catgName\": \"教育精选\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2223035,\n" +
                "        \"fId\": 2221010,\n" +
                "        \"catgName\": \"课外辅导\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"catgId\": 2221011,\n" +
                "    \"fId\": 1,\n" +
                "    \"catgName\": \"纪实\",\n" +
                "    \"actionType\": \"\",\n" +
                "    \"contentType\": \"\",\n" +
                "    \"subCatgs\": [\n" +
                "      {\n" +
                "        \"catgId\": 2221099,\n" +
                "        \"fId\": 2221011,\n" +
                "        \"catgName\": \"高清精选\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2223011,\n" +
                "        \"fId\": 2221011,\n" +
                "        \"catgName\": \"纪实热播\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2223013,\n" +
                "        \"fId\": 2221011,\n" +
                "        \"catgName\": \"战事风云\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2221102,\n" +
                "        \"fId\": 2221011,\n" +
                "        \"catgName\": \"历史考古\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2221103,\n" +
                "        \"fId\": 2221011,\n" +
                "        \"catgName\": \"自然传奇\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2223009,\n" +
                "        \"fId\": 2221011,\n" +
                "        \"catgName\": \"探索纪实\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2221105,\n" +
                "        \"fId\": 2221011,\n" +
                "        \"catgName\": \"古今人物\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2221106,\n" +
                "        \"fId\": 2221011,\n" +
                "        \"catgName\": \"海外巨制\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2221107,\n" +
                "        \"fId\": 2221011,\n" +
                "        \"catgName\": \"极光纪录片\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"catgId\": 2221735,\n" +
                "    \"fId\": 1,\n" +
                "    \"catgName\": \"党建专栏\",\n" +
                "    \"actionType\": \"\",\n" +
                "    \"contentType\": \"\",\n" +
                "    \"subCatgs\": [\n" +
                "      {\n" +
                "        \"catgId\": 2221736,\n" +
                "        \"fId\": 2221735,\n" +
                "        \"catgName\": \"聚焦十九大\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2221737,\n" +
                "        \"fId\": 2221735,\n" +
                "        \"catgName\": \"党建进行时\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2221738,\n" +
                "        \"fId\": 2221735,\n" +
                "        \"catgName\": \"党史阅览室\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"catgId\": 2223014,\n" +
                "    \"fId\": 1,\n" +
                "    \"catgName\": \"广场舞（限免）\",\n" +
                "    \"actionType\": \"\",\n" +
                "    \"contentType\": \"\",\n" +
                "    \"subCatgs\": [\n" +
                "      {\n" +
                "        \"catgId\": 2223015,\n" +
                "        \"fId\": 2223014,\n" +
                "        \"catgName\": \"每周新舞\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2223016,\n" +
                "        \"fId\": 2223014,\n" +
                "        \"catgName\": \"古典民族\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2223017,\n" +
                "        \"fId\": 2223014,\n" +
                "        \"catgName\": \"现代潮舞\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2223018,\n" +
                "        \"fId\": 2223014,\n" +
                "        \"catgName\": \"炫舞周边\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2223019,\n" +
                "        \"fId\": 2223014,\n" +
                "        \"catgName\": \"海丝广场舞\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"catgId\": 2221292,\n" +
                "    \"fId\": 1,\n" +
                "    \"catgName\": \"健康\",\n" +
                "    \"actionType\": \"\",\n" +
                "    \"contentType\": \"\",\n" +
                "    \"subCatgs\": [\n" +
                "      {\n" +
                "        \"catgId\": 2221293,\n" +
                "        \"fId\": 2221292,\n" +
                "        \"catgName\": \"广场舞\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2221294,\n" +
                "        \"fId\": 2221292,\n" +
                "        \"catgName\": \"运动健身\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2221295,\n" +
                "        \"fId\": 2221292,\n" +
                "        \"catgName\": \"健康百科\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2221296,\n" +
                "        \"fId\": 2221292,\n" +
                "        \"catgName\": \"慢病养护\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2221297,\n" +
                "        \"fId\": 2221292,\n" +
                "        \"catgName\": \"饮食养生\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2221550,\n" +
                "        \"fId\": 2221292,\n" +
                "        \"catgName\": \"母婴健康\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2221299,\n" +
                "        \"fId\": 2221292,\n" +
                "        \"catgName\": \"情感心理\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"catgId\": 2223022,\n" +
                "    \"fId\": 1,\n" +
                "    \"catgName\": \"茶文化\",\n" +
                "    \"actionType\": \"\",\n" +
                "    \"contentType\": \"\",\n" +
                "    \"subCatgs\": [\n" +
                "      {\n" +
                "        \"catgId\": 2223023,\n" +
                "        \"fId\": 2223022,\n" +
                "        \"catgName\": \"茶叶百科\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2223024,\n" +
                "        \"fId\": 2223022,\n" +
                "        \"catgName\": \"茶道文化\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2223025,\n" +
                "        \"fId\": 2223022,\n" +
                "        \"catgName\": \"茶疗养生\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2223026,\n" +
                "        \"fId\": 2223022,\n" +
                "        \"catgName\": \"茶工艺\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2223027,\n" +
                "        \"fId\": 2223022,\n" +
                "        \"catgName\": \"茶市资讯\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"catgId\": 2221012,\n" +
                "    \"fId\": 1,\n" +
                "    \"catgName\": \"生活\",\n" +
                "    \"actionType\": \"\",\n" +
                "    \"contentType\": \"\",\n" +
                "    \"subCatgs\": [\n" +
                "      {\n" +
                "        \"catgId\": 2223021,\n" +
                "        \"fId\": 2221012,\n" +
                "        \"catgName\": \"八闽戏曲\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2221113,\n" +
                "        \"fId\": 2221012,\n" +
                "        \"catgName\": \"生活百科\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2221514,\n" +
                "        \"fId\": 2221012,\n" +
                "        \"catgName\": \"环球美食\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2221116,\n" +
                "        \"fId\": 2221012,\n" +
                "        \"catgName\": \"环球旅行\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2221117,\n" +
                "        \"fId\": 2221012,\n" +
                "        \"catgName\": \"美妆风尚\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2223040,\n" +
                "        \"fId\": 2221012,\n" +
                "        \"catgName\": \"八闽风采\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2221118,\n" +
                "        \"fId\": 2221012,\n" +
                "        \"catgName\": \"时尚家居\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2221122,\n" +
                "        \"fId\": 2221012,\n" +
                "        \"catgName\": \"超级汽车\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"catgId\": 2222851,\n" +
                "    \"fId\": 1,\n" +
                "    \"catgName\": \"娱乐\",\n" +
                "    \"actionType\": \"\",\n" +
                "    \"contentType\": \"\",\n" +
                "    \"subCatgs\": [\n" +
                "      {\n" +
                "        \"catgId\": 2222856,\n" +
                "        \"fId\": 2222851,\n" +
                "        \"catgName\": \"电竞游戏\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2222857,\n" +
                "        \"fId\": 2222851,\n" +
                "        \"catgName\": \"音乐现场\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"catgId\": 2221014,\n" +
                "    \"fId\": 1,\n" +
                "    \"catgName\": \"更多\",\n" +
                "    \"actionType\": \"\",\n" +
                "    \"contentType\": \"\",\n" +
                "    \"subCatgs\": [\n" +
                "      {\n" +
                "        \"catgId\": 2221110,\n" +
                "        \"fId\": 2221014,\n" +
                "        \"catgName\": \"3D专区\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2221109,\n" +
                "        \"fId\": 2221014,\n" +
                "        \"catgName\": \"超高清专区\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2221111,\n" +
                "        \"fId\": 2221014,\n" +
                "        \"catgName\": \"体验专区\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"catgId\": 2221016,\n" +
                "    \"fId\": 1,\n" +
                "    \"catgName\": \"央视名栏\",\n" +
                "    \"actionType\": \"\",\n" +
                "    \"contentType\": \"\",\n" +
                "    \"subCatgs\": [\n" +
                "      {\n" +
                "        \"catgId\": 2221067,\n" +
                "        \"fId\": 2221016,\n" +
                "        \"catgName\": \"等着我\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2221068,\n" +
                "        \"fId\": 2221016,\n" +
                "        \"catgName\": \"开讲啦\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2221069,\n" +
                "        \"fId\": 2221016,\n" +
                "        \"catgName\": \"特别节目\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2221070,\n" +
                "        \"fId\": 2221016,\n" +
                "        \"catgName\": \"对话\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2221071,\n" +
                "        \"fId\": 2221016,\n" +
                "        \"catgName\": \"百家讲坛\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2221072,\n" +
                "        \"fId\": 2221016,\n" +
                "        \"catgName\": \"国宝档案\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2221082,\n" +
                "        \"fId\": 2221016,\n" +
                "        \"catgName\": \"经济半小时\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2221090,\n" +
                "        \"fId\": 2221016,\n" +
                "        \"catgName\": \"每周质量报告\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2221091,\n" +
                "        \"fId\": 2221016,\n" +
                "        \"catgName\": \"诗词大会\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2221092,\n" +
                "        \"fId\": 2221016,\n" +
                "        \"catgName\": \"了不起的挑战\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2221093,\n" +
                "        \"fId\": 2221016,\n" +
                "        \"catgName\": \"城市之间\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2221094,\n" +
                "        \"fId\": 2221016,\n" +
                "        \"catgName\": \"星光大道\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"catgId\": 2221019,\n" +
                "    \"fId\": 1,\n" +
                "    \"catgName\": \"新闻\",\n" +
                "    \"actionType\": \"\",\n" +
                "    \"contentType\": \"\",\n" +
                "    \"subCatgs\": [\n" +
                "      {\n" +
                "        \"catgId\": 2221040,\n" +
                "        \"fId\": 2221019,\n" +
                "        \"catgName\": \"时政热点\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2221041,\n" +
                "        \"fId\": 2221019,\n" +
                "        \"catgName\": \"资讯聚焦\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2221043,\n" +
                "        \"fId\": 2221019,\n" +
                "        \"catgName\": \"财经股市\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2221048,\n" +
                "        \"fId\": 2221019,\n" +
                "        \"catgName\": \"社会法治\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2221054,\n" +
                "        \"fId\": 2221019,\n" +
                "        \"catgName\": \"军事观察\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2223039,\n" +
                "        \"fId\": 2221019,\n" +
                "        \"catgName\": \"台海热点\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"catgId\": 2221024,\n" +
                "    \"fId\": 1,\n" +
                "    \"catgName\": \"演示片\",\n" +
                "    \"actionType\": \"\",\n" +
                "    \"contentType\": \"\",\n" +
                "    \"subCatgs\": [\n" +
                "      {\n" +
                "        \"catgId\": 2221026,\n" +
                "        \"fId\": 2221024,\n" +
                "        \"catgName\": \"4K演示\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2221029,\n" +
                "        \"fId\": 2221024,\n" +
                "        \"catgName\": \"高清演示\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"catgId\": 2221030,\n" +
                "        \"fId\": 2221024,\n" +
                "        \"catgName\": \"标清演示\",\n" +
                "        \"actionType\": \"\",\n" +
                "        \"contentType\": \"\",\n" +
                "        \"subCatgs\": []\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "]";
    }
}
