package h2.fw.core.web.sql;

import com.fasterxml.jackson.databind.ObjectMapper;
import h2.fw.core.web.EvaluationManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SQLManager {
    private static final Logger LOGGER = LogManager.getLogger(SQLManager.class.getName());

    private static SQLManager instance;
    private Map<String, SQLConnection> connections;
    private Map<String, Integer> connectionCount;
    private ObjectMapper objectMapper;
    // Store last query results per database
    private ThreadLocal<Map<String, List<Map<String, Object>>>> lastQueryResults = ThreadLocal.withInitial(HashMap::new);

    private SQLManager() {
        connections = new ConcurrentHashMap<>();
        connectionCount = new ConcurrentHashMap<>();
        objectMapper = new ObjectMapper();
    }

    public static synchronized SQLManager getInstance() {
        if (instance == null) {
            instance = new SQLManager();
        }
        return instance;
    }

    public SQLConnectionParams loadConnections(String name) throws IOException {
        SQLConnectionParams[] params = objectMapper.readValue(new File("./SQLConnections.json"), SQLConnectionParams[].class);

        for (SQLConnectionParams param : params) {
            if (param.getName().equals(name)) {
                return param;
            }
        }
        throw new IllegalArgumentException("No connection parameters found for name: " + name);
    }

    public SQLConnection connectDB(String name) throws IOException {
        connections.computeIfAbsent(name, k -> {
            try {
                SQLConnectionParams connectionParams = loadConnections(name);
                SQLConnection connection = SQLConnectionFactory.createConnection(connectionParams);
                connectionCount.put(name, 1);
                LOGGER.info("Connected to database: " + name);
                return connection;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        connectionCount.compute(name, (k, v) -> v == null ? 1 : v + 1);
//        System.out.println("Additional connection to DB: " + name);
        return connections.get(name);
    }

    public List<Map<String, Object>> executeQuery(String name, String query) throws IOException, SQLException {
        SQLConnection connection = connectDB(name);
        return connection.executeQuery(query);
    }
    public void executeQueryWithoutResult(String name, String query) throws IOException, SQLException {
        SQLConnection connection = connectDB(name);
        connection.executeUpdate(query);
    }

    public List<Map<String, Object>> executeQueryWithResult(String name, String query) throws IOException, SQLException {
        SQLConnection connection = connectDB(name);
        List<Map<String, Object>> results = connection.executeQuery(query);
        setLastQueryResults(name, results);
        return results;
    }



    private void setLastQueryResults(String database, List<Map<String, Object>> results) {
        // Store the raw results (without conversion) in the ThreadLocal variable
        lastQueryResults.get().put(database, results);
    }



    public List<Map<String, String>> getLastQueryResults(String database) {
        // Get the raw results for the specific database
        List<Map<String, Object>> rawResults = lastQueryResults.get().get(database);

        // Convert the raw results to a List<Map<String, String>>
        return convertResultsToStringMap(rawResults);
    }


    private List<Map<String, String>> convertResultsToStringMap(List<Map<String, Object>> results) {
        // Conversion logic to change Map<String, Object> to Map<String, String>
        List<Map<String, String>> stringResults = new ArrayList<>();
        for (Map<String, Object> result : results) {
            Map<String, String> stringResult = new HashMap<>();
            for (Map.Entry<String, Object> entry : result.entrySet()) {
                stringResult.put(entry.getKey(), entry.getValue() != null ? entry.getValue().toString() : null);
            }
            stringResults.add(stringResult);
        }
        return stringResults;
    }



    public void closeAllConnections() {
        for (SQLConnection connection : connections.values()) {
            connection.close();
        }
        connections.clear();

        // Clear all last query results
        clearLastQueryResults();

        LOGGER.info("All connections closed and last query results cleared.");
    }

    public void clearLastQueryResults() {
        lastQueryResults.get().clear();
    }
}
