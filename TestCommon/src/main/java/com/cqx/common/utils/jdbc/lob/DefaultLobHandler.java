package com.cqx.common.utils.jdbc.lob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DefaultLobHandler
 *
 * @author chenqixu
 */
public class DefaultLobHandler extends AbstractLobHandler {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private boolean wrapAsLob = false;

    private boolean streamAsLob = false;

    private boolean createTemporaryLob = false;

    /**
     * Specify whether to submit a byte array / String to the JDBC driver
     * wrapped in a JDBC Blob / Clob object, using the JDBC {@code setBlob} /
     * {@code setClob} method with a Blob / Clob argument.
     * <p>Default is "false", using the common JDBC 2.0 {@code setBinaryStream}
     * / {@code setCharacterStream} method for setting the content. Switch this
     * to "true" for explicit Blob / Clob wrapping against JDBC drivers that
     * are known to require such wrapping (e.g. PostgreSQL's for access to OID
     * columns, whereas BYTEA columns need to be accessed the standard way).
     * <p>This setting affects byte array / String arguments as well as stream
     * arguments, unless {@link #setStreamAsLob "streamAsLob"} overrides this
     * handling to use JDBC 4.0's new explicit streaming support (if available).
     *
     * @see java.sql.PreparedStatement#setBlob(int, java.sql.Blob)
     * @see java.sql.PreparedStatement#setClob(int, java.sql.Clob)
     */
    public void setWrapAsLob(boolean wrapAsLob) {
        this.wrapAsLob = wrapAsLob;
    }

    /**
     * Specify whether to submit a binary stream / character stream to the JDBC
     * driver as explicit LOB content, using the JDBC 4.0 {@code setBlob} /
     * {@code setClob} method with a stream argument.
     * <p>Default is "false", using the common JDBC 2.0 {@code setBinaryStream}
     * / {@code setCharacterStream} method for setting the content.
     * Switch this to "true" for explicit JDBC 4.0 streaming, provided that your
     * JDBC driver actually supports those JDBC 4.0 operations (e.g. Derby's).
     * <p>This setting affects stream arguments as well as byte array / String
     * arguments, requiring JDBC 4.0 support. For supporting LOB content against
     * JDBC 3.0, check out the {@link #setWrapAsLob "wrapAsLob"} setting.
     *
     * @see java.sql.PreparedStatement#setBlob(int, java.io.InputStream, long)
     * @see java.sql.PreparedStatement#setClob(int, java.io.Reader, long)
     */
    public void setStreamAsLob(boolean streamAsLob) {
        this.streamAsLob = streamAsLob;
    }

    /**
     * Specify whether to copy a byte array / String into a temporary JDBC
     * Blob / Clob object created through the JDBC 4.0 {@code createBlob} /
     * {@code createClob} methods.
     * <p>Default is "false", using the common JDBC 2.0 {@code setBinaryStream}
     * / {@code setCharacterStream} method for setting the content. Switch this
     * to "true" for explicit Blob / Clob creation using JDBC 4.0.
     * <p>This setting affects stream arguments as well as byte array / String
     * arguments, requiring JDBC 4.0 support. For supporting LOB content against
     * JDBC 3.0, check out the {@link #setWrapAsLob "wrapAsLob"} setting.
     *
     * @see java.sql.Connection#createBlob()
     * @see java.sql.Connection#createClob()
     */
    public void setCreateTemporaryLob(boolean createTemporaryLob) {
        this.createTemporaryLob = createTemporaryLob;
    }

    @Override
    public String getClobAsString(ResultSet rs, int columnIndex) throws SQLException {
        logger.debug("Returning CLOB as string");
        if (this.wrapAsLob) {
            Clob clob = rs.getClob(columnIndex);
            return clob.getSubString(1, (int) clob.length());
        } else {
            return rs.getString(columnIndex);
        }
    }

    @Override
    public LobCreator getLobCreator() {
        return null;
    }
}
