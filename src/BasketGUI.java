import models.Address;
import models.Customer;
import utils.Connection;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 * William Trent Holliday
 * 1/30/15
 */
public class BasketGUI {

    public BasketGUI() {
        // Make items in item table not draggable
        itemTable.getTableHeader().setReorderingAllowed(false);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                login(usernameField.getText(), loginPasswordField.getPassword());
            }
        });
    }

    private void login(String text, char[] password) {
        String userQuery = "SELECT * FROM CUSTOMERS WHERE EMAIL = ? AND PASSWORD = ?";
        ResultSet user = Connection.getResultsFromQuery(userQuery, text, String.valueOf(password));
        Customer loggedInCustomer = Customer.createCustomerFromQuery(user);
        Connection.setLoggedInCustomer(loggedInCustomer);

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

            OrderTable orderModel = new OrderTable(Connection.getLoggedInCustomer());
            orderTable.setModel(orderModel);

            orderTable.setIntercellSpacing(new Dimension(5, 5));
            orderTable.setRowHeight(35);

            JTableButtonRenderer buttonRenderer = new JTableButtonRenderer();
            orderTable.getColumn("View Details").setCellRenderer(buttonRenderer);
            orderTable.getColumn("Make Payment").setCellRenderer(buttonRenderer);
            orderTable.addMouseListener(new JTableButtonMouseListener(orderTable));
        }
    }

    private void logout() {
        tabPane.setComponentAt(0, homePanel);
        tabPane.repaint();
    }

    public static void main(String[] args) {
        try
        {
            UIManager.setLookAndFeel("com.jtattoo.plaf.acryl.AcrylLookAndFeel");
        }
        catch (Exception e) {
            System.out.println("Failed to load look and feel. You do not have JTattoo installed. " +
                    "Using system them instead.");
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        BasketGUI gui = new BasketGUI();
        JFrame frame = new JFrame("BasketGUI");
        frame.setContentPane(gui.panel1);

        InventoryTable inventoryModel = new InventoryTable();
        gui.itemTable.setModel(inventoryModel);

        gui.itemTable.setIntercellSpacing(new Dimension(5, 5));
        gui.itemTable.setRowHeight(35);

        JTableButtonRenderer buttonRenderer = new JTableButtonRenderer();
        gui.itemTable.getColumn("Details").setCellRenderer(buttonRenderer);
        gui.itemTable.getColumn("Buy Now").setCellRenderer(buttonRenderer);
        gui.itemTable.addMouseListener(new JTableButtonMouseListener(gui.itemTable));




        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(625, 450));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);


    }


    private static class JTableButtonRenderer extends DefaultTableCellRenderer {
        @Override public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
                    row, column);
            JButton button = (JButton) value;
            return button;
        }
    }

    private static class JTableButtonMouseListener extends MouseAdapter {
        private final JTable table;

        public JTableButtonMouseListener(JTable table) {
            this.table = table;
        }

        public void mouseClicked(MouseEvent e) {
            int column = table.getColumnModel().getColumnIndexAtX(e.getX()); // get the column of the button
            int row = e.getY() / table.getRowHeight(); //get the row of the button

            //Checking the row or column is valid or not
            if (row < table.getRowCount() && row >= 0 && column < table.getColumnCount() && column >= 0) {
                Object value = table.getValueAt(row, column);
                if (value instanceof JButton) {
                    //perform a click event
                    ((JButton) value).doClick();
                }
            }
        }
    }

    // GUI variables
    private JTabbedPane tabPane;
    private JPanel panel1;
    private JPanel inventoryTab;
    private JPanel Orders;
    private JScrollPane ordersTable;
    private JTextField customerField;
    private JTextField cityField;
    private JTable itemTable;
    private JTextField usernameField;
    private JPasswordField loginPasswordField;
    private JButton loginButton;
    private JButton logoutButton;
    private JPanel homePanel;
    private JTable orderTable;
    private HomeScreen homeScreen;

    // Other variables
}
