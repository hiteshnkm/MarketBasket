import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * William Trent Holliday
 * 1/30/15
 */
public class BasketGUI {
    private JTabbedPane tabPane;
    private JPanel panel1;
    private JPanel inventoryTab;
    private JPanel orderTab;
    private JList inventoryList;
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

    public BasketGUI() {

        addCustomerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                addCustomer();
                reloadCustomerList();
            }
        });
    }

    private static ResultSet getResultsFromQuery(String sqlQuery){
        DefaultListModel listModel = new DefaultListModel();
        java.sql.Connection connection = Connection.getConnection();
        ResultSet results;
        try {
            Statement statement = connection.createStatement();
            results = statement.executeQuery(sqlQuery);
        }catch(SQLException ex){
            System.out.println(ex);
            results = null;
        }
        return results;
    }

    private void addCustomer(){
        String updateQuery = "insert into customers (customerid, firstname, lastname, password)" +
                                " values ("+
                                    "'" + customerField.getText() +
                                    "', '" + firstNameField.getText() +
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
        }catch(SQLException e){
            e.printStackTrace();
        }
        customerList.setModel(customerModel);
    }

    public static void main(String[] args) {
        BasketGUI gui = new BasketGUI();
        JFrame frame = new JFrame("BasketGUI");
        frame.setContentPane(gui.panel1);
        ResultSet inventoryResults = getResultsFromQuery("select * from item");

        DefaultListModel inventoryModel = new DefaultListModel();

        try {
            while (inventoryResults.next())
                inventoryModel.addElement(inventoryResults.getString("itemname") + " - $" + inventoryResults.getDouble("price"));

        }catch(SQLException e){
            e.printStackTrace();
        }
        gui.inventoryList.setModel(inventoryModel);

        ResultSet customerResults = getResultsFromQuery("select * from customers");
        DefaultListModel customerModel = new DefaultListModel();
        try {
            while (customerResults.next())
                customerModel.addElement(customerResults.getString("firstname") + " " + customerResults.getString("lastname"));
        }catch(SQLException e){
            e.printStackTrace();
        }
        gui.customerList.setModel(customerModel);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
