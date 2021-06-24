package com.cqx.common.utils.jdbc.lob;

import java.io.Closeable;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * LobCreator
 *
 * @author chenqixu
 */
public interface LobCreator extends Closeable {

    void setClobAsString(PreparedStatement ps, int paramIndex, String content)
            throws SQLException;

    @Override
    void close();
}
