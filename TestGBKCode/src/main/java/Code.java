import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * Code
 *
 * @author chenqixu
 */
public class Code {

    private static final Logger logger = Logger.getLogger(Code.class);
    //日志配置文件
    public static String LOG4J_CONF_FILE_PATH = "../conf/log4j-code.properties";

    public static void main(String[] args) {
        //加载log4j配置文件
        System.setProperty("log_dir", "../log/");
        PropertyConfigurator.configure(LOG4J_CONF_FILE_PATH);
        logger.info("测试");
    }
}
