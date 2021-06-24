package com.cqx.common.utils.jdbc.lob;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * LobHandler
 *
 * @author chenqixu
 */
public interface LobHandler {

    String getClobAsString(ResultSet rs, String columnName) throws SQLException;

    String getClobAsString(ResultSet rs, int columnIndex) throws SQLException;

    LobCreator getLobCreator();
}
