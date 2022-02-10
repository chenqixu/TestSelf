package com.cqx.common.utils.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <h2>维表映射Bean</h2>
 * 配置举例，dims表示根：
 * <pre>
 * dims:
 *   - name: "user_status"
 *     kvs:
 *       - key: "0"
 *         value: "正常"
 *       - key: "10"
 *         value: "单停"
 * </pre>
 *
 * @author chenqixu
 */
public class DimBean {
    private String name;
    private List<KVS> kvsList = new ArrayList<>();
    private Map<String, String> kvsMap = new HashMap<>();

    /**
     * yaml配置文件转Bean List
     *
     * @param yaml
     * @return
     */
    public static List<DimBean> yamlToBeanList(Map yaml) {
        List<DimBean> dimBeanList = new ArrayList<>();
        if (yaml != null) {
            Object dims = yaml.get("dims");
            ArrayList dimsList = (ArrayList) dims;
            for (Object dim : dimsList) {
                DimBean dimBean = new DimBean();
                Map dimMap = (Map) dim;
                dimBean.setName(dimMap.get("name").toString());
                ArrayList kvsList = (ArrayList) dimMap.get("kvs");
                for (Object kvs : kvsList) {
                    Map kvsMap = (Map) kvs;
                    dimBean.addKVS(kvsMap.get("key").toString(), kvsMap.get("value").toString());
                }
                dimBeanList.add(dimBean);
            }
        }
        return dimBeanList;
    }

    /**
     * yaml配置文件转Bean Map
     *
     * @param yaml
     * @return
     */
    public static Map<String, DimBean> yamlToBeanMap(Map yaml) {
        Map<String, DimBean> dimBeanMap = new HashMap<>();
        for (DimBean dimBean : yamlToBeanList(yaml)) {
            dimBeanMap.put(dimBean.getName(), dimBean);
        }
        return dimBeanMap;
    }

    public void addKVS(String key, String value) {
        kvsList.add(new KVS(key, value));
        kvsMap.put(key, value);
    }

    public String getValueByKey(String key) {
        String ret = kvsMap.get(key);
        if (ret == null || ret.length() == 0 || ret.trim().length() == 0) ret = key;
        return ret;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<KVS> getKvsList() {
        return kvsList;
    }

    public void setKvsList(List<KVS> kvsList) {
        this.kvsList = kvsList;
    }

    public Map<String, String> getKvsMap() {
        return kvsMap;
    }

    public class KVS {
        private String key;
        private String value;

        public KVS(String key, String value) {
            setKey(key);
            setValue(value);
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
