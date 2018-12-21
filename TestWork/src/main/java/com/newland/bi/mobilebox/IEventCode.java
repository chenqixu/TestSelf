package com.newland.bi.mobilebox;

/**
 * 事件接口
 *
 * @author chenqixu
 */
public interface IEventCode {
    /**
     * 事件编码
     *
     * @return
     */
    int getCode();

    /**
     * 事件名称
     *
     * @return
     */
    String getName();

    /**
     * 必须提供toString的实现
     *
     * <pre>
     * &#064;Override
     * public String toString() {
     * 	   return String.format(&quot;Code:[%s], Name:[%s]. &quot;, this.code, this.name);
     * }
     * </pre>
     *
     * @return
     */
    String toString();
}
