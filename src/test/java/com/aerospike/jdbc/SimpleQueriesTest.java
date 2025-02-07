package com.aerospike.jdbc;

import com.aerospike.client.Value;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

import static com.aerospike.jdbc.util.TestUtil.closeQuietly;
import static org.testng.Assert.assertEquals;

public class SimpleQueriesTest extends JdbcBaseTest {

    @BeforeMethod
    public void setUp() throws SQLException {
        Value.UseBoolBin = false;
        Objects.requireNonNull(connection, "connection is null");
        Statement statement = null;
        int count;
        String query = String.format(
                "insert into %s (bin1, int1, str1, bool1) values (11100, 1, \"bar\", true)",
                tableName
        );
        try {
            statement = connection.createStatement();
            count = statement.executeUpdate(query);
        } finally {
            closeQuietly(statement);
        }
        assertEquals(1, count);
    }

    @AfterMethod
    public void tearDown() throws SQLException {
        Objects.requireNonNull(connection, "connection is null");
        Statement statement = null;
        ResultSet resultSet = null;
        String query = String.format("delete from %s", tableName);
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            resultSet.next();
        } finally {
            closeQuietly(statement);
            closeQuietly(resultSet);
        }
    }

    @Test
    public void testSelectQuery() throws SQLException {
        Statement statement = null;
        ResultSet resultSet = null;
        String query = String.format("select * from %s limit 10", tableName);
        int total = 0;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                assertEquals(11100, resultSet.getInt("bin1"));
                assertEquals(1, resultSet.getInt("int1"));
                assertEquals("bar", resultSet.getString("str1"));
                assertEquals(1, resultSet.getInt("bool1"));

                total++;
            }
            assertEquals(1, total);
        } finally {
            closeQuietly(statement);
            closeQuietly(resultSet);
        }
    }

    @Test
    public void testInsertQuery() throws SQLException {
        Statement statement = null;
        int count;
        String query = String.format("insert into %s (bin1, int1) values (11101, 3)", tableName);
        try {
            statement = connection.createStatement();
            count = statement.executeUpdate(query);
        } finally {
            closeQuietly(statement);
        }
        assertEquals(1, count);
    }

    @Test
    public void testUpdateQuery() throws SQLException {
        Statement statement = null;
        int count;
        String query = String.format("update %s set int1=100 where bin1>10000", tableName);
        try {
            statement = connection.createStatement();
            count = statement.executeUpdate(query);
        } finally {
            closeQuietly(statement);
        }
        assertEquals(1, count);

        query = String.format("update %s set int1=100 where bin1>20000", tableName);
        try {
            statement = connection.createStatement();
            count = statement.executeUpdate(query);
        } finally {
            closeQuietly(statement);
        }
        assertEquals(0, count);
    }

    @Test
    public void testSelectCountQuery() throws SQLException {
        Statement statement = null;
        ResultSet resultSet = null;
        String query = String.format("select count(*) from %s", tableName);
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            resultSet.next();
            assertEquals(1, resultSet.getObject(1));
        } finally {
            closeQuietly(statement);
            closeQuietly(resultSet);
        }
    }
}
