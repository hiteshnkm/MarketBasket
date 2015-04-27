package utils;

import models.db.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
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
    private static Customer loggedInCustomer;

    static class ConnectionHolder{

        private static java.sql.Connection CONN = createConnection();
    }

    /**
     * Create the initial connection to the database. Will only make the connection once. Uses a singleton to store
     * an instance of the connection.
     *
     * @return Connection object.
     */
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

    /**
     * A helper function that is used to create headless loading messages.
     *
     * @param message the text to display in the frame
     * @return the JFrame instance of the message box.
     */
    public static JFrame createLoadingFrame(String message) {
        final JFrame messageFrame = new JFrame();
        final JPanel panel = (JPanel) messageFrame.getContentPane();
        panel.setBorder(new EmptyBorder(10,10,10,10));
        JLabel messageLabel = new JLabel(message);
        panel.add(messageLabel);

        messageFrame.setUndecorated(true);
        messageFrame.getRootPane().setWindowDecorationStyle(JRootPane.NONE);

        messageFrame.setSize(250, 100);
        messageFrame.setLocationRelativeTo(null);
        messageFrame.setVisible(true);
        return messageFrame;
    }

    /**
     * Helper function for running a sql query on the database.
     *
     * Use this helper method to make calls to the database. The SQL query string must have a '?' where
     * you want to insert a variable that was passed through in args. SQL query must also not end with a ';'.
     *
     * The below example is how you would use the function to execute a call to get information for the item
     * with an itemid of 1.
     *
     * Ex.
     *  ResultSet queryResults = Connection.getResultsFromQuery("select * from items where itemid = ?", "1");
     *
     * @param sqlQuery the SQL query string to execute
     * @param args pass any number of string arguments that need to be inserted into the sql query
     * @return ResultSet object which can be used to pull the results of the query.
     */
    public static ResultSet getResultsFromQuery(String sqlQuery, String... args){
        java.sql.Connection connection = getConnection();
        ResultSet results;
        try {
            PreparedStatement statement = connection.prepareStatement(sqlQuery, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            // Add all of the arguments to the sql query
            for (int i = 0; i < args.length; i++) {
                int index = i + 1;
                statement.setString(index, args[i]);
            }
            results = statement.executeQuery();
        }catch(SQLException ex){
            JOptionPane.showMessageDialog(null, ex.getMessage());
            results = null;
        }
        return results;
    }

    public OrderLineItem getOrderLineFromQuery(ResultSet orderResult) {
        try {
            long lineID = orderResult.getInt("LINEID");
            long itemID = orderResult.getInt("ITEMID");
            long orderID = orderResult.getInt("ORDERID");
            double amtMoney = orderResult.getDouble("AMTMONEY");
            int itemQuantity = orderResult.getInt("ITEMQUANTITY");

            return new OrderLineItem(lineID, itemID, orderID, amtMoney, itemQuantity);
        } catch(SQLException ex){
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Gets the current logged in customer.
     * @return a Customer object or null if not logged in.
     */
    public static Customer getLoggedInCustomer() {
        return loggedInCustomer;
    }

    /**
     * Sets the logged in customer
     * @param loggedInCustomer the customer that just logged in.
     */
    public static void setLoggedInCustomer(Customer loggedInCustomer) {
        Connection.loggedInCustomer = loggedInCustomer;
    }

}
