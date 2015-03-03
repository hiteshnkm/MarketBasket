import java.sql.*;
import java.util.Properties;

/**
 * William Trent Holliday
 * 3/2/15
 */
public class Connection {

    private static String userName = "root";
    private static String password = "marketbasket";
    private static String dbName = "MRKTBSKT";
    private static String hostName = "oracle-sql-server.c5axgu5gzr3g.us-west-2.rds.amazonaws.com:1521";

    static class ConnectionHolder{
        private static java.sql.Connection CONN = createConnection();
    }

    private static java.sql.Connection createConnection() {

        java.sql.Connection conn = null;
        Properties connectionProps = new Properties();
        connectionProps.put("user", userName);
        connectionProps.put("password", password);

        try {
            conn = DriverManager.getConnection(
                    "jdbc:oracle:thin:@" + hostName + ":" + dbName,
                    connectionProps);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("Connected to database");
        return conn;
    }

    public static java.sql.Connection getConnection(){
        return ConnectionHolder.CONN;
    }

}
