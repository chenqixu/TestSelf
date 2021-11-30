package com.cqx.common.utils.serialize.gson;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 用来处理Gson.fromJson的泛型，比如List&lt;T&gt;，或者Map&lt;K, V&gt;
 *
 * @author chenqixu
 */
public class ParameterizedTypeImpl implements ParameterizedType {
    private final Class raw;
    private final Type[] args;

    public ParameterizedTypeImpl(Class raw, Type[] args) {
        this.raw = raw;
        this.args = args != null ? args : new Type[0];
    }

    /**
     * 返回List&lt;User&gt;里的User，所以这里返回[User.clas]
     *
     * @return
     */
    @Override
    public Type[] getActualTypeArguments() {
        return args;
    }

    /**
     * List&lt;User&gt;里的List，所以返回值是List.class
     *
     * @return
     */
    @Override
    public Type getRawType() {
        return raw;
    }

    /**
     * 用于这个泛型上中包含了内部类的情况，这里没有内部类，所以返回Null
     *
     * @return
     */
    @Override
    public Type getOwnerType() {
        return null;
    }
}
