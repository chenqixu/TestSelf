package com.bussiness.bi.bigdata.thread.local;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NamedInheritableThreadLocal;
import org.springframework.core.NamedThreadLocal;
import org.springframework.web.context.request.RequestAttributes;

/**
 * RequestContextHolder
 *
 * @author chenqixu
 */
public class RequestContextHolder {

    private static final Logger logger = LoggerFactory.getLogger(RequestContextHolder.class);
    private static final ThreadLocal<RequestAttributes> requestAttributesHolder = new NamedThreadLocal("Request attributes");
    private static final ThreadLocal<RequestAttributes> inheritableRequestAttributesHolder = new NamedInheritableThreadLocal("Request context");

    public static RequestAttributes getRequest() {
        logger.info("requestAttributesHolder：{}，inheritableRequestAttributesHolder：{}",
                requestAttributesHolder, inheritableRequestAttributesHolder);
        RequestAttributes attributes = (RequestAttributes) requestAttributesHolder.get();
        if (attributes == null) {
            attributes = (RequestAttributes) inheritableRequestAttributesHolder.get();
        }
        return attributes;
    }

    public static void getOtherRequest() {
        ThreadLocal<Integer> integerThreadLocal = new MyIntegerThreadLocal<>(1);
        integerThreadLocal.set(2);
        Integer other = integerThreadLocal.get();
        logger.info("integerThreadLocal：{}，other：{}", integerThreadLocal, other);
    }

}
