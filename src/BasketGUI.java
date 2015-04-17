import models.Customer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;

/**
 * William Trent Holliday
 * 1/30/15
 */
public class BasketGUI {
    private JTabbedPane tabPane;
    private JPanel panel1;
    private JPanel inventoryTab;
    private JPanel orderTab;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField emailField;
    private JTextField addressField;
    private JTextField stateField;
    private JTextField countryField;
    private JPasswordField passwordField;
    private JButton addCustomerButton;
    private JList customerList;
    private JTextField customerField;
    private JTextField cityField;
    private JTable itemTable;
    private JTextField usernameField;
    private JPasswordField loginPasswordField;
    private JButton loginButton;
    private JButton logoutButton;
    private JPanel homePanel;
    private HomeScreen homeScreen;

    public BasketGUI() {
        // Make items in item table not draggable
        itemTable.getTableHeader().setReorderingAllowed(false);

        addCustomerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                addCustomer();
                reloadCustomerList();
            }
        });
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                login(usernameField.getText(), loginPasswordField.getPassword());
            }
        });
    }

    private void login(String text, char[] password) {
        String userQuery = "SELECT * FROM CUSTOMERS WHERE EMAIL = ? AND PASSWORD = ?";
        ResultSet user = getResultsFromQuery(userQuery, text, String.valueOf(password));
        Customer loggedInCustomer = Customer.createCustomerFromQuery(user);
        if (loggedInCustomer == null) {
            JOptionPane.showMessageDialog(null, "Invalid login information.", "Could not login.", JOptionPane.ERROR_MESSAGE);
        }else {
            homeScreen = new HomeScreen(loggedInCustomer);
            logoutButton = homeScreen.getLogoutButton();
            logoutButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    logout();
                }
            });
            tabPane.setComponentAt(0, homeScreen.getMainPanel());
        }
    }

    private void logout() {
        tabPane.setComponentAt(0, homePanel);
        tabPane.repaint();
    }

    private static ResultSet getResultsFromQuery(String sqlQuery, String... args){
        java.sql.Connection connection = Connection.getConnection();
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

    private void addCustomer(){
        String updateQuery = "insert into customers (firstname, lastname, password)" +
                                " values ('" + firstNameField.getText() +
                                    "', '" + lastNameField.getText() +
                                    "', '" + passwordField.getText() +
                                "')";
        getResultsFromQuery(updateQuery);
    }

    private void reloadCustomerList(){
        ResultSet customerResults = getResultsFromQuery("select * from customers");
        DefaultListModel customerModel = new DefaultListModel();
        try {
            while (customerResults.next())
                customerModel.addElement(customerResults.getString("firstname") + " " + customerResults.getString("lastname"));
        } catch(SQLException e){
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        customerList.setModel(customerModel);
    }

    public static void main(String[] args) {
        BasketGUI gui = new BasketGUI();
        JFrame frame = new JFrame("BasketGUI");
        frame.setContentPane(gui.panel1);
        ResultSet inventoryResults = getResultsFromQuery("select * from item");

        InventoryTable inventoryModel = new InventoryTable();
        inventoryModel.addColumn("Item name");
        inventoryModel.addColumn("Price");
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
        try {
            while (inventoryResults.next()) {
                String[] rowData = {inventoryResults.getString("itemname"), currencyFormat.format(inventoryResults.getDouble("price"))};
                inventoryModel.addRow(rowData);
            }
        }catch(SQLException e){
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        gui.itemTable.setModel(inventoryModel);

        ResultSet customerResults = getResultsFromQuery("select * from customers");
        DefaultListModel customerModel = new DefaultListModel();
        try {
            while (customerResults.next())
                customerModel.addElement(customerResults.getString("firstname") + " " + customerResults.getString("lastname"));
        }catch(SQLException e){
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        gui.customerList.setModel(customerModel);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(625,310));
        frame.pack();
        frame.setVisible(true);
    }
}
