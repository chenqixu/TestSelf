package com.cqx.common.utils.jdbc.declare;

import com.cqx.common.utils.jdbc.DBType;

/**
 * 写入合并工具类
 *
 * @author chenqixu
 */
public class DeclareHelper {

    public static AbstractDeclare builder(DBType dbType) {
        switch (dbType) {
            case POSTGRESQL:
                return new PGDeclare();
        }
        return null;
    }
}
