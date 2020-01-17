import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * Code
 *
 * @author chenqixu
 */
public class Code {

    private static final Logger logger = Logger.getLogger(Code.class);
    //��־�����ļ�
    public static String LOG4J_CONF_FILE_PATH = "../conf/log4j-code.properties";

    public static void main(String[] args) {
        //����log4j�����ļ�
        System.setProperty("log_dir", "../log/");
        PropertyConfigurator.configure(LOG4J_CONF_FILE_PATH);
        logger.info("����");
    }
}
