package com.cqx.common.exception.base;

/**
 * 错误码的统一接口
 */
public interface ErrorCode {
    /**
     * 错误码编号<br>
     * 错误码编号有两部分组成，缩写（小写）+3位数字编号<br>
     * 1. 系统框架的错误。如数据聚合平台的框架错误：udap-001<br>
     * 2. 模块、组件的错误。模块、组件的缩写加上编号，如分布式采集的错误：cdc-001<br>
     * 建议，数字编号根据不同的类型进行分段提示<br>
     *
     * @return 错误码编号
     */
    String getCode();

    /**
     * 错误码描述，对错误内容的一句话说明。<br>
     * 具体的错误处理方法，可参见相应运维手册。<br>
     *
     * @return 错误码描述
     */
    String getDesc();

    /**
     * 必须提供toString的实现
     *
     * <pre>
     * &#064;Override
     * public String toString() {
     * 	   return String.format(&quot;Code:[%s], Description:[%s]. &quot;, this.code, this.describe);
     * }
     * </pre>
     */
    String toString();
}
