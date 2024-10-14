package h2.fw.core.web.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import h2.fw.core.web.api.RestfulComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SQLConnection {
    private static final Logger LOGGER = LogManager.getLogger(SQLConnection.class.getName());

    private SQLConnectionParams connectionParams;
    private HikariDataSource dataSource;

    public SQLConnection(SQLConnectionParams connectionParams) {
        this.connectionParams = connectionParams;
        this.connect();
    }

    private void connect() {
        if (dataSource == null) {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(getConnectionString());
            config.setUsername(connectionParams.getLogin());
            config.setPassword(connectionParams.getPassword());
            config.setMaximumPoolSize(10); // Adjust pool size as needed
            config.setMinimumIdle(2);
            config.setConnectionTimeout(30000);
            config.setIdleTimeout(600000);
            config.setMaxLifetime(1800000);
            dataSource = new HikariDataSource(config);
        }
    }

    public List<Map<String, Object>> executeQuery(String query) throws SQLException {
        long startTime = System.currentTimeMillis();
        List<Map<String, Object>> resultList = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(metaData.getColumnName(i), rs.getObject(i));
                }
                resultList.add(row);
            }
        } finally {
            long endTime = System.currentTimeMillis();
            LOGGER.info("Query took {} ms", endTime - startTime);
            LOGGER.info("resultList List: {}", resultList);
        }
        return resultList;
    }

    public void executeUpdate(String query) throws SQLException {
        long startTime = System.currentTimeMillis();
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(query);
        } finally {
            long endTime = System.currentTimeMillis();
            System.out.println("Update executed in " + (endTime - startTime) + " ms");
        }
    }

    private String getConnectionString() {
        switch (connectionParams.getType().toUpperCase()) {
            case "MYSQL":
                return String.format("jdbc:mysql://%s:%d/%s",
                        connectionParams.getHost(), connectionParams.getPort(), connectionParams.getSchema());
            case "ORACLE":
                return String.format("jdbc:oracle:thin:@%s:%d/%s",
                        connectionParams.getHost(), connectionParams.getPort(), connectionParams.getServiceName());
            case "POSTGRES":
                return String.format("jdbc:postgresql://%s:%d/%s",
                        connectionParams.getHost(), connectionParams.getPort(), connectionParams.getSchema());
            default:
                throw new IllegalArgumentException("Unsupported database type: " + connectionParams.getType());
        }
    }

    public void close() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}
