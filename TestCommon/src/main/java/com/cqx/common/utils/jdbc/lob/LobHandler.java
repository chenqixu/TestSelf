package com.cqx.common.utils.jdbc.lob;

import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * LobHandler
 *
 * @author chenqixu
 */
public interface LobHandler {

    /**
     * Retrieve the given column as bytes from the given ResultSet.
     * Might simply invoke {@code ResultSet.getBytes} or work with
     * {@code ResultSet.getBlob}, depending on the database and driver.
     *
     * @param rs         the ResultSet to retrieve the content from
     * @param columnName the column name to use
     * @return the content as byte array, or {@code null} in case of SQL NULL
     * @throws SQLException if thrown by JDBC methods
     * @see java.sql.ResultSet#getBytes
     */
    byte[] getBlobAsBytes(ResultSet rs, String columnName) throws SQLException;

    /**
     * Retrieve the given column as bytes from the given ResultSet.
     * Might simply invoke {@code ResultSet.getBytes} or work with
     * {@code ResultSet.getBlob}, depending on the database and driver.
     *
     * @param rs          the ResultSet to retrieve the content from
     * @param columnIndex the column index to use
     * @return the content as byte array, or {@code null} in case of SQL NULL
     * @throws SQLException if thrown by JDBC methods
     * @see java.sql.ResultSet#getBytes
     */
    byte[] getBlobAsBytes(ResultSet rs, int columnIndex) throws SQLException;

    /**
     * Retrieve the given column as binary stream from the given ResultSet.
     * Might simply invoke {@code ResultSet.getBinaryStream} or work with
     * {@code ResultSet.getBlob}, depending on the database and driver.
     *
     * @param rs         the ResultSet to retrieve the content from
     * @param columnName the column name to use
     * @return the content as binary stream, or {@code null} in case of SQL NULL
     * @throws SQLException if thrown by JDBC methods
     * @see java.sql.ResultSet#getBinaryStream
     */
    InputStream getBlobAsBinaryStream(ResultSet rs, String columnName) throws SQLException;

    /**
     * Retrieve the given column as binary stream from the given ResultSet.
     * Might simply invoke {@code ResultSet.getBinaryStream} or work with
     * {@code ResultSet.getBlob}, depending on the database and driver.
     *
     * @param rs          the ResultSet to retrieve the content from
     * @param columnIndex the column index to use
     * @return the content as binary stream, or {@code null} in case of SQL NULL
     * @throws SQLException if thrown by JDBC methods
     * @see java.sql.ResultSet#getBinaryStream
     */
    InputStream getBlobAsBinaryStream(ResultSet rs, int columnIndex) throws SQLException;

    /**
     * Retrieve the given column as String from the given ResultSet.
     * Might simply invoke {@code ResultSet.getString} or work with
     * {@code ResultSet.getClob}, depending on the database and driver.
     *
     * @param rs         the ResultSet to retrieve the content from
     * @param columnName the column name to use
     * @return the content as String, or {@code null} in case of SQL NULL
     * @throws SQLException if thrown by JDBC methods
     * @see java.sql.ResultSet#getString
     */
    String getClobAsString(ResultSet rs, String columnName) throws SQLException;

    /**
     * Retrieve the given column as String from the given ResultSet.
     * Might simply invoke {@code ResultSet.getString} or work with
     * {@code ResultSet.getClob}, depending on the database and driver.
     *
     * @param rs          the ResultSet to retrieve the content from
     * @param columnIndex the column index to use
     * @return the content as String, or {@code null} in case of SQL NULL
     * @throws SQLException if thrown by JDBC methods
     * @see java.sql.ResultSet#getString
     */
    String getClobAsString(ResultSet rs, int columnIndex) throws SQLException;

    /**
     * Create a new {@link LobCreator} instance, i.e. a session for creating BLOBs
     * and CLOBs. Needs to be closed after the created LOBs are not needed anymore -
     * typically after statement execution or transaction completion.
     *
     * @return the new LobCreator instance
     * @see LobCreator#close()
     */
    LobCreator getLobCreator();
}
