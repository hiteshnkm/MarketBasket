import models.Customer;
import models.Item;
import models.Order;
import utils.Connection;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ArrayList;

/**
 * William Trent Holliday
 * 4/16/15
 */
public class OrderTable  extends AbstractTableModel {
    private static final long serialVersionUID = 1L;
    private static final String[] COLUMN_NAMES = new String[] {"Order ID", "Order Date", "Total Price", "View Details", "Make Payment"};
    private static final Class<?>[] COLUMN_TYPES = new Class<?>[] {int.class, String.class, Long.class, JButton.class,  JButton.class};
    private static ArrayList<Order> orderList = new ArrayList<Order>();
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();

    public OrderTable(Customer customer){
        int customerID = customer.getCustomerID();
        ResultSet orderResults = Connection.getResultsFromQuery("select * from orders where customerid = ?", String.valueOf(customerID));

        try {
            while (orderResults.next()) {
                Order dbOrder = Connection.createOrderFromQuery(orderResults);
                if(dbOrder != null) {
                    orderList.add(dbOrder);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    @Override public int getRowCount() {
        return orderList.size();
    }

    @Override public String getColumnName(int columnIndex) {
        return COLUMN_NAMES[columnIndex];
    }

    @Override public Class<?> getColumnClass(int columnIndex) {
        return COLUMN_TYPES[columnIndex];
    }

    @Override public Object getValueAt(final int rowIndex, final int columnIndex) {
            /*Adding components*/
        final Order rowOrder = orderList.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return rowOrder.getOrderid();
            case 1:
                return rowOrder.getOrderDate();
            case 2:
                return currencyFormat.format(rowOrder.getTotalPrice());
            case 3:
                final JButton detail_button = new JButton(COLUMN_NAMES[columnIndex]);
                detail_button.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent arg0) {
                        JOptionPane.showMessageDialog(null, "Clicked view details.");
                    }
                });
                return detail_button;
            case 4:
                final JButton make_payment = new JButton(COLUMN_NAMES[columnIndex]);
                make_payment.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent arg0) {
                        JOptionPane.showMessageDialog(null, "Make payment");
                    }
                });
                return make_payment;
            default:
                return "Error";
        }
    }
}