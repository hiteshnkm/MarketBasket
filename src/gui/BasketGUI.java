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

        itemReportBtn.addActionListener(new ReportButtonEvent(itemReportOptions, itemReports, itemQueries, null));

        DefaultComboBoxModel<String> orderReportModel = new DefaultComboBoxModel<String>(orderReports);
        orderReportOptions.setModel(orderReportModel);

        orderReportBtn.addActionListener(new ReportButtonEvent(orderReportOptions, orderReports, orderQueries, null));

        DefaultComboBoxModel<String> paymentReportModel = new DefaultComboBoxModel<String>(paymentReports);
        paymentReportOptions.setModel(paymentReportModel);

        paymentReportBtn.addActionListener(new ReportButtonEvent(paymentReportOptions, paymentReports, paymentQueries, paymentCustomerParams));

        DefaultComboBoxModel<String> customerReportModel = new DefaultComboBoxModel<String>(customerReports);
        customerReportOptions.setModel(customerReportModel);

        customerReportBtn.addActionListener(new ReportButtonEvent(customerReportOptions, customerReports, customerQueries, null));

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

        invRefreshButton.addActionListener(new ActionListener() {
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
                        updateInventoryTable();
                    }
                };
                refreshWorker.execute();
            }
        });
    }

    private SwingWorker<Void, Void> updateInventoryTable() {

        TableModel tableModel = itemTable.getModel();

        final JFrame orderLoading = Connection.createLoadingFrame("Loading inventory...");
        if (tableModel instanceof InventoryTable) {
            ((InventoryTable) tableModel).clearRows();
            tabPane.repaint();
        }

        // Swing worker that will pull the inventory information.
        // Will display a loading message while it runs in the background
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            public Void doInBackground() {
                InventoryTable inventoryModel = new InventoryTable();
                itemTable.setModel(inventoryModel);

                itemTable.setIntercellSpacing(new Dimension(5, 5));
                itemTable.setRowHeight(35);

                JTableButtonRenderer buttonRenderer = new JTableButtonRenderer();

                itemTable.getColumnModel().getColumn(2).setCellRenderer(buttonRenderer);
                itemTable.getColumnModel().getColumn(3).setCellRenderer(buttonRenderer);
                itemTable.removeMouseListener(itemTableMouseListener);
                itemTable.addMouseListener(itemTableMouseListener);

                return null;
            }

            @Override
            public void done() {
                orderLoading.dispose();
            }
        };
        worker.execute();
        return worker;

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
        private boolean[] needLoggedInCustomer;

        public ReportButtonEvent(JComboBox dropDownBox, String[] reports, String[] queries, boolean[] needLoggedInCustomer){
            this.dropDownBox = dropDownBox;
            this.reports = reports;
            this.queries = queries;
            this.needLoggedInCustomer = needLoggedInCustomer;
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
                        generateReport(generatingReportMessage, reports, queries, needLoggedInCustomer, selectedReportIndex);
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, ex.getMessage(), "Error generating report", JOptionPane.ERROR_MESSAGE);
                    }
                    return null;
                }
            };
            reportWorker.execute();
        }
    }

    private static void generateReport(JFrame generatingReportMessage, String[] reportList, String[] queryList,
                                        int selectedReportIndex) throws SQLException {
        generateReport(generatingReportMessage, reportList, queryList, null, selectedReportIndex);
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
    private static void generateReport(JFrame generatingReportMessage, String[] reportList, String[] queryList, boolean[] needLoggedInCustomer, int selectedReportIndex) throws SQLException{
        String selectedReport = reportList[selectedReportIndex];
        String selectedReportQuery = queryList[selectedReportIndex];

        ResultSet queryResult;

        if(needLoggedInCustomer != null && needLoggedInCustomer[selectedReportIndex]) {
            queryResult = Connection.getResultsFromQuery(selectedReportQuery, String.valueOf(Connection.getLoggedInCustomer().getCustomerID()));
        }
        else{
            queryResult = Connection.getResultsFromQuery(selectedReportQuery);
        }

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

        SwingWorker<Void, Void> invUpdateWorker = gui.updateInventoryTable();

        // quick hack to prevent main gui from displaying before inventory update is finished.
        while (!invUpdateWorker.isDone()) {

        }
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(650, 540));
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
    private JButton invRefreshButton;
    private HomeScreen homeScreen;

    // Other variables
    private final JTableButtonMouseListener orderTableMouseListener =  new JTableButtonMouseListener(orderTable);
    private final JTableButtonMouseListener itemTableMouseListener =  new JTableButtonMouseListener(itemTable);

    private static final String[] itemReports = {
            "How many of a specific item has been ordered?",
            "Average item price.",
            "Get most expensive and least expensive item.",
            "What items did a specific customer order?",
            "What kind of Electronics is in the Market Basket inventory?",
            "How many of a specific item was ordered?",
            "What is the top selling item?",
            "What is the lowest selling item?",
            "Query to find all items a customer ordered.",
            "What kind of items do we sell?"

    };
    private static final String[] itemQueries = {
            "Select ItemName,Count(ItemQuantity) as AmountSold from Order_Line_Item\n" +
                    "Join Item on Order_Line_Item.ItemID = Item.ItemID\n" +
                    "Where ItemName = 'Dell laptop'\n" +
                    "Group by Itemname, Itemquantity",
            "Select avg(Price) as AveragePrices from Item",
            "SELECT * FROM ITEM ORDER BY PRICE DESC",
            "Select Distinct Customers.CustomerID, ItemName From Item  Join Order_Line_Item on Item.ItemID = Order_Line_Item.ItemID Join Orders on Order_Line_Item.OrderID = Orders.OrderID Join Customers on Orders.CustomerID = Customers.CustomerID\n",
            "Select * From Item\n" +
                    "Where Category_Type = 'Electronics'",
            "Select Distinct ItemName, ItemQuantity From Item Join Order_Line_Item on Item.ItemID = Order_Line_Item.ItemID",
            "Select Item.Itemid, ItemName, MAX(ItemQuantity) as AmountSold From Item \n" +
                    "Join Order_Line_Item on Item.ItemID = Order_Line_Item.ItemID \n" +
                    "Group by ItemQuantity, itemname, item.itemid\n" +
                    "HAVING MAX(ItemQuantity) > 6",
            "Select Item.Itemid, ItemName, MIN(ItemQuantity) as AmountSold From Item \n" +
                    "Join Order_Line_Item on Item.ItemID = Order_Line_Item.ItemID \n" +
                    "Group by ItemQuantity, itemname, item.itemid\n" +
                    "HAVING MAX(ItemQuantity) <= 1",
            "Select Distinct Customers.CustomerID, ItemName from Item\n" +
                    "Join Order_Line_Item on Item.ItemID = Order_Line_Item.ItemID \n" +
                    "Join Orders on Order_Line_Item.OrderId = Orders.OrderId \n" +
                    "Join Customers on Orders.CustomerID = Customers.CustomerID\n" +
                    "Order by CustomerID asc",
            "Select Distinct Category_Type from Item"
    };

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

    private static final String[] paymentReports = {
            "Sum of payments for current customer",
            "Method of payment for current customer",
            "Number of payments for customer",
            "What payments have been processed? (completed)",
            "Pending payments",
            "All payments for current customer",
            "Customer with most payments",
            "Customer with highest payment",
            "Most used method of payment",
            "Dates payments were made"
    };

    private static final String[] paymentQueries = {
            "SELECT DISTINCT (FIRSTNAME ||' '|| MIDDLEINIT|| ' '|| LASTNAME) AS FULL_NAME, \n" +
                    "\n" +
                    "PAYMENT.CUSTOMERID, SUM(PAYMENTAMOUNT) AS TOTAL_AMOUNT\n" +
                    "\n" +
                    "FROM PAYMENT JOIN CUSTOMERS ON PAYMENT.CUSTOMERID = CUSTOMERS.CUSTOMERID\n" +
                    "\n" +
                    "WHERE PAYMENT.CUSTOMERID = ?\n" +
                    "\n" +
                    "GROUP BY PAYMENT.CUSTOMERID, (FIRSTNAME ||' '|| MIDDLEINIT|| ' '|| LASTNAME)",
            "SELECT DISTINCT PAYMENT.CUSTOMERID, (FIRSTNAME ||' '|| MIDDLEINIT|| ' '|| LASTNAME)\n" +
                    "\n" +
                    "AS FULL_NAME, PAYMENT.PAYMENTMETHOD\n" +
                    "\n" +
                    "FROM PAYMENT JOIN CUSTOMERS ON PAYMENT.CUSTOMERID = CUSTOMERS.CUSTOMERID\n" +
                    "\n" +
                    "WHERE PAYMENT.CUSTOMERID = ?\n" +
                    "\n" +
                    "GROUP BY PAYMENT.CUSTOMERID, (FIRSTNAME||' '|| MIDDLEINIT|| ' '|| \n" +
                    "\n" +
                    "LASTNAME),PAYMENT.PAYMENTMETHOD",
            "SELECT DISTINCT PAYMENT.CUSTOMERID, (FIRSTNAME ||' '|| MIDDLEINIT|| ' '|| LASTNAME) \n" +
                    "\n" +
                    "AS FULL_NAME, COUNT(PAYMENTID)AS NUMBER_OF_PAYMENTS_MADE\n" +
                    "\n" +
                    "FROM PAYMENT JOIN CUSTOMERS ON PAYMENT.CUSTOMERID = CUSTOMERS.CUSTOMERID\n" +
                    "\n" +
                    "WHERE PAYMENT.CUSTOMERID = ?\n" +
                    "\n" +
                    "GROUP BY PAYMENT.CUSTOMERID,(FIRSTNAME||' '|| MIDDLEINIT|| ' '|| LASTNAME)",
            "SELECT DISTINCT PAYMENT.CUSTOMERID, (FIRSTNAME ||' '|| MIDDLEINIT|| ' '|| LASTNAME)\n" +
                    "\n" +
                    "AS FULL_NAME, PAYMENTID, ITEM.ITEMNAME, PAYMENTAMOUNT, PAYMENTDATE AS \n" +
                    "\n" +
                    "DATE_PAYMENT_PROCESSED\n" +
                    "\n" +
                    "FROM PAYMENT JOIN CUSTOMERS ON PAYMENT.CUSTOMERID = CUSTOMERS.CUSTOMERID\n" +
                    "\n" +
                    "JOIN ORDERS ON ORDERS.CUSTOMERID = CUSTOMERS.CUSTOMERID\n" +
                    "\n" +
                    "JOIN ORDER_LINE_ITEM ON ORDER_LINE_ITEM.ORDERID = ORDERS.ORDERID\n" +
                    "\n" +
                    "JOIN ITEM ON ORDER_LINE_ITEM.ITEMID = ITEM.ITEMID\n" +
                    "\n" +
                    "WHERE PAYMENTAMOUNT > 0 \n" +
                    "\n" +
                    "GROUP BY PAYMENT.CUSTOMERID, (FIRSTNAME||' '|| MIDDLEINIT|| ' '|| LASTNAME), \n" +
                    "\n" +
                    "PAYMENTID, PAYMENTDATE, PAYMENTAMOUNT, ITEM.ITEMNAME\n" +
                    "\n" +
                    "ORDER BY FULL_NAME",
            "SELECT DISTINCT PAYMENT.CUSTOMERID, (FIRSTNAME ||' '|| MIDDLEINIT|| ' '||\n" +
                    "\n" +
                    "LASTNAME) AS FULL_NAME, PAYMENTID, BALANCE\n" +
                    "\n" +
                    "FROM PAYMENT JOIN CUSTOMERS ON PAYMENT.CUSTOMERID = \n" +
                    "\n" +
                    "CUSTOMERS.CUSTOMERID\n" +
                    "\n" +
                    "JOIN ORDERS ON ORDERS.CUSTOMERID = CUSTOMERS.CUSTOMERID\n" +
                    "\n" +
                    "JOIN ORDER_LINE_ITEM ON ORDER_LINE_ITEM.ORDERID = ORDERS.ORDERID\n" +
                    "\n" +
                    "JOIN ITEM ON ORDER_LINE_ITEM.ITEMID = ITEM.ITEMID\n" +
                    "\n" +
                    "WHERE BALANCE != 0 \n" +
                    "\n" +
                    "GROUP BY PAYMENT.CUSTOMERID, (FIRSTNAME||' '|| MIDDLEINIT|| ' '|| LASTNAME), \n" +
                    "\n" +
                    "BALANCE, PAYMENTID\n" +
                    "\n" +
                    "ORDER BY PAYMENTID",
            "select firstname, lastname, paymentid, paymentamount from payment join customers on customers.customerid = payment.customerid", // 6 - query that displays all payments from an individual customer (not total)
            "SELECT DISTINCT PAYMENT.CUSTOMERID, (FIRSTNAME ||' '|| MIDDLEINIT|| ' '|| LASTNAME)\n" +
                    "\n" +
                    "AS FULL_NAME, COUNT(PAYMENTID) AS NUMBER_OF_PAYMENTS_MADE\n" +
                    "\n" +
                    "FROM PAYMENT JOIN CUSTOMERS ON PAYMENT.CUSTOMERID = CUSTOMERS.CUSTOMERID\n" +
                    "\n" +
                    "GROUP BY PAYMENT.CUSTOMERID, (FIRSTNAME ||' '|| MIDDLEINIT|| ' '|| LASTNAME)",
            "SELECT DISTINCT PAYMENT.CUSTOMERID, (FIRSTNAME ||' '|| MIDDLEINIT|| ' '|| LASTNAME) AS FULL_NAME, MAX(PAYMENTAMOUNT) AS HIGHEST_PAYMENT\n" +
                    "FROM PAYMENT JOIN CUSTOMERS ON PAYMENT.CUSTOMERID = CUSTOMERS.CUSTOMERID\n" +
                    "GROUP BY PAYMENT.CUSTOMERID, (FIRSTNAME ||' '|| MIDDLEINIT|| ' '|| LASTNAME)", // 8 - highest payment
            "SELECT DISTINCT PAYMENTMETHOD, COUNT(*) AS NUM_OF_TIMES_USED\n" +
                    "FROM PAYMENT JOIN CUSTOMERS ON PAYMENT.CUSTOMERID = CUSTOMERS.CUSTOMERID\n" +
                    "GROUP BY PAYMENTMETHOD\n" +
                    "ORDER BY COUNT(*) DESC", // 9 - most used method of payment
            "select firstname, lastname, paymentid, paymentdate from payment join customers on customers.customerid = payment.customerid" // 10 - what dates payments are made
    };

    private static final String[] customerReports = {
            "What is the age demographic of our customers?",
            "What is the regional demographic of our customers?",
            "What is a specific customers balance?",
            "What is the average amount spent by a specific customer across all of their orders?",
            "How many of a specific category of items has a customer purchased?",
            "Which customers may also be purchasing for their company?",
            "List the customers who opt for email notifications on their purchases.",
            "What payment methods have been used and how often?",
            "Which customers DON’T have an account?",
            "Which customers have a shipping address different than their billing address?"
    };

    private static final String[] customerQueries = {
            "SELECT CASE\n" +
                    "  WHEN AGE <= 18 THEN '0-18'\n" +
                    "  WHEN AGE > 18 AND AGE <= 32 THEN '19-32'\n" +
                    "  WHEN AGE > 32 AND AGE <= 51 THEN '33-51'\n" +
                    "  WHEN AGE > 51 AND AGE <= 64 THEN '52-64'\n" +
                    "  ELSE '65+'\n" +
                    "END AS \"AGE RANGE\",\n" +
                    "  COUNT(*) AS COUNT\n" +
                    "FROM CUSTOMERS\n" +
                    " \n" +
                    "GROUP BY CASE\n" +
                    "  WHEN AGE <= 18 THEN '0-18'\n" +
                    "  WHEN AGE > 18 AND AGE <= 32 THEN '19-32'\n" +
                    "  WHEN AGE > 32 AND AGE <= 51 THEN '33-51'\n" +
                    "  WHEN AGE > 51 AND AGE <= 64 THEN '52-64'\n" +
                    "  ELSE '65+'\n" +
                    "END\n",
            "SELECT COUNTRY, COUNT(COUNTRY) AS COUNT FROM ADDRESS\n" +
                    "GROUP BY COUNTRY\n",
            "SELECT CUSTOMERS.CUSTOMERID, FIRSTNAME, LASTNAME, SUM(BALANCE) FROM CUSTOMERS JOIN PAYMENT ON CUSTOMERS.CUSTOMERID = PAYMENT.CUSTOMERID\n" +
                    "GROUP BY CUSTOMERS.CUSTOMERID, FIRSTNAME, LASTNAME",
            "SELECT CUSTOMERS.CUSTOMERID, FIRSTNAME, LASTNAME, AVG(TOTALPRICE) FROM CUSTOMERS JOIN ORDERS ON CUSTOMERS.CUSTOMERID = ORDERS.CUSTOMERID\n" +
                    "GROUP BY CUSTOMERS.CUSTOMERID, FIRSTNAME, LASTNAME",
            "SELECT CUSTOMERS.CUSTOMERID, FIRSTNAME, LASTNAME, CATEGORY_TYPE, COUNT(CATEGORY_TYPE) FROM CUSTOMERS\n" +
                    "JOIN ORDERS ON CUSTOMERS.CUSTOMERID = ORDERS.CUSTOMERID\n" +
                    "JOIN ORDER_LINE_ITEM ON ORDERS.ORDERID = ORDER_LINE_ITEM.ORDERID\n" +
                    "JOIN ITEM ON ORDER_LINE_ITEM.ITEMID = ITEM.ITEMID\n" +
                    "GROUP BY CUSTOMERS.CUSTOMERID, CATEGORY_TYPE, FIRSTNAME, LASTNAME",
            "SELECT CUSTOMERID, FIRSTNAME, LASTNAME, COMPANY FROM CUSTOMERS\n" +
                    "WHERE COMPANY IS NOT NULL",
            "SELECT CUSTOMERS.CUSTOMERID, FIRSTNAME, LASTNAME, ACCOUNT.RECEIVENOTIF FROM CUSTOMERS JOIN ACCOUNT ON CUSTOMERS.CUSTOMERID = ACCOUNT.CUSTOMERID\n" +
                    "WHERE RECEIVENOTIF = 'y'",
            "SELECT PAYMENTMETHOD, COUNT(PAYMENTMETHOD) FROM PAYMENT\n" +
                    "GROUP BY PAYMENTMETHOD",
            "SELECT CUSTOMERS.CUSTOMERID, ACCOUNT.CUSTOMERID AS ACCOUNTID FROM CUSTOMERS LEFT JOIN ACCOUNT ON ACCOUNT.CUSTOMERID = CUSTOMERS.CUSTOMERID\n" +
                    "WHERE ACCOUNT.CUSTOMERID IS NULL",
            "SELECT FIRSTNAME, LASTNAME FROM CUSTOMERS JOIN ADDRESS ON ADDRESS.CUSTOMERID = CUSTOMERS.CUSTOMERID\n" +
                    "WHERE ISSHIPPING = 'false'"
    };

    private static final boolean[] paymentCustomerParams = {
            true,
            true,
            true,
            false,
            false,
            false,
            false,
            false,
            false,
            false
    };
}
