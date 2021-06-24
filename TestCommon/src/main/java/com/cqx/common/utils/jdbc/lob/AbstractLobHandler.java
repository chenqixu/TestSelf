package com.cqx.common.utils.jdbc.lob;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * AbstractLobHandler
 *
 * @author chenqixu
 */
public abstract class AbstractLobHandler implements LobHandler {

    @Override
    public String getClobAsString(ResultSet rs, String columnName) throws SQLException {
        return getClobAsString(rs, rs.findColumn(columnName));
    }
}
