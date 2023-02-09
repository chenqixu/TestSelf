package com.cqx.common.utils.jdbc.lob;

import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * AbstractLobHandler
 *
 * @author chenqixu
 */
public abstract class AbstractLobHandler implements LobHandler {

    @Override
    public byte[] getBlobAsBytes(ResultSet rs, String columnName) throws SQLException {
        return getBlobAsBytes(rs, rs.findColumn(columnName));
    }

    @Override
    public InputStream getBlobAsBinaryStream(ResultSet rs, String columnName) throws SQLException {
        return getBlobAsBinaryStream(rs, rs.findColumn(columnName));
    }

    @Override
    public String getClobAsString(ResultSet rs, String columnName) throws SQLException {
        return getClobAsString(rs, rs.findColumn(columnName));
    }
}
