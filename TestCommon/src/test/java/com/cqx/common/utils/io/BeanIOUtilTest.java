package com.cqx.common.utils.io;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BeanIOUtilTest {

    private BeanIOUtil<TestBean> beanIOUtil;

    @Before
    public void setUp() throws Exception {
        beanIOUtil = new BeanIOUtil<>("d:\\tmp\\data\\gejie\\success.txt", "UTF-8", TestBean.class);
    }

    @Test
    public void other() throws IOException {
        BeanIOUtil<TestBean> beanIOUtil = new BeanIOUtil<>(TestBean.class);
        beanIOUtil.start_save("d:\\tmp\\data\\gejie\\1.txt", "GBK");
        beanIOUtil.saveBeanToFile(new TestBean("13500001111", "张三"));
        beanIOUtil.stop_save();
    }

    @Test
    public void saveBeanToFile() throws IOException {
        try {
            beanIOUtil.start_save();
            beanIOUtil.saveBeanToFile(new TestBean("13500001111", "张三"));
        } finally {
            beanIOUtil.stop_save();
        }
    }

    @Test
    public void saveListBeanToFile() throws IOException {
        try {
            beanIOUtil.start_save();
            List<TestBean> testBeanList = new ArrayList<>();
            testBeanList.add(new TestBean("13500001111", "张三"));
            testBeanList.add(new TestBean("13600002222", "李四"));
            testBeanList.add(new TestBean("13700003333", "王五"));
            beanIOUtil.saveListBeanToFile(testBeanList);
        } finally {
            beanIOUtil.stop_save();
        }
    }

    @Test
    public void readFileToBean() throws IOException {
        try {
            beanIOUtil.start_read();
            TestBean testBean = beanIOUtil.readFileToBean();
            System.out.println(testBean);
        } finally {
            beanIOUtil.stop_read();
        }
    }

    @Test
    public void readFileToListBean() throws IOException {
        try {
            beanIOUtil.start_read();
            List<TestBean> testBeanList = beanIOUtil.readFileToListBean();
            for (TestBean testBean : testBeanList)
                System.out.println(testBean);
        } finally {
            beanIOUtil.stop_read();
        }
    }

    public class TestBean {
        String id;
        String name;

        public TestBean(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String toString() {
            return "id：" + id + "，name：" + name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}