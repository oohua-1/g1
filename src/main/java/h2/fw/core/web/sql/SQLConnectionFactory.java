package h2.fw.core.web.sql;

public class SQLConnectionFactory {
    public static SQLConnection createConnection(SQLConnectionParams params) {
        return new SQLConnection(params);
    }
}
