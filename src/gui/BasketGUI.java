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
import javax.swing.table.*;

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

        DefaultComboBoxModel itemReportModel = new DefaultComboBoxModel<String>(itemReports);
        itemReportOptions.setModel(itemReportModel);

        itemReportBtn.addActionListener(new ReportButtonEvent(itemReportOptions, itemReports, itemQueries));

        DefaultComboBoxModel<String> orderReportModel = new DefaultComboBoxModel<String>(orderReports);
        orderReportOptions.setModel(orderReportModel);

        orderReportBtn.addActionListener(new ReportButtonEvent(orderReportOptions, orderReports, orderQueries));

        // Disable the cart and orders tab initially, since a user will not be logged in
        tabPane.setEnabledAt(2, false);
        tabPane.setEnabledAt(3, false);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                final JFrame loginMessage = Connection.createLoadingFrame("Authenticating user...");
                // Run the login process in the background and display a notification when the process finishes
                SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                    @Override
                    public Void doInBackground() {
                        login(usernameField.getText(), loginPasswordField.getPassword());
                        return null;
                    }

                    @Override
                    public void done() {
                        loginMessage.dispose();
                    }
                };
                worker.execute();
            }
        });


        tabPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent changeEvent) {
                // Refresh our cart when that tab is selected
                if (tabPane.getSelectedIndex() == 2) {
                    CartTable cartTable = new CartTable(placeOrder);
                    cartItemTable.setModel(cartTable);
                    cartItemTable.getColumn("Remove").setCellRenderer(new JTableButtonRenderer());

                    // If there are no items in the cart then we want to disable the place order button
                    if (cartTable.getCartItems().size() < 1) {
                        placeOrder.setEnabled(false);
                    }
                    // Should be enabled for items in the cart
                    else {
                        placeOrder.setEnabled(true);
                    }
                } else if (tabPane.getSelectedIndex() == 3) {
                    TableModel tableModel = orderTable.getModel();

                    // If the table model is an not an instance of OrderTable or the customer that loaded the order table
                    // is not the current logged in user then we only need to reload the date.
                    if (!(tableModel instanceof OrderTable) || Connection.getLoggedInCustomer().getCustomerID() != ((OrderTable) tableModel).getCustomerID()) {
                        updateOrderTable();
                    }
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
                ((CartTable) cartItemTable.getModel()).clearItems();
                cartItemTable.repaint();
            }
        });

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                SwingWorker<Void, Void> refreshWorker = new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        Connection.refreshConnection();
                        return null;
                    }

                    @Override
                    public void done(){
                        updateOrderTable();
                    }
                };
                refreshWorker.execute();
            }
        });
    }

    private void updateOrderTable(){
        TableModel tableModel = orderTable.getModel();

        final JFrame orderLoading = Connection.createLoadingFrame("Loading order history");
        if (tableModel instanceof OrderTable) {
            ((OrderTable) tableModel).clearRows();
            tabPane.repaint();
        }

        // Swing worker that will pull the order information for the logged in customer
        // will display a loading message while it runs in the background
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            public Void doInBackground() {
                OrderTable orderModel = new OrderTable(Connection.getLoggedInCustomer());
                orderTable.setModel(orderModel);
                orderTable.setIntercellSpacing(new Dimension(5, 5));
                orderTable.setRowHeight(35);

                TableColumnModel orderColumnModel = orderTable.getColumnModel();

                TableColumn firstColumn = orderColumnModel.getColumn(0);
                firstColumn.setMinWidth(20);

                TableColumn secondColumn = orderColumnModel.getColumn(1);
                secondColumn.setMinWidth(80);

                TableColumn thirdColumn = orderColumnModel.getColumn(2);
                thirdColumn.setMinWidth(55);

                TableColumn fourthColumn = orderColumnModel.getColumn(3);
                DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
                rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
                fourthColumn.setCellRenderer(rightRenderer);
                fourthColumn.setMinWidth(55);
                fourthColumn.setPreferredWidth(80);

                TableColumn fifthColumn = orderColumnModel.getColumn(4);
                JTableButtonRenderer buttonRenderer = new JTableButtonRenderer();
                fifthColumn.setCellRenderer(buttonRenderer);
                fifthColumn.setMinWidth(100);
//                fifthColumn.setMaxWidth(100);

                TableColumn sixthColumn = orderColumnModel.getColumn(5);
                sixthColumn.setCellRenderer(buttonRenderer);
                sixthColumn.setMinWidth(150);

                orderTable.removeMouseListener(orderTableMouseListener);
                orderTable.addMouseListener(orderTableMouseListener);

                return null;
            }

            @Override
            public void done() {
                orderLoading.dispose();
            }
        };
        worker.execute();
    }

    private class ReportButtonEvent implements ActionListener{

        private JComboBox dropDownBox;
        private String[] reports;
        private String[] queries;

        public ReportButtonEvent(JComboBox dropDownBox, String[] reports, String[] queries){
            this.dropDownBox = dropDownBox;
            this.reports = reports;
            this.queries = queries;
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            final JFrame generatingReportMessage = Connection.createLoadingFrame("Generating report...");
            generatingReportMessage.setVisible(true);

            SwingWorker<Void, Void> reportWorker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    // Get the selected item report
                    int selectedReportIndex = dropDownBox.getSelectedIndex();
                    try{
                        // Create the report
                        generateReport(generatingReportMessage, reports, queries, selectedReportIndex);
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, ex.getMessage(), "Error generating report", JOptionPane.ERROR_MESSAGE);
                    }
                    return null;
                }
            };
            reportWorker.execute();
        }
    }

    /**
     * Method to show the specified report. This run the specified query from the given list of queries, and will
     * create and show a report dialog containing the results of the query.
     *
     * The report list and the query list indices must align. Meaning that the sql query for the report at
     * reportList[0] must exist at queryList[0].
     *
     * @param generatingReportMessage the loading notification window to close on completion.
     * @param reportList the list of all the reports
     * @param queryList the list of all of the queries
     * @param selectedReportIndex the index of the report that was selected
     * @throws SQLException
     */
    private static void generateReport(JFrame generatingReportMessage, String[] reportList, String[] queryList, int selectedReportIndex) throws SQLException{
        String selectedReport = reportList[selectedReportIndex];
        String selectedReportQuery = queryList[selectedReportIndex];

        ResultSet queryResult = Connection.getResultsFromQuery(selectedReportQuery);

        ArrayList<String[]> result = new ArrayList<String[]>();
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
    private JButton refreshButton;
    private HomeScreen homeScreen;

    // Other variables
    private final JTableButtonMouseListener orderTableMouseListener =  new JTableButtonMouseListener(orderTable);

    private static final String[] itemReports = {"Average item price.", "Get most expensive and least expensive item."};
    private static final String[] itemQueries = {"Select avg(Price) as AveragePrices from Item", "SELECT * FROM ITEM ORDER BY PRICE DESC"};

    private static final String[] orderReports = {
            "Number of orders per customer", "Number of orders by state",
            "Average order price", "Total money spent by customer",
            "Average time to ship order", "Number of orders by month",
            "Number of orders by day of week", "Number of orders by zip",
            "Orders not yet shipped", "Top 5 most expensive orders"
    };
    private static final String[] orderQueries = {
            "select customers.firstname, customers.lastname, count(orders.customerid) as \"Number Of Orders\" from customers\n" +
                    "join orders on orders.customerid = customers.customerid\n" +
                    "group by customers.firstname, customers.lastname\n" +
                    "order by \"Number Of Orders\" desc\n",
            "select address.state, count(orderid) as \"Number of Orders\" from orders\n" +
                    "join address on address.addressid = orders.shipaddress\n" +
                    "group by address.state",
            "select to_char(avg(totalprice), 'fm9999999.90') as \"Average Price\" from orders",
            "select firstname, lastname, sum(orders.totalprice) as \"Total Money Spent\" from customers\n" +
                    "join orders on orders.customerid = customers.customerid\n" +
                    "group by firstname, lastname",
            "select avg(shippeddate - orderdate) || ' days' as \"Avg Time to Ship\" from orders",
            "select to_char(orderdate, 'Month') as \"Month\", count(orderdate) as \"Number of orders\" from orders\n" +
                    "group by to_char(orderdate, 'Month') order by \"Number of orders\" desc",
            "select to_char(orderdate, 'Day') as \"Day\", count(orderdate) as \"Number of orders\" from orders\n" +
                    "group by to_char(orderdate, 'Day') order by \"Number of orders\" desc",
            "select address.zip, count(billingaddress) as \"Number of orders\" from orders\n" +
                    "join address on addressid = billingaddress\n" +
                    "group by address.zip order by \"Number of orders\" desc",
            "select orderid, orderdate, 'Not Shipped' as \"Ship Status\" from orders\n" +
                    "where SHIPPEDDATE is null",
            "select * from\n" +
                    "(select orderid, totalprice from orders order by totalprice desc)\n" +
                    "where rownum <= 5"
    };
}
