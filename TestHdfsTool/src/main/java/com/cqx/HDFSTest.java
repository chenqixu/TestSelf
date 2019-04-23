package com.cqx;

import com.alibaba.fastjson.JSON;
import com.cqx.common.option.OptionsTool;
import com.cqx.common.utils.file.FileUtil;
import com.cqx.common.utils.system.SleepUtil;
import com.cqx.hdfs.bean.HdfsToolBean;
import com.cqx.hdfs.bean.WriterBean;
import com.cqx.util.AppConst;
import com.cqx.util.HdfsTool;
import com.cqx.util.HdfsToolFactory;
import com.cqx.write.WriteHdfs;

import java.io.IOException;
import java.util.List;

/**
 * HDFSTest
 *
 * @author chenqixu
 */
public class HDFSTest {

    private WriteHdfs writeHdfs;
    private HdfsToolBean hdfsToolBean;
    private HdfsToolFactory hdfsToolFactory;

    private HDFSTest() {
    }

    public static HDFSTest builder() {
        return new HDFSTest();
    }

    public static void main(String[] args) throws Exception {
        if (HdfsTool.isWindow()) {
            args = new String[]{"-h", "edc_base",
                    "-p", "D:\\tmp\\test.log",
                    "-c", "d:/tmp",
                    "-t", AppConst.APPEND_TYPE
            };
            args = new String[]{"-h", "edc_base",
//                    "-p", "D:\\tmp\\data\\orcouputnull\\hdfsappend.txt",
                    "-p", "D:\\tmp\\logs\\getData.log",
                    "-c", "d:/tmp",
                    "-t", AppConst.WRITE_TYPE,
                    "-rc", "GBK",
                    "-rs", "0"
//                    "-f", "false",
//                    "-lc", "100000"
            };
        }

//        //杀掉进程
//        KillHandler killHandler = new KillHandler();
//        killHandler.registerSignal("TERM");

        HdfsToolBean hdfsToolBean = new OptionsTool().parser(args, HdfsToolBean.class);
        HdfsTool.setHadoopUser(hdfsToolBean.getHadoop_user());
        HDFSTest.builder().setHdfsToolBean(hdfsToolBean).run();
    }

    private void run() throws Exception {
        writeHdfs = new WriteHdfs(hdfsToolBean);
        try {
            switch (hdfsToolBean.getType()) {
                case AppConst.APPENDZKJSON_TYPE:
                    dealZk(hdfsToolBean.getPath(), hdfsToolBean.getReadCode(), Integer.valueOf(hdfsToolBean.getRetryseq()));
                    break;
                case AppConst.WRITE_TYPE:
                    writeHdfs.setPath(hdfsToolBean.getPath());
                    writeHdfs.exec();
                    break;
                case AppConst.APPENDNOTCLOSE_TYPE:
                    writeHdfs.appendNotClose(hdfsToolBean.getPath());
                    break;
                case AppConst.APPEND_TYPE:
                    writeHdfs.append(hdfsToolBean.getPath(), 0);
                    break;
                case AppConst.SLEEP_TYPE:
                    SleepUtil.sleepSecond(Integer.valueOf(hdfsToolBean.getRetryseq()));
                    break;
                case AppConst.COPY_FROM_LOCAL:
                    hdfsToolFactory = HdfsToolFactory.builder(hdfsToolBean.getConf_path());
//                    String src = "";
//                    String dst = "";
//                    hdfsToolFactory.copyFromLocalFile(true, false, src, dst);
                    break;
                default:
                    break;
            }
        } finally {
            writeHdfs.close();
            hdfsToolFactory.close();
        }
    }

    private void dealZk(String file, String readCode, int seq) {
        FileUtil fileUtil = new FileUtil();
        List<String> list = fileUtil.read(file, readCode);
        String jsonstr = list.get(0);
        List<WriterBean> resultlist = JSON.parseArray(jsonstr, WriterBean.class);
        for (WriterBean writerBean : resultlist) {
            writeHdfs.append(writerBean.getPathAndFileName() + ".tmp", seq);
        }
    }

    public HDFSTest setHdfsToolBean(HdfsToolBean hdfsToolBean) {
        this.hdfsToolBean = hdfsToolBean;
        return this;
    }
}
