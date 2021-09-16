package com.newland.bi.bigdata.txt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文本处理
 *
 * @author chenqixu
 */
public class TextDeal {

    public void lib() {
        String lib = "activation-1.1," +
                "aircompressor-0.3," +
                "annotations-2.0.3," +
                "ant-1.9.1," +
                "antisamy-1.4.3," +
                "ant-launcher-1.9.1," +
                "antlr4-runtime-4.7.1," +
                "antlr-runtime-3.5.2," +
                "aopalliance-1.0," +
                "apacheds-i18n-2.0.0-M15," +
                "apacheds-kerberos-codec-2.0.0-M15," +
                "apache-log4j-extras-1.2.17," +
                "apache-mime4j-core-0.7.2," +
                "api-asn1-api-1.0.0-M20," +
                "api-util-1.0.0-M20," +
                "asm-3.1," +
                "asm-5.0.4," +
                "asm-commons-3.1," +
                "asm-tree-3.1," +
                "aviator-3.1.0," +
                "avro-1.7.6-cdh5.7.2," +
                "aws-java-sdk-core-1.10.6," +
                "aws-java-sdk-kms-1.10.6," +
                "aws-java-sdk-s3-1.10.6," +
                "axiom-api-1.2.13," +
                "axiom-impl-1.2.13," +
                "axis2-1.6.2," +
                "axis2-adb-1.6.2," +
                "axis2-kernel-1.6.2," +
                "axis2-transport-http-1.6.2," +
                "axis2-transport-local-1.6.2," +
                "batik-css-1.7," +
                "batik-ext-1.7," +
                "batik-util-1.7," +
                "bonecp-0.8.0.RELEASE," +
                "bsh-core-2.0b4," +
                "calcite-avatica-1.0.0-incubating," +
                "calcite-core-1.0.0-incubating," +
                "calcite-linq4j-1.0.0-incubating," +
                "cglib-nodep-3.2.5," +
                "commons-beanutils-1.9.3," +
                "commons-beanutils-core-1.8.0," +
                "commons-cli-1.2," +
                "commons-codec-1.6," +
                "commons-collections-3.2.2," +
                "commons-collections4-4.2," +
                "commons-compiler-2.7.6," +
                "commons-compress-1.15," +
                "commons-configuration-1.6," +
                "commons-configuration2-2.1.1," +
                "commons-configuration2-2.7," +
                "commons-daemon-1.0.13," +
                "commons-dbcp-1.4," +
                "commons-digester-1.8," +
                "commons-el-1.0," +
                "commons-fileupload-1.2," +
                "commons-httpclient-3.1," +
                "commons-io-2.5," +
                "commons-lang-2.6," +
                "commons-lang3-3.4," +
                "commons-logging-1.1.3," +
                "commons-math-2.1," +
                "commons-math3-3.1.1," +
                "commons-net-3.1," +
                "commons-pool-1.5.4," +
                "commons-pool2-2.6.1," +
                "commons-text-1.3," +
                "commons-validator-1.6," +
                "curator-client-2.7.1," +
                "curator-framework-2.7.1," +
                "curator-recipes-2.7.1," +
                "datanucleus-api-jdo-3.2.6," +
                "datanucleus-core-3.2.10," +
                "datanucleus-rdbms-3.2.9," +
                "db2jcc-1.4.2," +
                "db2jcc_license_cu-1.4.2," +
                "derby-10.11.1.1," +
                "disruptor-3.3.0," +
                "dnsns," +
                "dom4j-1.6.1," +
                "druid-1.1.5," +
                "edc-dataasset-service-1.0.0," +
                "edc-env-config-client-3.0," +
                "edtFTPj-pro-4.8.1," +
                "eigenbase-properties-1.1.4," +
                "esapi-2.1.0," +
                "expectit-core-0.7.0," +
                "fastjson-1.1.41," +
                "findbugs-annotations-1.3.9-1," +
                "ganymed-ssh2-262," +
                "gbase-connector-java-8.3.81.53," +
                "geronimo-activation_1.1_spec-1.0.2," +
                "geronimo-annotation_1.0_spec-1.1.1," +
                "geronimo-jaspic_1.0_spec-1.0," +
                "geronimo-javamail_1.4_spec-1.7.1," +
                "geronimo-jta_1.1_spec-1.1.1," +
                "geronimo-stax-api_1.0_spec-1.0.1," +
                "geronimo-ws-metadata_2.0_spec-1.1.2," +
                "greenplum-1.0," +
                "groovy-all-2.4.4," +
                "gson-2.2.4," +
                "guava-16.0.1," +
                "guice-3.0," +
                "guice-servlet-3.0," +
                "hadoop-annotations-3.1.1-hw-ei-302022," +
                "hadoop-auth-3.1.1-hw-ei-302022," +
                "hadoop-common-3.1.1-hw-ei-302022," +
                "hadoop-hdfs-3.1.1-hw-ei-302022," +
                "hadoop-hdfs-client-3.1.1-hw-ei-302022," +
                "hadoop-hdfs-datamovement-3.1.1-hw-ei-302022," +
                "hadoop-hdfs-httpfs-3.1.1-hw-ei-302022," +
                "hadoop-hdfs-native-client-3.1.1-hw-ei-302022," +
                "hadoop-hdfs-nfs-3.1.1-hw-ei-302022," +
                "hadoop-hdfs-rbf-3.1.1-hw-ei-302022," +
                "hadoop-huaweicloud-3.1.1-hw-42," +
                "hadoop-kms-3.1.1-hw-ei-302022," +
                "hadoop-mapreduce-client-app-3.1.1-hw-ei-302022," +
                "hadoop-mapreduce-client-common-3.1.1-hw-ei-302022," +
                "hadoop-mapreduce-client-core-3.1.1-hw-ei-302022," +
                "hadoop-mapreduce-client-jobclient-3.1.1-hw-ei-302022," +
                "hadoop-mapreduce-client-shuffle-3.1.1-hw-ei-302022," +
                "hadoop-nfs-3.1.1-hw-ei-302022," +
                "hadoop-plugins-8.0.2-302022," +
                "hadoop-yarn-api-3.1.1-hw-ei-302022," +
                "hadoop-yarn-client-3.1.1-hw-ei-302022," +
                "hadoop-yarn-common-3.1.1-hw-ei-302022," +
                "hadoop-yarn-server-common-3.1.1-hw-ei-302022," +
                "hamcrest-core-1.3," +
                "high-scale-lib-1.1.1," +
                "hive-beeline-3.1.0-hw-ei-302022," +
                "hive-cli-3.1.0-hw-ei-302022," +
                "hive-common-3.1.0-hw-ei-302022," +
                "hive-exec-3.1.0-hw-ei-302022," +
                "hive-hcatalog-core-3.1.0-hw-ei-302022," +
                "hive-jdbc-3.1.0-hw-ei-302022," +
                "hive-jdbc-handler-3.1.0-hw-ei-302022," +
                "hive-metastore-3.1.0-hw-ei-302022," +
                "hive-serde-3.1.0-hw-ei-302022," +
                "hive-service-3.1.0-hw-ei-302022," +
                "hive-service-rpc-3.1.0-hw-ei-302022," +
                "hive-shims-0.23-3.1.0-hw-ei-302022," +
                "hive-shims-3.1.0-hw-ei-302022," +
                "hive-shims-common-3.1.0-hw-ei-302022," +
                "hive-shims-scheduler-3.1.0-hw-ei-302022," +
                "hive-standalone-metastore-3.1.0-hw-ei-302022," +
                "htrace-core-3.2.0-incubating," +
                "htrace-core4-4.0.1-incubating," +
                "httpclient-4.2.5," +
                "httpcore-4.2.4," +
                "ImpalaJDBC41-2.5.36.1056GA," +
                "jackson-annotations-2.2.3," +
                "jackson-annotations-2.8.0," +
                "jackson-core-2.8.9," +
                "jackson-core-asl-1.8.8," +
                "jackson-databind-2.8.9," +
                "jackson-jaxrs-1.8.8," +
                "jackson-jaxrs-base-2.8.9," +
                "jackson-jaxrs-json-provider-2.8.9," +
                "jackson-mapper-asl-1.8.8," +
                "jackson-module-jaxb-annotations-2.8.9," +
                "jackson-xc-1.8.3," +
                "jamon-runtime-2.4.1," +
                "janino-2.7.6," +
                "jasper-compiler-5.5.23," +
                "jasper-runtime-5.5.23," +
                "javax.inject-1," +
                "java-xmlbuilder-0.4," +
                "jaxb-api-2.2.2," +
                "jaxb-impl-2.2.3-1," +
                "jaxen-1.1.3," +
                "jcodings-1.0.8," +
                "jdo-api-3.0.1," +
                "jdom-2.0.2," +
                "jedis-2.9.0," +
                "jersey-client-1.9," +
                "jersey-core-1.9," +
                "jersey-guice-1.9," +
                "jersey-json-1.9," +
                "jersey-server-1.9," +
                "jersey-servlet-1.14," +
                "jets3t-0.9.0," +
                "jettison-1.1," +
                "jetty-6.1.26.cloudera.4," +
                "jetty-all-7.6.0.v20120127," +
                "jetty-sslengine-6.1.26.cloudera.4," +
                "jetty-util-6.1.26.cloudera.4," +
                "jline-0.9.94," +
                "joda-time-2.5," +
                "joni-2.1.2," +
                "jopt-simple-4.9," +
                "jpam-1.1," +
                "jsch-0.1.49," +
                "jsp-2.1-6.1.14," +
                "jsp-api-2.1-6.1.14," +
                "jsp-api-2.1," +
                "jsr305-3.0.0," +
                "jsr311-api-1.0," +
                "jta-1.1," +
                "kafka_2.10-0.10.1.0," +
                "kafka-clients-0.10.1.0," +
                "leveldbjni-all-1.8," +
                "libfb303-0.9.2," +
                "libthrift-0.9.2," +
                "localedata," +
                "log4j-1.2.17," +
                "logredactor-1.0.3," +
                "lz4-1.3.0," +
                "lzo-core-1.0.5," +
                "mail-1.4.1," +
                "metrics-core-2.2.0," +
                "metrics-core-3.0.2," +
                "metrics-json-3.0.2," +
                "metrics-jvm-3.0.2," +
                "mysql-connector-java-5.1.39," +
                "neethi-3.0.2," +
                "nekohtml-1.9.12," +
                "netty-3.6.2.Final," +
                "netty-all-4.0.23.Final," +
                "nl-assets-tools-1.0," +
                "nl-bd-common-utils-1.0.0," +
                "nl-bd-dependency-hadoop-1.0.0-cdh5.7.2," +
                "nl-bd-expression-utils-1.0.0," +
                "nl-bd-jms-utils-2.0.0," +
                "nl-bd-mem-utils-1.0.0," +
                "nl-bd-model-1.0.1," +
                "nl-bi-util-1.1.0," +
                "nl-component-dataassets-1.0," +
                "nl-component-des-1.0," +
                "nl-component-Fujian-1.0," +
                "nl-component-FujianBI-CDRCheck-1.0," +
                "nl-component-FujianBI-common-1.0," +
                "nl-component-FujianBI-DBConvertDB-1.0," +
                "nl-component-FujianBI-DBCreateTable-1.0," +
                "nl-component-FujianBI-DBLinkDataSync-1.0," +
                "nl-component-FujianBI-ExTableLoad-1.0," +
                "nl-component-FujianBI-ExTableLoadV2-1.0," +
                "nl-component-FujianBI-FileToRedis-1.0," +
                "nl-component-FujianBI-FTPFileCheck-1.0," +
                "nl-component-FujianBI-HdfsFileLoadClean-1.0," +
                "nl-component-FujianBI-HDFSToTimesten-1.0," +
                "nl-component-FujianBI-LinuxExecCommand-1.0," +
                "nl-component-FujianBI-OracleTableSync-1.0," +
                "nl-component-FujianBI-OracleToKafka-1.0," +
                "nl-component-FujianBI-RedisToHDFS-1.0," +
                "nl-component-FujianBI-SqoopExp-1.0," +
                "nl-component-FujianBI-SqoopImp-1.0," +
                "nl-component-offline-analysis-1.0," +
                "nl-component-shell-1.0," +
                "nl-component-sparksql-1.0," +
                "nl-component-tools-1.0," +
                "nl-kafka-tool-1.0.1," +
                "nl-oozie-common-1.0," +
                "nl-oozie-core-1.0," +
                "nl-oozie-entry-1.0," +
                "nl-rt-mobilebox-redis-jdbc-1.0," +
                "nl-transform-common-1.1," +
                "objenesis-2.6," +
                "ojdbc8-12.2.0.1," +
                "opencsv-2.3," +
                "opencsv-4.3.2," +
                "oro-2.0.8," +
                "paranamer-2.3," +
                "parquet-hadoop-bundle-1.5.0-cdh5.7.2," +
                "pentaho-aggdesigner-algorithm-5.1.5-jhyde," +
                "protobuf-java-2.5.0," +
                "protostuff-api-1.0.8," +
                "protostuff-collectionschema-1.0.8," +
                "protostuff-core-1.0.8," +
                "protostuff-runtime-1.0.8," +
                "re2j-1.1," +
                "reflectasm-1.11.3," +
                "scala-library-2.10.6," +
                "servlet-api-2.5-6.1.14," +
                "servlet-api-2.5," +
                "slf4j-api-1.7.5," +
                "slf4j-log4j12-1.7.5," +
                "snappy-java-1.1.1.6," +
                "spring-beans-3.2.0.RELEASE," +
                "spring-context-3.2.0.RELEASE," +
                "spring-core-3.2.0.RELEASE," +
                "spring-expression-3.2.0.RELEASE," +
                "spring-web-3.2.0.RELEASE," +
                "ST4-4.0.4," +
                "stax2-api-4.2.1," +
                "stax2-api-4.2," +
                "stax-api-1.0.1," +
                "stax-api-1.0-2," +
                "sunec," +
                "sunjce_provider," +
                "sunpkcs11," +
                "ttjdbc6-1.0.0," +
                "velocity-1.5," +
                "woden-api-1.0M9," +
                "woden-impl-commons-1.0M9," +
                "woden-impl-dom-1.0M9," +
                "woodstox-core-6.2.1," +
                "woodstox-core-asl-4.4.1," +
                "wsdl4j-1.6.2," +
                "xalan-2.7.0," +
                "xercesImpl-2.9.1," +
                "xml-apis-1.0.b2," +
                "xml-apis-ext-1.3.04," +
                "xmlenc-0.52," +
                "xmlpull-1.1.3.1," +
                "XmlSchema-1.4.7," +
                "xom-1.2.5," +
                "xpp3_min-1.1.4c," +
                "xstream-1.4.9," +
                "zipfs," +
                "zkclient-0.9," +
                "zookeeper-3.5.6-hw-ei-302022," +
                "zookeeper-jute-3.5.6-hw-ei-302022";
        String[] libs = lib.split(",");
        Map<String, List<String>> maps = new HashMap<>();
        for (String tmp : libs) {
            String[] tmps = tmp.split("-");
            StringBuilder sb = new StringBuilder();
            String version = null;
            for (int i = 0; i < tmps.length; i++) {
                if ((i + 1) == tmps.length) {
                    version = tmps[i];
                } else {
                    sb.append(tmps[i]).append("-");
                }
            }
            String name = sb.toString();
            List<String> versionList = maps.get(name);
            if (versionList == null) {
                versionList = new ArrayList<>();
                versionList.add(version);
                maps.put(name, versionList);
            } else {
                versionList.add(version);
            }
        }

        for (Map.Entry<String, List<String>> entry : maps.entrySet()) {
            String name = entry.getKey();
            List<String> versionList = entry.getValue();
            if (versionList.size() > 1) System.out.println(name + "，versionList：" + versionList);
        }
    }
}
