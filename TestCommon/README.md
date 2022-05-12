#公共jar

##版本变化说明
* ClassUtil缺陷修复，jar包无法正常匹配package导致的多匹配问题 [common-20220426](#common-20220426)
* RedisFactory新增incr和hincrBy功能，用于获取分布式的唯一自增ID [common-20220427](#common-20220427)
* JDBC测试JavaBean方式插入oracle.Clob，结论：String类型就可以，使用buildClob也是可以的 [common-20220512](#common-20220512)

###common-20220426
```
ClassUtil缺陷修复，jar包无法正常匹配package导致的多匹配问题
1、此问题只会发生在需要做两个及两个以上识别的情况
比如：
    //扫描所有有AnnoRule注解的类
    Set<Class<?>> classSet = classUtil.getClassSet("com.xx.impl", AnnoRule.class);
    
    //扫描所有有AnnoRule注解的类
    Set<Class<?>> classSet = classUtil.getClassSet("com.xx.dvimpl", AnnoRule.class);
虽然参数传了不同的package name，但是没有根据package name进行区分，导致类初始化异常
```
###common-20220427
```
RedisFactory新增incr和hincrBy功能，用于获取分布式的唯一自增ID
1、验证过程参考com.cqx.common.utils.redis.RedisFactoryTest的concurrentINCRTest方法
2、使用方法
    使用RedisFactory.builder构造一个客户端对象RedisClient
    this._redisClient = RedisFactory.builder()
            .setIp_ports("10.1.8.200:10000,10.1.8.201:10000,10.1.8.202:10000")
            .setMode(RedisFactory.CLUSTER_MODE_TYPE)
            .setPipeline(false)
            .build();
    调用对应的hincrBy即可，1L表示每次自增幅度为1
    _redisClient.hincrBy(key, field, 1L);
```
###common-20220512
```
JDBC测试JavaBean方式插入oracle.Clob，结论：String类型就可以，使用buildClob也是可以的
1、使用String方式
javabean中对应的Clob字段类型设置成String就可以了
2、使用buildClob方式
javabean中对应的Clob字段类型使用@DB_StrToClob声明，类型使用String
使用方法即可自动调用buildClob：<T> int executeBatch(String sql, List<T> tList, Class<T> beanCls, String fields) throws Exception;
```
