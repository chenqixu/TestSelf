package com.cqx.write;

/**
 * IRebuilder
 *
 * @author chenqixu
 */
public abstract class IRebuilder<K, V> implements Cloneable {
    private K k;
    private V v;

    public K getK() {
        return k;
    }

    public void setK(K k) {
        this.k = k;
    }

    public V getV() {
        return v;
    }

    public void setV(V v) {
        this.v = v;
    }

    abstract K preper() throws Exception;

    abstract void close();

    abstract void commit();

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
