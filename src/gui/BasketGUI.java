package gui;

import gui.dialogs.CreateOrder;
import gui.dialogs.ReportDialog;
import models.db.Customer;
import models.tables.CartTable;
import models.tables.InventoryTable;
import models.tables.OrderTable;
import models.tables.ReportTable;
import utils.Connection;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;

import java.awt.*;
import java.awt.event.*;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

/**
 * William Trent Holliday
 * 1/30/15
 */
public class BasketGUI {

    public BasketGUI() {
        // Make items in item table not draggable
        itemTable.getTableHeader().setReorderingAllowed(false);

        final String[] itemReports = {"Average item price.", "Get most expensive and least expensive item."};
        final String[] itemQueries = {"Select avg(Price) as AveragePrices from Item", "SELECT * FROM ITEM ORDER BY PRICE DESC"};
        DefaultComboBoxModel itemReportModel = new DefaultComboBoxModel<String>(itemReports);
        itemReportOptions.setModel(itemReportModel);
        itemReportBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                final JFrame generatingReportMessage = Connection.createLoadingFrame("Generating report...");
                generatingReportMessage.setVisible(true);

                SwingWorker<Void, Void> reportWorker = new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        int selectedReportIndex = itemReportOptions.getSelectedIndex();

                        String selectedReport = itemReports[selectedReportIndex];
                        String selectedReportQuery = itemQueries[selectedReportIndex];

                        ArrayList<String[]> result = new ArrayList<String[]>();
                        ResultSet queryResult = Connection.getResultsFromQuery(selectedReportQuery);
                        try {
                            ResultSetMetaData resultData = queryResult.getMetaData();
                            int columnCount = resultData.getColumnCount();

                            String[] columnNames = new String[columnCount];
                            while (queryResult.next()) {
                                String[] row = new String[columnCount];
                                for (int i = 0; i < columnCount; i++) {
                                    if (queryResult.isFirst())
                                        columnNames[i] = resultData.getColumnLabel(i + 1);
                                    row[i] = queryResult.getString(i + 1);
                                }
                                result.add(row);
                            }
                            generatingReportMessage.dispose();
                            ReportTable reportTable = new ReportTable(columnNames, result);
                            ReportDialog reportDialog = new ReportDialog(selectedReport, reportTable);
                            reportDialog.pack();
                            reportDialog.setVisible(true);
                        } catch (SQLException ex) {
                            JOptionPane.showMessageDialog(null, ex.getMessage(), "Error generating report", JOptionPane.ERROR_MESSAGE);
                        }
                        return null;
                    }
                };
                reportWorker.execute();
            }
        });

        // Disable the cart and orders tab initially, since a user will not be logged in
        tabPane.setEnabledAt(2, false);
        tabPane.setEnabledAt(3, false);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                final JFrame loginMessage = Connection.createLoadingFrame("Authenticating user...");
                // Run the login process in the background and display a notification when the process finishes
                SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>(){
                    @Override
                    public Void doInBackground(){
                        login(usernameField.getText(), loginPasswordField.getPassword());
                        return null;
                    }

                    @Override
                    public void done(){
                        loginMessage.dispose();
                    }
                };
                worker.execute();
            }
        });


        tabPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent changeEvent) {

                TableModel cartModel = cartItemTable.getModel();
                // Check to see if the cart table model has been initialized
                if (cartModel instanceof ReportTable) {
                    // If the table contains no items we disable the place order button
                    if (((CartTable) cartModel).getCartItems().size() < 1) {
                        placeOrder.setEnabled(false);
                    }
                    // We have items now so we re-enable the button
                    else {
                        placeOrder.setEnabled(true);
                    }
                }
                // The cart table model has not been set which means there are no items in the table
                // so the customer should not be able to place an ad.
                else{
                    placeOrder.setEnabled(false);
                }

                // Refresh our cart when that tab is selected
                if (tabPane.getSelectedIndex() == 2) {
                    CartTable reportTable = new CartTable(placeOrder);
                    cartItemTable.setModel(reportTable);
                    cartItemTable.getColumn("Remove").setCellRenderer(new JTableButtonRenderer());
                }
                else if (tabPane.getSelectedIndex() == 3) {

                    final JFrame orderLoading = Connection.createLoadingFrame("Loading order history");

                    // Swing worker that will pull the order information for the logged in customer
                    // will display a loading message while it runs in the background
                    SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>(){
                        @Override
                        public Void doInBackground(){
                            TableModel tableModel = orderTable.getModel();

                            // If the table model is an instance of OrderTable then
                            // we only need to get the latest orders and not all of them.
                            if (tableModel instanceof OrderTable) {
                                ((OrderTable) tableModel).updateOrders();
                                return null;
                            }

                            OrderTable orderModel = new OrderTable(Connection.getLoggedInCustomer());
                            orderTable.setModel(orderModel);
                            orderTable.setIntercellSpacing(new Dimension(5, 5));
                            orderTable.setRowHeight(35);

                            JTableButtonRenderer buttonRenderer = new JTableButtonRenderer();
                            orderTable.getColumn("View Details").setCellRenderer(buttonRenderer);
                            orderTable.getColumn("Make Payment").setCellRenderer(buttonRenderer);
                            return null;
                        }

                        @Override
                        public void done(){
                            orderLoading.dispose();
                        }
                    };
                    worker.execute();
                }
                tabPane.getRootPane().repaint();
            }
        });

        placeOrder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                List<Map> cartItems = ((CartTable) cartItemTable.getModel()).getCartItems();
                CreateOrder order = new CreateOrder(cartItems);
                order.pack();
                order.setLocationRelativeTo(JOptionPane.getFrameForComponent(placeOrder));
                order.setVisible(true);
                ((ReportTable) cartItemTable.getModel()).clearItems();
                cartItemTable.repaint();
            }
        });
    }

    private void login(String text, char[] password) {
        String userQuery = "SELECT * FROM CUSTOMERS WHERE EMAIL = ?";
        ResultSet user = Connection.getResultsFromQuery(userQuery, text);
        Customer loggedInCustomer = Customer.createCustomerFromQuery(user, String.valueOf(password));
        Connection.setLoggedInCustomer(loggedInCustomer);

        if (loggedInCustomer == null) {
            JOptionPane.showMessageDialog(null, "Invalid login information.", "Could not login.", JOptionPane.ERROR_MESSAGE);
        } else {
            homeScreen = new HomeScreen(loggedInCustomer);
            logoutButton = homeScreen.getLogoutButton();
            logoutButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    logout();
                }
            });
            tabPane.setComponentAt(0, homeScreen.getMainPanel());

            cartItemTable.addMouseListener(new JTableButtonMouseListener(cartItemTable));
            cartItemTable.setRowHeight(35);
            cartItemTable.setIntercellSpacing(new Dimension(5, 5));

            // When the customer is logged in we want to allow them to go to the cart and orders tab
            tabPane.setEnabledAt(2, true);
            tabPane.setEnabledAt(3, true);
            tabPane.repaint();
        }
    }

    private void logout() {
        // Disable the cart and orders tabs since the user logged out
        tabPane.setEnabledAt(2, false);
        tabPane.setEnabledAt(3, false);

        Connection.setLoggedInCustomer(null);
        tabPane.setComponentAt(0, homePanel);
        tabPane.repaint();
    }

    public static void main(String[] args) {
        try
        {
            UIManager.setLookAndFeel("com.jtattoo.plaf.hifi.HiFiLookAndFeel");
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

        gui.itemTable.getColumnModel().getColumn(2).setCellRenderer(buttonRenderer);
        gui.itemTable.getColumnModel().getColumn(3).setCellRenderer(buttonRenderer);
        gui.itemTable.addMouseListener(new JTableButtonMouseListener(gui.itemTable));

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(625, 540));
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
            table.getRootPane().repaint();
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
    private JTable cartItemTable;
    private JScrollPane cartTab;
    private JButton placeOrder;
    private JComboBox itemReportOptions;
    private JButton itemReportBtn;
    private JComboBox orderReportOptions;
    private JComboBox customerReportOptions;
    private JComboBox paymentReportOptions;
    private JButton orderReportBtn;
    private JButton customerReportBtn;
    private JButton paymentReportBtn;
    private HomeScreen homeScreen;

    // Other variables
}
