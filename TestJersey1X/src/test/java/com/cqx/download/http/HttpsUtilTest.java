package com.cqx.download.http;

import com.cqx.bean.RestParam;
import com.cqx.download.yaoqi.FileUtil;
import org.junit.Before;
import org.junit.Test;

public class HttpsUtilTest {

    private HttpsUtil httpsUtil = new HttpsUtil();
    private FileUtil fileUtil = new FileUtil();

    @Before
    public void setUp() {
        fileUtil.setTitle("TEST");
    }

    @Test
    public void httpRequest() {
//        String url = "https://m.yaoqi99.com/mh/list_1_2.html";//首页
//        String url = "https://m.yaoqi99.com/mh/1727.html";//图书
        String url = "https://m.yaoqi99.com/mh/541_9.html";//图书
        Object indexObj = httpsUtil.httpRequest(new RestParam(
                url,
                "GET", null, "https", "", "",
                "", "",
                "", ""), "string", null);
        System.out.println(indexObj);
//        String b = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\"><html xmlns=\"http://www.w3.org/1999/xhtml\"><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" /><title>邪恶少女色系漫画全集朋友的母亲_日本漫画_妖气漫画</title><meta name=\"keywords\" content=\"色系,邪恶,全集,母亲,少女,漫画,朋友\" /><meta name=\"description\" content=\"妖气漫画日本漫画栏目，为您呈现邪恶少女色系漫画全集朋友的母亲，如果您喜欢记得常来妖气漫画日本漫画频道。\" /><meta name=\"viewport\" content=\"width=device-width,initial-scale=1.0,maximum-scale=1.0,minimum-scale=1.0,user-scalable=no\"><meta http-equiv=\"Cache-Control\" content=\"no-transform\" /> <meta http-equiv=\"Cache-Control\" content=\"no-siteapp\" /><link href=\"/css/all.css\" rel=\"stylesheet\" type=\"text/css\"><script src=\"/js/3g.js\" type=\"text/javascript\"></script></head><body><header><div id=\"topbar\"><a class=\"home\" href=\"/\"></a><span><a href=\"https://m.cyaoqi.com\" title=\"妖气漫画\">妖气漫画 m.cyaoqi.com</a></span><a id=\"catebar\" href=\"#\">导航</a></div><div id=\"menu\" style=\"display: none;\">  <ul>      <li><a href=\"/mh/\" title=\"少女漫画\">少女漫画</a></li>      <li><a href=\"/gif/\" title=\"邪恶动图\">邪恶动图</a></li>                         <li><a href=\"/sxjt/\" title=\"色系军团\">色系军团</a></li>                         <li><a href=\"/kalie/\" title=\"卡列漫画\">卡列漫画</a></li>                         <li><a href=\"/huanken/\" title=\"幻啃漫画\">幻啃漫画</a></li>                         <li><a href=\"/selie/\" title=\"色列漫画\">色列漫画</a></li>                         <li><a href=\"/sexiaozu/\" title=\"色小组\">色小组</a></li>                          <li><a href=\"/neihan/\" title=\"内涵漫画\">内涵漫画</a></li>  </ul></div></header><main>  <section>   <div class=\"adding\">  <script language=\"javascript\">adding_se();</script>  </div>  <div class=\"ad1\">  <!-- head 部分 -->   </div>  <div class=\"location\">    <h3><a href='https://www.cyaoqi.com/'>妖气漫画</a> > <a href='/mh/'>日本漫画</a> > 正文</h3>  </div>  <article class=\"pd10\">  <h1>邪恶少女色系漫画全集朋友的母亲</h1>  <div class=\"ad2\">  <script language=\"javascript\">ad2();</script>  </div>  <div class=\"adw1\">  <script language=\"javascript\">adw1();</script>  </div> <div class=\"wenshang\">  <script language=\"javascript\">wenshang();</script>  </div>  <div class=\"content\">                     \t<p style=\"text-align: center;\"><a href='1727_2.html'><img alt=\"邪恶少女色系漫画全集朋友的母亲\" src=\"http://3nlg.lzsysj.com/uploads/allimg/170406/p5ayy2lxn15829.jpg\" /></a></p>                                                              </div>  <div class=\"adw2\">  <script language=\"javascript\">adw2();</script>  </div>   <div class=\"ad2\">  <script language=\"javascript\">adw3();</script>  </div>  <div class=\"inc_page\"><a>共207页: </a><a href='#'>上一页</a><span class=\"current\"><a href='#'>1</a></span><a href='1727_2.html'>2</a><a href='1727_3.html'>3</a><a href='1727_4.html'>4</a><a href='1727_5.html'>5</a><a href='1727_6.html'>6</a><a href='1727_7.html'>7</a><a href='1727_8.html'>8</a><a href='1727_9.html'>9</a><a href='1727_10.html'>10</a><a href='1727_2.html'>下一页</a></div>  <div class=\"ad3\">  <script language=\"javascript\">ad3_se();</script>  </div>  <div class=\"shangxia\">                    \t\t<div class=\"shangxia1\">                            <a href=\"/mh/1728.html\">上一篇</a>                            </div>                            <div class=\"shangxia2\">                            <a  title=\"返回列表\" href=\"/mh/\">返回列表</a>                            </div>                            <div class=\"shangxia4\">                                <div id='tag0c4b5a539885ff5e7f8756dd066ce234'><a  title=\"随机一篇\" href=\"/mh/4885.html\">随机一篇</a>    </div>                            </div>                            <div class=\"shangxia3\">                            <a href=\"/mh/1726.html\">下一篇</a>                            </div>    </div>    <div class=\"ad4\">  <script language=\"javascript\">ad4();</script>  </div>  </article>  <div class=\"adw3\">  <script language=\"javascript\">adw10();</script>  </div>    <div class=\"c_box box\">      <h3><a href=\"/mh/\" class=\"fr fc1\">无翼鸟漫画</a><strong class=\"fc3\">大家都在看的“邪恶漫画”</strong></h3>      <div>        <ul class=\"pic\">             <div id='tag20e6e4fc2a1b2039b76f901dd74b156c'><li><a href=\"/mh/1733.html\" title=\"漫画之全集夺取的身体\"><img src=\"http://3nlg.lzsysj.com/uploads/allimg/170415/1-1F415204T70-L.jpg\" alt=\"漫画之全集夺取的身体\" border=\"0\"><p>漫画之全集夺取的身体</p></a></li><li><a href=\"/mh/1845.html\" title=\"漫画大全暴走教师\"><img src=\"http://3nlg.lzsysj.com/uploads/allimg/170605/1-1F6051019300-L.jpg\" alt=\"漫画大全暴走教师\" border=\"0\"><p>漫画大全暴走教师</p></a></li><li><a href=\"/mh/4185.html\" title=\"肉番漫画:皇女工やる\"><img src=\"http://3xe3.lzsysj.com/uploads/allimg/170326/1-1F3262304050-L.jpg\" alt=\"肉番漫画:皇女工やる\" border=\"0\"><p>肉番漫画:皇女工やる</p></a></li><li><a href=\"/mh/5488.html\" title=\"漫画少女漫画系列无遮挡邪恶\"><img src=\"http://3yml.lzsysj.com/uploads/allimg/161218/1-16121QP0530-L.jpg\" alt=\"漫画少女漫画系列无遮挡邪恶\" border=\"0\"><p>漫画少女漫画系列无遮挡邪恶</p></a></li><li><a href=\"/mh/3711.html\" title=\"【肉番漫画】拉提琺肉番福利漫画\"><img src=\"http://3xe3.lzsysj.com/uploads/allimg/160617/1-16061G33K00-L.jpg\" alt=\"【肉番漫画】拉提琺肉番福利漫画\" border=\"0\"><p>【肉番漫画】拉提琺肉番福利漫画</p></a></li><li><a href=\"/mh/1579.html\" title=\"里番h少女漫画之最爱的调查\"><img src=\"http://3nlg.lzsysj.com/uploads/allimg/170221/1-1F2210U0300-L.jpg\" alt=\"里番h少女漫画之最爱的调查\" border=\"0\"><p>里番h少女漫画之最爱的调查</p></a></li><li><a href=\"/mh/1010.html\" title=\"【妖气漫画】铃根犹豫地下室h里\"><img src=\"http://3nlg.lzsysj.com/uploads/allimg/160922/1-160922132J80-L.png\" alt=\"【妖气漫画】铃根犹豫地下室h里\" border=\"0\"><p>【妖气漫画】铃根犹豫地下室h里</p></a></li><li><a href=\"/mh/691.html\" title=\"【漫画】小早川肉番足控本\"><img src=\"http://3nlg.lzsysj.com/uploads/allimg/160630/1-1606301051390-L.jpg\" alt=\"【漫画】小早川肉番足控本\" border=\"0\"><p>【漫画】小早川肉番足控本</p></a></li><li><a href=\"/mh/3677.html\" title=\"【邪恶少女漫画】里番漫画之新婚\"><img src=\"http://3xe3.lzsysj.com/uploads/allimg/160514/1-1605142112170-L.jpg\" alt=\"【邪恶少女漫画】里番漫画之新婚\" border=\"0\"><p>【邪恶少女漫画】里番漫画之新婚</p></a></li>    </div></ul>    </div>                    </div>   <div class=\"adw4\">  <script language=\"javascript\">adw4();</script>  </div>  <div class=\"adwxx\">  <script language=\"javascript\">adwxx();</script>  </div>  <div class=\"adw99\">  <script language=\"javascript\">adw99();</script>  </div><div class=\"box1\">\t<div class=\"jinri\">\t\t<h3>相关邪恶漫画</h3>\t</div>\t<div class=\"wz\">\t\t\t<ul>\t\t\t <li><a href=\"/mh/6120.html\">漫画：爸妈上班了只剩下姐弟在家</a></li><li><a href=\"/mh/6116.html\">色系漫画：妹妹的LOVE</a></li><li><a href=\"/mh/6115.html\">漫画：她是一个比我长7岁的女性</a></li><li><a href=\"/mh/6112.html\">漫画：性感的老师之间的故事</a></li><li><a href=\"/mh/6109.html\">漫画：姐姐做梦和弟弟那个了</a></li><li><a href=\"/mh/6104.html\">漫画：喂美女来温存吧</a></li><li><a href=\"/mh/6102.html\">漫画：有希姐好舒服啊</a></li><li><a href=\"/mh/6100.html\">漫画：恋心之漂亮大美女</a></li>\t\t\t</ul>\t</div></div>  <div class=\"ad5\">  <script language=\"javascript\">ad5();</script>  </div>    </section></main> <div class=\"c_box box\"> <h3><a href=\"/mh/\" class=\"fr fc1\">日本漫画</a><strong class=\"fc3\">分类索引</strong></h3> <div> <ul class=\"pic\"> <div> <li><a href=\"/mh/\">邪恶漫画</a></li><li><a href=\"/gif/\">动态图</a></li> </div> </ul> </div> </div><footer><a href=\"https://m.cyaoqi.com/\" >妖气漫画</a>(记得保存我们的网址:m.cyaoqi.com，或者百度搜\"妖气漫画\")<p id=\"back-top\"><a href=\"#top\"><span>∧</span></a></p><script type=\"text/javascript\">$(document).ready(function(){\t$(\"#back-top\").hide();\t$(function () {\t\t$(window).scroll(function () {\t\t\tif ($(this).scrollTop() > 100) {\t\t\t\t$('#back-top').fadeIn();\t\t\t} else {\t\t\t\t$('#back-top').fadeOut();\t\t\t}\t\t});\t\t$('#back-top a').click(function () {\t\t\t$('body,html').animate({\t\t\t\tscrollTop: 0\t\t\t}, 800);\t\t\treturn false;\t\t});\t});});</script><script language=\"javascript\">gg_ar_800();</script><script language=\"javascript\">gg_ar_801();</script><script language=\"javascript\">gg_ar_803();</script><script language=\"javascript\">gg_ar_dl();</script><div style=\"display:none\"><script language=\"javascript\">cnzz();</script></div><div style=\"display:none\"><script language=\"javascript\">baidutj();</script></div><script language=\"javascript\">piaofu();</script><div style=\"display:none\"><script language=\"javascript\">lala();</script></div><script src=\"/js/jquery.js\" type=\"text/javascript\"></script><script> $(function(){    $('#catebar').click(function() {      $('#menu').slideToggle('fast');      return false;    });  })  $(document).click(function(event) {    $('#menu').hide('fast');  });    $(function(){    $('#manhua-gengduo').click(function() {      $('#menu-manhua').slideToggle('fast');      return false;    });  })  $(document).click(function(event) {    $('#menu-manhua').hide('fast');  });</script></body></html>";
//        YaoqiParser.getImageAndNext(b);
    }

    @Test
    public void hanzi() {
        String url = "https://hanyu.baidu.com/zici/s?wd=聚";//聚
        Object indexObj = httpsUtil.httpRequest(new RestParam(
                url,
                "GET", null, "https", "", "",
                "", "",
                "", ""), "string", null);
        System.out.println(indexObj);
    }

    @Test
    public void imgDownload() {
        String image_url = "http://3nlg.lzsysj.com/uploads/allimg/160424/xcb1g5pppl457.jpg";
        String image_urlIShttpOrhttps = "http";
        httpsUtil.httpRequest(new RestParam(
                image_url,
                "GET", null, image_urlIShttpOrhttps,
                "", "", "", "",
                "", ""), "file", fileUtil);
    }

}