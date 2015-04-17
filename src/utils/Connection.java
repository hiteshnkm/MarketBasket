package utils;

import models.Customer;
import models.Item;

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
        JPanel panel = (JPanel) messageFrame.getContentPane();
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

    public static Item createItemFromQuery(ResultSet inventoryResults) {
        try {
            Long itemid = inventoryResults.getLong("itemid");
            String itemname = inventoryResults.getString("itemname");
            String description = inventoryResults.getString("description");
            String category = inventoryResults.getString("category_type");
            Double price = inventoryResults.getDouble("price");
            int quantity = inventoryResults.getInt("quantity");

            return new Item(itemid, itemname, description, category, price, quantity);
        } catch(SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error loading items from database.", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    public static Customer getLoggedInCustomer() {
        return loggedInCustomer;
    }

    public static void setLoggedInCustomer(Customer loggedInCustomer) {
        Connection.loggedInCustomer = loggedInCustomer;
    }

    public static void placeOrder(Item rowItem) {
        Customer currentCustomer = getLoggedInCustomer();
    }
}
