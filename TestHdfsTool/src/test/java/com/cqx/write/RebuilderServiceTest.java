package com.cqx.write;

import com.cqx.hdfs.bean.AbstractHDFSWriter;
import com.cqx.hdfs.bean.WriterBean;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class RebuilderServiceTest {

    public static Logger logger = LoggerFactory.getLogger(RebuilderServiceTest.class);
    private RebuilderService<AbstractHDFSWriter, WriterBean> rebuilderService;
    private List<WriterBean> tmplist;

    @Before
    public void setUp() throws Exception {
        tmplist = new ArrayList<>();
        tmplist.add(WriterBean.builder().setPathAndFileName("/test/1.data"));
        tmplist.add(WriterBean.builder().setPathAndFileName("/test/2.data"));
        rebuilderService = RebuilderService.builder(tmplist);
        rebuilderService.setRebuilerDeal(new IRebuilder<AbstractHDFSWriter, WriterBean>() {
            @Override
            AbstractHDFSWriter preper() throws Exception {
                AbstractHDFSWriter abstractHDFSWriter = makeNewWriter(getV());
                setK(abstractHDFSWriter);
                return abstractHDFSWriter;
            }

            @Override
            void close() {
                closeAbsWriter(getK());
            }

            @Override
            void commit() {
                putWriter(getK());
            }
        });
    }

    @Test
    public void start() throws Exception {
        rebuilderService.start();
    }

    private AbstractHDFSWriter makeNewWriter(WriterBean writerBean) {
        AbstractHDFSWriter abstractHDFSWriter = AbstractHDFSWriter.builder();
        logger.info("makeNewWriter：writerbean：{}，makeNewWrite：{}", writerBean, abstractHDFSWriter);
        return abstractHDFSWriter;
    }

    private void closeAbsWriter(AbstractHDFSWriter abstractHDFSWriter) {
        logger.info("close：{}", abstractHDFSWriter);
    }

    private void putWriter(AbstractHDFSWriter abstractHDFSWriter) {
        logger.info("putWriter：{}", abstractHDFSWriter);
    }
}