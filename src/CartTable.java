import models.Customer;
import models.Item;
import utils.Connection;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * William Trent Holliday
 * 4/16/15
 */
public class CartTable extends AbstractTableModel {
    private static final long serialVersionUID = 1L;
    private static final String[] COLUMN_NAMES = new String[] {"Item Name", "Price", "Quantity", "Remove"};
    private static final Class<?>[] COLUMN_TYPES = new Class<?>[] {String.class, Long.class, int.class,  JButton.class};
    private static List<Map> itemList = new ArrayList<Map>();
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
    private Customer loggedInCustomer;

    public CartTable (){
        loggedInCustomer = Connection.getLoggedInCustomer();
        itemList = loggedInCustomer.getCartItems();
    }

    @Override public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    @Override public int getRowCount() {
        return itemList.size();
    }

    @Override public String getColumnName(int columnIndex) {
        return COLUMN_NAMES[columnIndex];
    }

    @Override public Class<?> getColumnClass(int columnIndex) {
        return COLUMN_TYPES[columnIndex];
    }

    @Override public Object getValueAt(final int rowIndex, final int columnIndex) {
            /*Adding components*/
        final Map rowMap = itemList.get(rowIndex);
        final Item rowItem = ((Item)rowMap.get("item"));
        final Integer itemQuantity = ((Integer) rowMap.get("quantity"));
        switch (columnIndex) {
            case 0:
                return rowItem.getItemName();
            case 1:
                return currencyFormat.format(rowItem.getPrice());
            case 2:
                return itemQuantity;
            case 3:
                final JButton removeItem = new JButton(COLUMN_NAMES[columnIndex]);
                removeItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        loggedInCustomer.removeItemFromCart(rowIndex);
                    }
                });
                return removeItem;
            default:
                return "Error";
        }
    }

    public List<Map> getCartItems(){
        return itemList;
    }
}
