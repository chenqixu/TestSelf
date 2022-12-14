package com.cqx.common.utils.coordinate;

import com.cqx.common.utils.http.HttpUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptException;
import java.io.FileNotFoundException;

public class CoordinateTransformTest {
    private static final Logger logger = LoggerFactory.getLogger(CoordinateTransformTest.class);
    private HttpUtil httpUtil;
    // 经度，Longtitude
    private double lng = 114.21892734521;
    // 纬度，Latitude
    private double lat = 29.575429778924;

    @Before
    public void setUp() throws Exception {
        httpUtil = new HttpUtil();
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
    public void transformBD09ToGCJ02() {
    }

    @Test
    public void transformGCJ02ToBD09() {
    }

    @Test
    public void transformGCJ02ToWGS84() {
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
    public void transformBD09ToWGS84() {
    }

    @Test
    public void transformWGS84ToBD09() throws ScriptException, NoSuchMethodException, FileNotFoundException {
        double[] lngLat = CoordinateTransform.transformWGS84ToBD09(lng, lat);
        for (double d : lngLat) {
            logger.info("{}", d);
        }
        // 114.22539195427781   29.5815853675143
        // javascript
//        CoordinateTransformJS js = new CoordinateTransformJS("d:/tmp/html/coordinate/");
//        js.transformWGS84ToBD09(lng, lat);
        // python
        try (CoordinateTransformPython python = new CoordinateTransformPython("I:\\Document\\Workspaces\\Git\\TestShell\\2022\\wgs84to.py")) {
            python.transformWGS84ToBD09(lng, lat);
            python.transformWGS84ToGCJ02(lng, lat);
        }
    }
}