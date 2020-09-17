package com.cqx.distributed.coordinatio;

/**
 * CoordinatioFactory
 *
 * @author chenqixu
 */
public class CoordinatioFactory {

    private static volatile CoordinatioInf coordinatio;

    public static CoordinatioInf getInstance() {
        if (coordinatio == null) {
            synchronized (LocalCoordinatioImpl.class) {
                if (coordinatio == null) {
                    coordinatio = new LocalCoordinatioImpl();
                }
            }
        }
        return coordinatio;
    }

}
