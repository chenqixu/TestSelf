package com.cqx.write;

/**
 * RebuilderCallable
 *
 * @author chenqixu
 */
public class RebuilderCallable<K, V> implements IWatcher {

    private IRebuilder<K, V> iRebuilder;
    private RebuilderStatus status = RebuilderStatus.PREPER;

    public RebuilderCallable(IRebuilder<K, V> iRebuilder, V v) {
        this.iRebuilder = iRebuilder;
        this.iRebuilder.setV(v);
    }

    @Override
    public K call() throws Exception {
        K tmp = null;
        switch (status) {
            case PREPER:
                tmp = iRebuilder.preper();
                if (tmp != null) {
                    iRebuilder.close();
                }
                break;
            case REBUILDER:
                tmp = iRebuilder.preper();
                if (tmp != null) {
                    iRebuilder.commit();
                }
                break;
            default:
                break;
        }
        return tmp;
    }

    @Override
    public void changeRebuilder() {
        status = RebuilderStatus.REBUILDER;
    }

    public enum RebuilderStatus {
        PREPER, REBUILDER;
    }
}
