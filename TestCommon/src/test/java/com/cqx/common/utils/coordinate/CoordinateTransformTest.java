package com.cqx.common.utils.coordinate;

import com.alibaba.fastjson.JSON;
import com.cqx.common.test.TestBase;
import com.cqx.common.utils.http.HttpUtil;
import com.cqx.common.utils.jdbc.DBBean;
import com.cqx.common.utils.jdbc.JDBCUtil;
import com.cqx.common.utils.jdbc.ParamsParserUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptException;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CoordinateTransformTest extends TestBase {
    private static final Logger logger = LoggerFactory.getLogger(CoordinateTransformTest.class);
    private HttpUtil httpUtil;
    // 经度，Longtitude
    private double lng = 114.21892734521;
    // 纬度，Latitude
    private double lat = 29.575429778924;
    // 参数
    private Map param;

    @Before
    public void setUp() throws Exception {
        httpUtil = new HttpUtil();
        param = getParam("jdbc.yaml");
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * <table>
     * <thead>
     * <tr>
     * <td>参数名称</td>
     * <td>含义</td>
     * </tr>
     * </thead>
     * <tbody>
     * <tr>
     * <td>coords</td>
     * <td>需转换的源坐标，多组坐标以“;”分隔<br/>（经度，纬度）</td>
     * </tr>
     * <tr>
     * <td>ak</td>
     * <td>开发者密钥</td>
     * </tr>
     * <tr>
     * <td>from</td>
     * <td>源坐标类型：<br/>
     * &nbsp;&nbsp;&nbsp;&nbsp;1：GPS标准坐标；<br/>
     * &nbsp;&nbsp;&nbsp;&nbsp;2：搜狗地图坐标；<br/>
     * &nbsp;&nbsp;&nbsp;&nbsp;3：火星坐标（gcj02），即高德地图、腾讯地图和MapABC等地图使用的坐标；<br/>
     * &nbsp;&nbsp;&nbsp;&nbsp;4：3中列举的地图坐标对应的墨卡托平面坐标;<br/>
     * &nbsp;&nbsp;&nbsp;&nbsp;5：百度地图采用的经纬度坐标（bd09ll）；<br/>
     * &nbsp;&nbsp;&nbsp;&nbsp;6：百度地图采用的墨卡托平面坐标（bd09mc）;<br/>
     * &nbsp;&nbsp;&nbsp;&nbsp;7：图吧地图坐标；<br/>
     * &nbsp;&nbsp;&nbsp;&nbsp;8：51地图坐标；</td>
     * </tr>
     * <tr>
     * <td>to</td>
     * <td>目标坐标类型：<br/>
     * &nbsp;&nbsp;&nbsp;&nbsp;3：火星坐标（gcj02），即高德地图、腾讯地图及MapABC等地图使用的坐标；<br/>
     * &nbsp;&nbsp;&nbsp;&nbsp;5：百度地图采用的经纬度坐标（bd09ll）；<br/>
     * &nbsp;&nbsp;&nbsp;&nbsp;6：百度地图采用的墨卡托平面坐标（bd09mc）；</td>
     * </tr>
     * </tbody>
     * </table>
     */
    @Test
    public void baiduAPI() {
        String url = "https://api.map.baidu.com/geoconv/v1/?" +
                "coords=" + lng + "," + lat +
                "&from=1&to=5&ak=ckiUPov3TQUfcQLwEq412AcfdXtsZCMu";
        String response = httpUtil.doGet(url);
        logger.info("response：{}", response);
        // 114.21892734521,29.575429778924
        // 5
        // {"status":0,"result":[{"x":114.2307519546763,"y":29.57908428837437}]}
        // 3
        // {"status":0,"result":[{"x":114.22430528428819,"y":29.57284912109375}]}
    }

    @Test
    public void BaiduApiResponseParser() {
        String jsonStr = "{\"status\":0,\"result\":[{\"x\":114.2307519546763,\"y\":29.57908428837437}]}";
        BD09 bd09 = JSON.parseObject(jsonStr, BD09.class);
        logger.info("status={}, x={}, y={}", bd09.getStatus(), bd09.getResult()[0].getX(), bd09.getResult()[0].getY());
    }

    @Test
    public void transformWGS84ToGCJ02() {
        double[] lngLat = CoordinateTransform.transformWGS84ToGCJ02(lng, lat);
        for (double d : lngLat) {
            logger.info("{}", d);
        }
        // 114.21892734521   29.575429778924
    }

    @Test
    public void transformWGS84ToBD09() throws ScriptException, NoSuchMethodException, FileNotFoundException {
        double[] lngLat = CoordinateTransform.transformWGS84ToBD09(lng, lat);
        for (double d : lngLat) {
            logger.info("{}", d);
        }
        // 114.22539195427781   29.5815853675143
    }

    @Test
    public void transformWGS84ToBD09_JavaScript() throws ScriptException, FileNotFoundException, NoSuchMethodException {
        // javascript
        CoordinateTransformJS js = new CoordinateTransformJS("d:/tmp/html/coordinate/");
        js.transformWGS84ToGCJ02(lng, lat);
    }

    @Test
    public void transformWGS84ToBD09_Python() {
        // python
        try (CoordinateTransformPython python = new CoordinateTransformPython("I:\\Document\\Workspaces\\Git\\TestShell\\2022\\wgs84to.py")) {
            python.transformWGS84ToBD09(lng, lat);
            python.transformWGS84ToGCJ02(lng, lat);
        }
    }

    @Test
    public void checkAPIAndTransform() {
        ParamsParserUtil paramsParserUtil = new ParamsParserUtil(param);
        DBBean dbBean = paramsParserUtil.getBeanMap().get("oracle11g_xdmart205_Bean");
        try (JDBCUtil jdbcUtil = new JDBCUtil(dbBean)) {
            // 清表
            jdbcUtil.executeUpdate("truncate table xdmart.ft_mid_sector_info_daily_check");
            // 查表
            List<CoordinateBean> coordinateBeanList = jdbcUtil.executeQuery(
                    "select longitude,latitude,site_lon_gcj02,site_lat_gcj02,site_lon_bd09,site_lat_bd09 from xdmart.ft_mid_sector_info_daily_mid"
                    , CoordinateBean.class);
            List<String> insertSqls = new ArrayList<>();
            String insertSql = "insert into xdmart.ft_mid_sector_info_daily_check(longitude,latitude,site_lon_gcj02,site_lat_gcj02,site_lon_bd09,site_lat_bd09,site_lon_bdapi,site_lat_bdapi) values(%s,%s,%s,%s,%s,%s,%s,%s)";
            for (CoordinateBean coordinateBean : coordinateBeanList) {
                // 调用API
                String url = "https://api.map.baidu.com/geoconv/v1/?" +
                        "coords=" + coordinateBean.getLongitude() + "," + coordinateBean.getLatitude() +
                        "&from=1&to=5&ak=ckiUPov3TQUfcQLwEq412AcfdXtsZCMu";
                String response = httpUtil.doGet(url);
                BD09 bd09 = JSON.parseObject(response, BD09.class);
                // 生产写表sql
                insertSqls.add(String.format(insertSql, coordinateBean.getLongitude(), coordinateBean.getLatitude()
                        , coordinateBean.getSite_lon_gcj02(), coordinateBean.getSite_lat_gcj02()
                        , coordinateBean.getSite_lon_bd09(), coordinateBean.getSite_lat_bd09()
                        , bd09.getResult()[0].getX(), bd09.getResult()[0].getY()));
                logger.info("lon={}, lat={}", coordinateBean.getLongitude(), coordinateBean.getLatitude());
                logger.debug("sql={}", insertSqls.get(insertSqls.size() - 1));
                // todo 只调用一次接口，如果有需求，请人工放开
                break;
            }
            if (insertSqls.size() > 0) {
                jdbcUtil.executeBatch(insertSqls);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}