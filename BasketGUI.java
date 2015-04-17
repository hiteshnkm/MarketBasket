import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
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
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JPasswordField passwordField;
    private JButton addCustomerButton;
    private JList customerList;
    private JTextField customerField;
    private JTable itemTable;
    private JPanel Orders;
    private JTable table1;
    private JScrollPane ordersTable;

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
        }catch(SQLException e){
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        customerList.setModel(customerModel);
    }

    public static void main(String[] args) {
        BasketGUI gui = new BasketGUI();
        JFrame frame = new JFrame("BasketGUI");
        frame.setContentPane(gui.panel1);
        ResultSet inventoryResults = getResultsFromQuery("select * from item");

        DefaultTableModel inventoryModel = new DefaultTableModel();
        inventoryModel.addColumn("Item name");
        inventoryModel.addColumn("Price");

        try {
            while (inventoryResults.next()) {
                String[] rowData = {inventoryResults.getString("itemname"), "$" + inventoryResults.getDouble("price")};
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
        frame.setPreferredSize(new Dimension(800,600));
        frame.pack();
        frame.setVisible(true);
    }
}
