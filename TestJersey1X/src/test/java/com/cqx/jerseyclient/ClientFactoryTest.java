package com.cqx.jerseyclient;

import com.alibaba.fastjson.JSON;
import com.cqx.bean.RestParam;
import com.cqx.bean.oa.*;
import com.cqx.common.utils.file.FileCount;
import com.cqx.common.utils.file.FileUtil;
import com.cqx.common.utils.http.HttpUtil;
import com.cqx.common.utils.http.JavaScriptUtil;
import com.cqx.common.utils.system.SleepUtil;
import com.cqx.common.utils.system.TimeUtil;
import com.cqx.download.http.HttpsUtil;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientFactoryTest {
    private static final Logger logger = LoggerFactory.getLogger(ClientFactoryTest.class);

    @Test
    public void postFile() {
        boolean result = ClientFactory.getInstance()
                .buildFile()
                .addFile(new File("d:\\tmp\\1.txt"))
                .postFile("http://localhost:19192/server/call/upload", Boolean.class);
        logger.info("result：{}", result);
    }

    @Test
    public void postBatchFile() {
        boolean result = ClientFactory.getInstance()
                .buildFile()
                .addFile(new File("d:\\tmp\\1.txt"))
                .addFile(new File("d:\\tmp\\2.txt"))
                .postFile("http://localhost:19192/server/call/batch/upload", Boolean.class);
        logger.info("result：{}", result);
    }

    @Test
    public void callComponent() {
        ClientFactory clientFactory = ClientFactory.getInstance();
        clientFactory.postJSON("http://10.1.8.203:19192/server/call/callComponent",
                "{\"className\":\"com.newland.component.FujianBI.impl.ComponentNoWorkTest\"," +
                        "\"param\":\"<dog xmlns='uri:dog' id='node2880'>" +
                        "<desc><![CDATA[组件测试类]]></desc>" +
                        "<component name='component_no_work_test' version='1.0'>" +
                        "<param name='hdfs_name'><![CDATA[localfs]]></param>" +
                        "</component>" +
                        "</dog>\"," +
                        "\"task_id\":\"100000000001\"}");
        while (clientFactory.get("http://10.1.8.203:19192/server/call/get_task_status/100000000001", Integer.class) != 0) {
            List<String> logs = clientFactory.get("http://10.1.8.203:19192/server/call/get_task_log/100000000001", List.class);
            for (String log : logs) {
                logger.info("log：{}", log);
            }
            SleepUtil.sleepMilliSecond(500);
        }
        //日志有可能没消费完，再消费一次
        List<String> logs = clientFactory.get("http://10.1.8.203:19192/server/call/get_task_log/100000000001", List.class);
        for (String log : logs) {
            logger.info("log：{}", log);
        }
        //最后释放任务
        clientFactory.get("http://10.1.8.203:19192/server/call/release_task/100000000001");
    }

    @Test
    public void chatgpt() {
        String url = "https://api.openai.com/v1/chat/completions";
        HttpsUtil httpsUtil = new HttpsUtil();
        Object obj = httpsUtil.httpRequest(new RestParam(
                url, "GET", null, "https", "", "",
                "", "",
                "", ""), "string", null);
        logger.info("{}", obj);
    }

    @Test
    public void oa() throws URISyntaxException {
        HttpsUtil httpsUtil = new HttpsUtil();
        HttpUtil httpUtil = new HttpUtil();

        // newdoValidate，获取casKey
//        String url = "https://10.1.4.252/nloa/baseData.do?action=newDoValidate&sasVea=03456&sacVeb=d20ab6e9e64a8af1dddc88876881ccc1&_t" + System.currentTimeMillis() / 1000;
//        Object casKey = httpsUtil.httpRequest(new RestParam(
//                url, "GET", null, "https", "", "",
//                "", "",
//                "", ""), "string", null);
//        logger.info("casKey={}", casKey);

        // 跳转到NL开发云平台
        // var encodeKey = encodeURIComponent(casKey);
        // var encodeUrl = encodeURIComponent(window.location.href);
        // var agent = navigator.userAgent.toLowerCase();
        // http://10.1.4.79:8080/casAuth?casKey=PPkun60rO5fDMw0LS3PTUHJoLWt4auz297sATLpJTKM=&casUrl=https://10.1.4.252/cas/login?act=login
//        String agentNL = httpUtil.doGet("http://10.1.4.79:8080/casAuth?casKey=" + URLEncoder.encode(casKey.toString()) + "&casUrl=https://10.1.4.252/cas/login?act=login");
//        logger.info("agentNL={}", agentNL);

        // casToken，获取token
        Map<String, String> header = new HashMap<>();
//        header.put("Authorization", "Basic dGVzdDp0ZXN0");
//        String casToken = httpUtil.doPost("http://10.1.4.79:8080/api/auth/oauthForward/casToken"
//                , header, "{\"casKey\":\"" + casKey + "\"}");
//        logger.info("casToken={}", casToken);
//        CasTokenBean casTokenBean = JSON.parseObject(casToken, CasTokenBean.class);
//        logger.info("Access_token={}", casTokenBean.getRespData().getAccess_token());
//        header.clear();
//        header.put("Authorization", casTokenBean.getRespData().getAccess_token());
        header.put("Authorization", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2luZm8iOnsiaWQiOiJlNTQ0ZjU1NGE0YjY0NjdkODg4N2Y1MmUyZmY2OTUwNiIsIm5hbWUiOiLpmYjmo4vml60iLCJzcGVsbCI6ImNoZW5xaXh1Iiwic2hvcnROYW1lIjoiQ1FYIiwiYWNjb3VudCI6IjAzNDU2IiwicGFzc3dvcmQiOm51bGwsIm1vYmlsZSI6IjEzNTA5MzIzODI0IiwiZW1haWwiOiJjaGVucXhAbmV3bGFuZC5jb20uY24iLCJvdGhlckVtYWlsIjpudWxsLCJpbm5lckVtYWlsIjpudWxsLCJnZW5kZXIiOjEsImFjdG9ySWQiOiJlM2JmYjE1NjUyOWU0NDZjOTg5NGViZjE1ZDdhYTc3NSIsIm9yaWdpbkZsYWciOiJSRU1PVEUiLCJvdXR0ZXJJZCI6IjEwMzQ1NiIsInRhbGVudElkIjoiMTAxMzgzIiwiYXBwSWQiOiJhZmYyNTY5MDkzMzk0YjYzYjE0NWZiNjY0ZmJiODRlNSIsIm9ialN0YXR1cyI6MCwiY3JlYXRlRGF0ZSI6MTU2OTg0MTk1MTAwMCwiY3JlYXRlVXNlciI6bnVsbCwidXBkYXRlRGF0ZSI6MTY1ODgzMzkyMDAwMCwidXBkYXRlVXNlciI6bnVsbCwiaXNJbml0UHdkIjoxLCJjYW5VcGRhdGVQd2QiOjEsIm1lbnVzIjpudWxsLCJkZXBhcnRtZW50SWQiOiI1ZDdhMzQzNzc4ZGE0MjRkYTViNWY4ZWE3NTZmNTdmYiIsImRlcGFydG1lbnRBY3RvcklkIjoiNmRhYzM2ZjJmOGU2NDE5MjllZjI4OTI4MDcwNWY3Y2IiLCJkZXBhcnRtZW50TmFtZSI6IuaVsOaNruiBmuWQiOe7hCIsIm9yZ2FuaXphdGlvbk5hbWUiOm51bGwsIm9yZ2FuaXphdGlvblBob3RvIjpudWxsLCJob21lUGFnZVBhdGgiOm51bGwsImhvbWVQYWdlVGl0bGUiOm51bGwsImxvZ2luRXJyb3JUaW1lcyI6MCwibGFzdExvZ2luRXJyb3JUaW1lIjpudWxsLCJhcHBTdGF0dXMiOm51bGwsImludmFsaWRUaW1lIjo0MTAyMzI5NjAwMDAwLCJ0aGVtZVZhbHVlIjpudWxsLCJleGlzdFRyeUFjY291bnQiOjB9LCJ1c2VyX25hbWUiOiIwMzQ1NiIsInNjb3BlIjpbInVzZXIiXSwiZXhwIjoxNjgxNzUzODY3LCJhdXRob3JpdGllcyI6WyLmma7pgJrnlKjmiLciXSwianRpIjoiOWRhYTc0NjYtYjRmZS00MmJjLThiNjgtOTMxNzY0YjkwNzZlIiwiY2xpZW50X2lkIjoidGVzdCJ9.81nmLqmc0GoBHPU1vlslr_iHJATKTqA1_-n-GmdGp5A");

        // getSystemConfig
//        String getSystemConfig = httpUtil.doGet("http://10.1.4.79:8080/api/tenant/globalConfig/getSystemConfig?_t=" + System.currentTimeMillis() / 1000);
//        logger.info("getSystemConfig={}", getSystemConfig);

        // createVisitsRecord
        header.put("Host", "10.1.4.79:8080");
        header.put("Accept", "application/json, text/plain, */*");
        header.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36 Edg/109.0.1518.55");
        header.put("Content-Type", "application/json");
        header.put("Origin", "http://10.1.4.79:8080");
        header.put("Accept-Encoding", "gzip, deflate");
        header.put("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6");
        header.put("Cookie", "HttpOnly; HttpOnly");
        header.put("Connection", "close");
//        String createVisitsRecord = httpUtil.doPost("http://10.1.4.79:8080/api/tenant/userMonthVisits/createVisitsRecord"
//                , header, "{\"resourceAddress\": \"/home\"}");// agile/board
//        logger.info("createVisitsRecord={}", createVisitsRecord);

        // 启动一个websocket客户端
//        startWebSocketClient("ws://10.1.4.79:8080/ws/messager/sock-js/588/04xuiqvf/websocket");

        // 查询团队
        String agileTeamGroupOfVdeptByTeamId = httpUtil.doPost("http://10.1.4.79:8080/api/agile/agileTeam/getAgileTeamGroupOfVdeptByTeamId"
                , header, "{\"agileTeamId\":\"\"}");
        logger.info("agileTeamGroupOfVdeptByTeamId={}", agileTeamGroupOfVdeptByTeamId);
    }

    @Test
    public void oaMoveCardItem() throws Exception {
        HttpsUtil httpsUtil = new HttpsUtil();
        HttpUtil httpUtil = new HttpUtil();
        // 请求头
        Map<String, String> header = new HashMap<>();
        header.put("Host", "10.1.4.79:8080");
        header.put("Accept", "application/json, text/plain, */*");
        header.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36 Edg/109.0.1518.55");
        header.put("Content-Type", "application/json");
        header.put("Origin", "http://10.1.4.79:8080");
        header.put("Accept-Encoding", "gzip, deflate");
        header.put("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6");
        header.put("Cookie", "HttpOnly; HttpOnly");
        header.put("Connection", "close");
        // 团队ID
        final String TeamId = "5837efa8fe8a4503a56cba64e0e6fa82";
        String oa_username = "03456";
        String oa_password = "12345674a";
        String user_id = null;
        // 是否分解故事
        final boolean isStoryFenJie = false;
        // 是否移动任务卡片
        final boolean isMoveTaskCard = true;

        JavaScriptUtil javaScriptUtil = new JavaScriptUtil("d:\\tmp\\html\\oa\\js\\", "md5");
        oa_password = javaScriptUtil.exec("var md5Pwd = hex_md5('" + oa_password + "')", "md5Pwd").toString();

        AgileTeamMemberEmployee agileTeamMemberEmployee = parserAgileTeamMemberEmployee();
        if (agileTeamMemberEmployee != null) {
            for (Employee employee : agileTeamMemberEmployee.getRespData()) {
                logger.info("name={}, id={}, account={}", employee.getName(), employee.getId(), employee.getAccount());
                if (employee.getAccount().equals(oa_username)) {
                    user_id = employee.getId();
                    break;
                }
            }
        }

        logger.info("[oa] oa_username={}, oa_password={}, user_id={}", oa_username, oa_password, user_id);

        // newdoValidate，获取casKey
        String url = "https://10.1.4.252/nloa/baseData.do?action=newDoValidate&sasVea=" + oa_username + "&sacVeb=" + oa_password + "&_t"
                + System.currentTimeMillis() / 1000;
        Object casKey = httpsUtil.httpRequest(new RestParam(
                url, "GET", null, "https", "", "",
                "", "",
                "", ""), "string", null);
        logger.info("casKey={}", casKey);

        // casToken，获取token
        header.put("Authorization", "Basic dGVzdDp0ZXN0");
        String casToken = httpUtil.doPost("http://10.1.4.79:8080/api/auth/oauthForward/casToken"
                , header, "{\"casKey\":\"" + casKey + "\"}");
        logger.info("casToken={}", casToken);
        CasTokenBean casTokenBean = JSON.parseObject(casToken, CasTokenBean.class);
        logger.info("Access_token={}", casTokenBean.getRespData().getAccess_token());
        header.put("Authorization", casTokenBean.getRespData().getAccess_token());

        // getAgilePlanByTeamId，获取最新agilePlanId
        String getAgilePlanByTeamId = httpUtil.doGet("http://10.1.4.79:8080/api/agile/agilePlan/getAgilePlanByTeamId/"
                + TeamId + "?_t=" + System.currentTimeMillis() / 1000, header);
        logger.info("getAgilePlanByTeamId={}", getAgilePlanByTeamId);
        AgilePlanByTeamIdRespBean agilePlanByTeamIdRespBean = JSON.parseObject(getAgilePlanByTeamId, AgilePlanByTeamIdRespBean.class);
        List<AgilePlanByTeamIdRespData> agilePlanByTeamIdRespDataList = new ArrayList<>();
        if (agilePlanByTeamIdRespBean.getRespData() != null) {
            for (AgilePlanByTeamIdRespData agilePlanByTeamIdRespData : agilePlanByTeamIdRespBean.getRespData()) {
                String name = agilePlanByTeamIdRespData.getName();
                if (name.contains("DAG") && name.contains("2023")) {
                    agilePlanByTeamIdRespDataList.add(agilePlanByTeamIdRespData);
                }
            }
            Collections.sort(agilePlanByTeamIdRespDataList);
            // 获取最新计划
            if (agilePlanByTeamIdRespDataList.size() > 0) {
                AgilePlanByTeamIdRespData newPlan = agilePlanByTeamIdRespDataList.get(0);
                logger.info("AgilePlanId={}, Name={}, PlanDateStart={}"
                        , newPlan.getAgilePlanId()
                        , newPlan.getName()
                        , TimeUtil.formatTime(newPlan.getPlanDateStart()));

                // queryList，通过planId查询需求、任务、开发、测试、运维的columnId
                // <需求<就绪>,任务<待办任务>,开发<进行中,已完成>,测试<进行中,已完成>>,运维<<待验收,待交付,已交付>>
                // 测试-已完成
                String complatePosition = "";
                String jsonQueryList = httpUtil.doPost("http://10.1.4.79:8080/api/agile/freeColumn/queryList"
                        , header, "{\"planId\":\"" + newPlan.getAgilePlanId() + "\"}");
                QueryList queryList = JSON.parseObject(jsonQueryList, QueryList.class);
                Map<String, QueryListRespDataNext> nextMap = new HashMap<>();
                // 先按children拆成QueryListRespDataNextList
                List<QueryListRespDataNext> queryListRespDataNextList = new ArrayList<>();
                for (QueryListRespData respData : queryList.getRespData()) {
                    logger.info(" ColumnName={}, value={}"
                            , respData.getColumnName()
                            , JSON.toJSON(respData));
                    for (QueryListChildren queryListChildren : respData.getChildren()) {
                        queryListRespDataNextList.add(new QueryListRespDataNext(respData, queryListChildren));
                        if (respData.getColumnName().contains("测试") && queryListChildren.getStatusColumnName().contains("已完成")) {
                            complatePosition = respData.getColumnId() + "_" + queryListChildren.getStatusColumnId();
                        }
                    }
                }
                // 然后再循环QueryListRespDataNextList写入map
                LinkedBlockingQueue<QueryListRespDataNext> queue = new LinkedBlockingQueue<>();
                for (QueryListRespDataNext next : queryListRespDataNextList) {
                    QueryListRespDataNext queueData = queue.poll();
                    if (queueData != null) {
                        nextMap.put(queueData.getColumnId() + "_" + queueData.getStatusColumnId(), next);
                    }
                    queue.offer(next);
                }
                logger.info("complatePosition={}, nextMap={}", complatePosition, JSON.toJSONString(nextMap));

                // queryBoardData，获取所有信息
                QueryBoardData queryBoardData = new QueryBoardData();
                queryBoardData.setTeamId(TeamId);
                queryBoardData.setAgilePlanId(newPlan.getAgilePlanId());
                if (!isStoryFenJie) {
                    // 未分解故事的时候加上这个就查询不到
                    queryBoardData.setMemberSearch(user_id);
                }
                String retBoardData = httpUtil.doPost("http://10.1.4.79:8080/api/agile/freeColumnItem/queryBoardData"
                        , header, JSON.toJSONString(queryBoardData));
                BoardDataBean boardDataBean = JSON.parseObject(retBoardData, BoardDataBean.class);
                AtomicInteger moveCnt = new AtomicInteger(0);
                // 故事
                for (BoardDataRespData boardDataRespData : boardDataBean.getRespData()) {
                    if (moveCnt.get() > 0) {
                        logger.info("一天只移动一个卡片，今天的任务已经完成。");
                        break;
                    }
                    boolean isFenJie = false;
                    String _UserStoryId = boardDataRespData.getUserStoryId();
                    String _UserStoryCode = boardDataRespData.getUserStoryCode();
                    String _Person = boardDataRespData.getResponsiblePerson();
                    String _PersonName = boardDataRespData.getResponsiblePersonName();
                    String _scale = boardDataRespData.getScale();
                    if (_Person.equals(user_id)) {
                        // 根据估算规模来判断是否分解完成
                        // 根据规模/2来进行任务分解
                        int scale = Integer.valueOf(_scale);
                        // 判断能否整除
                        if (scale % 2 == 0) {
                            int _taskSize = 0;
                            for (TaskCard taskCard : boardDataRespData.getTaskCardList()) {
                                String _itemId = taskCard.getItemId();
                                // 已分解
                                if (_itemId != null && _itemId.length() > 0) {
                                    _taskSize++;
                                }
                            }
                            int _needTaskSize = scale / 2;
                            if (_needTaskSize != _taskSize) {
                                logger.warn("[估算分解不完整] 估算规模/2={}, 已分解任务={}", _needTaskSize, _taskSize);
                                // 分解剩余任务
                                for (int i = (_taskSize + 1); i <= _needTaskSize; i++) {
                                    // addTaskCard
                                    AddTaskCard addTaskCard = new AddTaskCard();
                                    addTaskCard.setUserStoryId(_UserStoryId);
                                    addTaskCard.setAgilePlanId(newPlan.getAgilePlanId());
                                    addTaskCard.setName(_UserStoryCode + "-V" + i);
                                    addTaskCard.setDescription(_UserStoryCode + "-V" + i);
                                    if (isStoryFenJie) {
                                        String retAddTaskCard = httpUtil.doPost("http://10.1.4.79:8080/api/agile/task/addTaskCard"
                                                , header, JSON.toJSONString(addTaskCard));
                                        logger.info("[分解故事] retAddTaskCard={}", retAddTaskCard);
                                    }
                                }
                            }
                        } else {// 不能整除
                            logger.warn("[异常-故事未分解] {}的[{}]{}, 规模点数不能被2整除！请调整后再试。当前规模点数={}"
                                    , _PersonName, _UserStoryId, _UserStoryCode, scale);
                            continue;
                        }

                        // 任务卡片
                        for (TaskCard taskCard : boardDataRespData.getTaskCardList()) {
                            if (moveCnt.get() > 0) {
                                logger.info("一天只移动一个卡片，今天的任务已经完成。");
                                break;
                            }
                            BoardDataRespPosition taskCardPosition = taskCard.getPosition();
                            String _itemId = taskCard.getItemId();
                            // 已分解
                            if (_itemId != null && _itemId.length() > 0) {
                                isFenJie = true;
                                logger.info("[故事已分解] {}的[{}]{}, ItemId={}, Name={}, StatusColumnId={}, Sort={}"
                                        , _PersonName, _UserStoryId, _UserStoryCode, taskCard.getItemId(), taskCard.getName()
                                        , taskCardPosition.getStatusColumnId(), taskCardPosition.getSort());
                                // 一般只会有一个卡片，所以这里的for循环看上去确实有点奇怪，干脆在末尾加一个break;
                                for (Task task : taskCard.getTaskList()) {
                                    // Status=1表示待办任务
                                    // Status=2表示进行中(开发和测试通用)
                                    // Status=3表示已完成(开发和测试通用)
                                    logger.info("Status={}, UserStoryId={}", task.statusDesc(), task.getUserStoryId());
                                    //=================================
                                    // moveCardItem，移动卡片
                                    //=================================
                                    if (isMoveTaskCard) {
                                        if (moveCnt.get() > 0) {
                                            logger.info("一天只移动一个卡片，今天的任务已经完成。");
                                            break;
                                        }
                                        String nowPosition = taskCardPosition.getColumnId() + "_" + taskCardPosition.getStatusColumnId();
                                        if (nowPosition.contains(complatePosition)) {
                                            logger.info("[任务已经移动到底] {}的{}", _PersonName, taskCard.getName());
                                        } else {
                                            while (!nowPosition.contains(complatePosition)) {
                                                MoveCardReqBean moveCardReqBean = new MoveCardReqBean();
                                                // 任务->开发(进行中、已完成)->测试(进行中、已完成)
                                                // 获取移动列的信息
                                                QueryListRespDataNext queryListRespDataNext = nextMap.get(
                                                        taskCardPosition.getColumnId() + "_" + taskCardPosition.getStatusColumnId());
                                                moveCardReqBean.setClearTask(false);// false固定
                                                moveCardReqBean.setItemId(_itemId);// 项目ID
                                                moveCardReqBean.setUserStoryId(_UserStoryId);// 用户故事ID
                                                moveCardReqBean.setSort(taskCardPosition.getSort());// position的sort
                                                moveCardReqBean.setColumnId(queryListRespDataNext.getColumnId());// 要移动的列的ID
                                                moveCardReqBean.setColumnTypeSort(queryListRespDataNext.getSort());// 要移动的列的sort
                                                moveCardReqBean.setStatusColumnId(queryListRespDataNext.getStatusColumnId());// 要移动的列的statusColumnId
                                                moveCardReqBean.setTypeId(queryListRespDataNext.getTypeId());// 要移动的列的typeId
                                                logger.info("moveCardReqBean={}", JSON.toJSONString(moveCardReqBean));
                                                // 从任务一直移动到测试(已完成)为止
                                                String moveCardRespStr = httpUtil.doPost("http://10.1.4.79:8080/api/agile/freeColumnItem/moveCardItem"
                                                        , header, JSON.toJSONString(moveCardReqBean));
                                                MoveCardRespBean moveCardRespBean = JSON.parseObject(moveCardRespStr, MoveCardRespBean.class);
                                                // 更新当前位置
                                                taskCardPosition = moveCardRespBean.getRespData().getPosition();
                                                nowPosition = taskCardPosition.getColumnId() + "_" + taskCardPosition.getStatusColumnId();
                                                logger.info("moveCardRespStr={}, nowPosition={}, contains={}"
                                                        , moveCardRespStr, nowPosition, nowPosition.contains(complatePosition));
                                            }
                                            // 一天只移动一个卡片
                                            moveCnt.incrementAndGet();
                                        }
                                    }
                                    break;
                                }
                            } else if (!isFenJie) {// 未分解
                                logger.info("[故事未分解] {}的[{}]{}, 估算规模={}", _PersonName, _UserStoryId, _UserStoryCode, _scale);
                            } else {
                                logger.warn("[异常-故事分解不完整] {}的[{}]{}, 估算规模={}", _PersonName, _UserStoryId, _UserStoryCode, _scale);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * <table>
     * <tr>
     * <td>任务</td>
     * <td>"columnId": "297bf1e466ef4a6595f892f074784cc3"</td>
     * <td>待办任务</td>
     * <td>"statusColumnId": "2"</td>
     * </tr>
     * <tr>
     * <td>开发</td>
     * <td>"columnId": "e00d2e8c07ad46af9474a2f65f2d0d46"</td>
     * <td>进行中</td>
     * <td>"statusColumnId": "3"</td>
     * <td>已完成</td>
     * <td>"statusColumnId": "4"</td>
     * </tr>
     * <tr>
     * <td>测试</td>
     * <td>"columnId": "411625364d1b478a9c6df82420d4913b"</td>
     * <td>进行中</td>
     * <td>"statusColumnId": "3"</td>
     * <td>已完成</td>
     * <td>"statusColumnId": "4"</td>
     * </tr>
     * </table>
     */
    @Test
    public void BoardDataBean() {
        String json = "{\"respResult\":\"1\",\"respData\":[{\"itemId\":\"a7e202102db847c4ae92b1bff1f76676\",\"userStoryId\":\"f6d7f9e360a5437ea44e11d954c658b4\",\"userStoryCode\":\"实时中台-B域模型分析设计跟进与协调0417\",\"userStorySeq\":\"Y3634\",\"description\":\"产品经理要求，完成实时中台-B域-模型分析设计跟进与协调\",\"agilePlanId\":\"de8531c6e2e5472cba283b248f5faa89\",\"priority\":\"3\",\"priorityName\":\"高\",\"type\":\"2\",\"status\":\"2\",\"scale\":\"5\",\"isPrint\":false,\"responsiblePerson\":\"e544f554a4b6467d8887f52e2ff69506\",\"responsiblePersonName\":\"陈棋旭\",\"position\":{\"columnId\":\"ccb4155892ba40cb9b516c501f8b280f\",\"statusColumnId\":1,\"sort\":1},\"taskCardList\":[{\"itemId\":\"c2e80672b7b84cd998de02592b1c43a6\",\"name\":\"实时中台-B域模型分析设计跟进与协调0417-v2\",\"description\":\"实时中台-B域模型分析设计跟进与协调0417-v2\",\"createUserId\":\"e544f554a4b6467d8887f52e2ff69506\",\"duration\":1.0,\"isPrint\":false,\"position\":{\"columnId\":\"297bf1e466ef4a6595f892f074784cc3\",\"statusColumnId\":2,\"taskId\":\"3d619b63f8d04634911f7da7c359b8c1\",\"sort\":1},\"messageCount\":0,\"blockStatus\":0,\"taskList\":[{\"taskId\":\"3d619b63f8d04634911f7da7c359b8c1\",\"itemId\":\"c2e80672b7b84cd998de02592b1c43a6\",\"userId\":\"e544f554a4b6467d8887f52e2ff69506\",\"userName\":\"陈棋旭\",\"createTime\":1681735486284,\"duration\":1.0,\"description\":\"实时中台-B域模型分析设计跟进与协调0417-v2\",\"isCrossTeamTask\":0,\"userStoryId\":\"f6d7f9e360a5437ea44e11d954c658b4\",\"status\":1}],\"isDelay\":0},{\"itemId\":\"5549bd7421564fd9b6550dc9a7bba948\",\"name\":\"实时中台-B域模型分析设计跟进与协调0417\",\"description\":\"产品经理要求，完成实时中台-B域-模型分析设计跟进与协调\",\"createUserId\":\"e544f554a4b6467d8887f52e2ff69506\",\"duration\":1.0,\"isPrint\":false,\"position\":{\"columnId\":\"411625364d1b478a9c6df82420d4913b\",\"statusColumnId\":4,\"taskId\":\"8b518c79693849a98a714c0800529c25\",\"sort\":1},\"messageCount\":0,\"blockStatus\":0,\"taskList\":[{\"taskId\":\"8b518c79693849a98a714c0800529c25\",\"itemId\":\"5549bd7421564fd9b6550dc9a7bba948\",\"userId\":\"e544f554a4b6467d8887f52e2ff69506\",\"userName\":\"陈棋旭\",\"createTime\":1681735476900,\"duration\":1.0,\"actualDateStart\":1681740757429,\"actualDateEnd\":1681789652236,\"description\":\"产品经理要求，完成实时中台-B域-模型分析设计跟进与协调\",\"isCrossTeamTask\":0,\"userStoryId\":\"f6d7f9e360a5437ea44e11d954c658b4\",\"status\":3}],\"isDelay\":0},{\"itemId\":\"1e103dd67d0d4643b596b9d8b62dba5e\",\"name\":\"实时中台-B域模型分析设计跟进与协调0417-v3\",\"description\":\"实时中台-B域模型分析设计跟进与协调0417-v3\",\"createUserId\":\"e544f554a4b6467d8887f52e2ff69506\",\"duration\":1.0,\"isPrint\":false,\"position\":{\"columnId\":\"297bf1e466ef4a6595f892f074784cc3\",\"statusColumnId\":2,\"taskId\":\"7159807bbc8342abb61b009caee321de\",\"sort\":2},\"messageCount\":0,\"blockStatus\":0,\"taskList\":[{\"taskId\":\"7159807bbc8342abb61b009caee321de\",\"itemId\":\"1e103dd67d0d4643b596b9d8b62dba5e\",\"userId\":\"e544f554a4b6467d8887f52e2ff69506\",\"userName\":\"陈棋旭\",\"createTime\":1681735517754,\"duration\":1.0,\"description\":\"实时中台-B域模型分析设计跟进与协调0417-v3\",\"isCrossTeamTask\":0,\"userStoryId\":\"f6d7f9e360a5437ea44e11d954c658b4\",\"status\":1}],\"isDelay\":0}],\"hasIterationHistory\":false,\"taskCardPositionPriority\":0},{\"itemId\":\"cd0f116f73a44602a6797abc59263d5c\",\"userStoryId\":\"058f7188e66c4807966180fdd1da1a8c\",\"userStoryCode\":\"云边上报-内部联调0417\",\"userStorySeq\":\"P3648\",\"description\":\"产品经理要求，完成云边上报-内部联调\",\"agilePlanId\":\"de8531c6e2e5472cba283b248f5faa89\",\"priority\":\"3\",\"priorityName\":\"高\",\"type\":\"2\",\"status\":\"2\",\"scale\":\"5\",\"isPrint\":false,\"responsiblePerson\":\"e544f554a4b6467d8887f52e2ff69506\",\"responsiblePersonName\":\"陈棋旭\",\"position\":{\"columnId\":\"ccb4155892ba40cb9b516c501f8b280f\",\"statusColumnId\":1,\"sort\":1},\"taskCardList\":[{\"itemId\":\"14d508d072964127b4020b7925cb606d\",\"name\":\"云边上报-内部联调0417-v2\",\"description\":\"云边上报-内部联调0417-v2\",\"createUserId\":\"e544f554a4b6467d8887f52e2ff69506\",\"duration\":1.0,\"isPrint\":false,\"position\":{\"columnId\":\"297bf1e466ef4a6595f892f074784cc3\",\"statusColumnId\":2,\"taskId\":\"6bccedd6831a4535bb21e36c1cc82d83\",\"sort\":1},\"messageCount\":0,\"blockStatus\":0,\"taskList\":[{\"taskId\":\"6bccedd6831a4535bb21e36c1cc82d83\",\"itemId\":\"14d508d072964127b4020b7925cb606d\",\"userId\":\"e544f554a4b6467d8887f52e2ff69506\",\"userName\":\"陈棋旭\",\"createTime\":1681735503960,\"duration\":1.0,\"description\":\"云边上报-内部联调0417-v2\",\"isCrossTeamTask\":0,\"userStoryId\":\"058f7188e66c4807966180fdd1da1a8c\",\"status\":1}],\"isDelay\":0},{\"itemId\":\"9f84dee1b281470294ad66414a067d09\",\"name\":\"云边上报-内部联调0417\",\"description\":\"产品经理要求，完成云边上报-内部联调\",\"createUserId\":\"e544f554a4b6467d8887f52e2ff69506\",\"duration\":1.0,\"isPrint\":false,\"position\":{\"columnId\":\"e00d2e8c07ad46af9474a2f65f2d0d46\",\"statusColumnId\":4,\"taskId\":\"29ace76804f44773b5bc7da5d0801f41\",\"sort\":1},\"messageCount\":0,\"blockStatus\":0,\"taskList\":[{\"taskId\":\"29ace76804f44773b5bc7da5d0801f41\",\"itemId\":\"9f84dee1b281470294ad66414a067d09\",\"userId\":\"e544f554a4b6467d8887f52e2ff69506\",\"userName\":\"陈棋旭\",\"createTime\":1681735496359,\"duration\":1.0,\"actualDateStart\":1681789663243,\"actualDateEnd\":1682059565985,\"description\":\"产品经理要求，完成云边上报-内部联调\",\"isCrossTeamTask\":0,\"userStoryId\":\"058f7188e66c4807966180fdd1da1a8c\",\"status\":3}],\"isDelay\":0}],\"hasIterationHistory\":false,\"taskCardPositionPriority\":0}],\"succ\":true}";
        BoardDataBean boardDataBean = JSON.parseObject(json, BoardDataBean.class);
        // 故事
        for (BoardDataRespData boardDataRespData : boardDataBean.getRespData()) {
            logger.info("Description={}", boardDataRespData.getDescription());
            // 任务卡片
            for (TaskCard taskCard : boardDataRespData.getTaskCardList()) {
                BoardDataRespPosition taskCardPosition = taskCard.getPosition();
                logger.info("ItemId={}, Name={}, StatusColumnId={}, Sort={}"
                        , taskCard.getItemId(), taskCard.getName()
                        , taskCardPosition.getStatusColumnId(), taskCardPosition.getSort());
                for (Task task : taskCard.getTaskList()) {
                    // Status=1表示待办任务
                    // Status=2表示进行中(开发和测试通用)
                    // Status=3表示已完成(开发和测试通用)
                    logger.info("Status={}, UserStoryId={}", task.getStatus(), task.getUserStoryId());
                }
            }
        }

        String moveJson = "{\"respResult\":\"1\",\"respData\":{\"itemId\":\"9f84dee1b281470294ad66414a067d09\",\"position\":{\"seqId\":\"59ddffbbeff84f4cae369217f7ac0e3b\",\"itemId\":\"9f84dee1b281470294ad66414a067d09\",\"userStoryId\":\"058f7188e66c4807966180fdd1da1a8c\",\"columnId\":\"411625364d1b478a9c6df82420d4913b\",\"statusColumnId\":3,\"createTime\":1682060157379,\"createUser\":\"e544f554a4b6467d8887f52e2ff69506\",\"taskId\":\"29ace76804f44773b5bc7da5d0801f41\",\"objStatus\":0,\"sort\":1},\"messageCount\":0,\"taskList\":[{\"taskId\":\"29ace76804f44773b5bc7da5d0801f41\",\"itemId\":\"9f84dee1b281470294ad66414a067d09\",\"userId\":\"e544f554a4b6467d8887f52e2ff69506\",\"userName\":\"陈棋旭\",\"taskName\":\"云边上报-内部联调0417\",\"duration\":1.0,\"actualDateStart\":1681789663243,\"actualDateEnd\":1682059565985,\"description\":\"产品经理要求，完成云边上报-内部联调\",\"isCrossTeamTask\":0,\"userStoryId\":\"058f7188e66c4807966180fdd1da1a8c\",\"status\":2}],\"isDelay\":0},\"succ\":true}";
        MoveCardRespBean moveCardRespBean = JSON.parseObject(moveJson, MoveCardRespBean.class);
        TaskCard taskCard = moveCardRespBean.getRespData();
        BoardDataRespPosition taskCardPosition = taskCard.getPosition();
        logger.info("Sort={}", taskCardPosition.getSort());
    }

    @Test
    public void AgilePlanByTeamIdRespBean() {
        StringBuilder sb = new StringBuilder();
        sb.append("{}");
        AgilePlanByTeamIdRespBean agilePlanByTeamIdRespBean = JSON.parseObject(sb.toString(), AgilePlanByTeamIdRespBean.class);
        List<AgilePlanByTeamIdRespData> agilePlanByTeamIdRespDataList = new ArrayList<>();
        if (agilePlanByTeamIdRespBean.getRespData() != null) {
            for (AgilePlanByTeamIdRespData agilePlanByTeamIdRespData : agilePlanByTeamIdRespBean.getRespData()) {
                String name = agilePlanByTeamIdRespData.getName();
                if (name.contains("DAG") && name.contains("2023")) {
                    agilePlanByTeamIdRespDataList.add(agilePlanByTeamIdRespData);
                }
            }
            Collections.sort(agilePlanByTeamIdRespDataList);
            // 获取最新计划
            if (agilePlanByTeamIdRespDataList.size() > 0) {
                AgilePlanByTeamIdRespData newPlan = agilePlanByTeamIdRespDataList.get(0);
                logger.info("AgilePlanId={}, Name={}, PlanDateStart={}"
                        , newPlan.getAgilePlanId()
                        , newPlan.getName()
                        , TimeUtil.formatTime(newPlan.getPlanDateStart()));
            }
        }
    }

    @Test
    public void yearTest() {
        String year = TimeUtil.formatTime(System.currentTimeMillis(), "YYYY");
        logger.info("{}", year);
    }

    private AgileTeamMemberEmployee parserAgileTeamMemberEmployee() throws IOException {
        FileUtil fileUtil = new FileUtil();
        final StringBuilder sb = new StringBuilder();
        AgileTeamMemberEmployee agileTeamMemberEmployee = null;
        try {
            fileUtil.setReader(getClass().getResourceAsStream("/AgileTeamMemberEmployee.json"));
            fileUtil.read(new FileCount() {
                @Override
                public void run(String content) throws IOException {
                    sb.append(content);
                }
            });
            agileTeamMemberEmployee = JSON.parseObject(sb.toString(), AgileTeamMemberEmployee.class);
        } finally {
            fileUtil.closeRead();
        }
        return agileTeamMemberEmployee;
    }

    private void startWebSocketClient(String url) throws URISyntaxException {
        WebSocketClient webSocketClient = new WebSocketClient(new URI(url)
                , new Draft_6455()) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                logger.info("websocket客户端和服务器连接成功");
            }

            @Override
            public void onMessage(String message) {
                logger.info("websocket客户端收到消息={}", message);
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                logger.info("websocket客户端退出连接");
            }

            @Override
            public void onError(Exception e) {
                logger.info("websocket客户端和服务器连接发生错误={}", e.getMessage());
            }
        };
        webSocketClient.connect();
    }
}