import javax.swing.*;
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
        // Display a splash screen, while we connect to the database
        JFrame messageFrame = createLoadingFrame("Connecting to database...");

        try {
            DriverManager.setLoginTimeout(300);
            conn = DriverManager.getConnection(
                    "jdbc:oracle:thin:@" + hostName + ":" + dbName,
                    connectionProps);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error connecting to database", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        messageFrame.dispose();
        System.out.println("Connected to database");
        return conn;
    }

    public static java.sql.Connection getConnection(){
        return ConnectionHolder.CONN;
    }

    private static JFrame createLoadingFrame(String message) {
        JFrame messageFrame = new JFrame();
        messageFrame.getContentPane().add(new JLabel(message));

        messageFrame.setUndecorated(true);
        messageFrame.getRootPane().setWindowDecorationStyle(JRootPane.NONE);

        messageFrame.setSize(250, 100);
        messageFrame.setLocationRelativeTo(null);
        messageFrame.setVisible(true);
        return messageFrame;
    }

}
