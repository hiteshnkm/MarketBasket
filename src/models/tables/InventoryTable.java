package models.tables;

import gui.dialogs.BuyItem;
import gui.dialogs.ItemDetail;
import models.db.Item;
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
public class InventoryTable  extends AbstractTableModel {
    private static final long serialVersionUID = 1L;
    private static final String[] COLUMN_NAMES = new String[] {"Item Name", "Price", "", ""};
    private static final Class<?>[] COLUMN_TYPES = new Class<?>[] {String.class, Long.class, JButton.class, JButton.class};
    private static ArrayList<Item> itemList = new ArrayList<Item>();
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();

    public InventoryTable(){
        ResultSet inventoryResults = Connection.getResultsFromQuery("select * from item");

        try {
            while (inventoryResults.next()) {
                Item dbItem = Item.createItemFromQuery(inventoryResults);
                if(dbItem != null) {
                    itemList.add(dbItem);
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
        final Item rowItem = itemList.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return rowItem.getItemName();
            case 1:
                return currencyFormat.format(rowItem.getPrice());
            case 2:
                final JButton detail_button = new JButton("Details");
                detail_button.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent arg0) {
                        ItemDetail dialog = new ItemDetail(rowItem);
                        dialog.pack();
                        dialog.setLocationRelativeTo(JOptionPane.getFrameForComponent(detail_button));
                        dialog.setVisible(true);
                    }
                });
                return detail_button;
            case 3:
                final JButton buy_button = new JButton("Buy");
                buy_button.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent arg0) {
//                        Connection.placeOrder(rowItem);
                        BuyItem buyDialog = new BuyItem(rowItem);
                        buyDialog.pack();
                        buyDialog.setLocationRelativeTo(JOptionPane.getFrameForComponent(buy_button));
                        buyDialog.setVisible(true);
                    }
                });
                return buy_button;
            default:
                return "Error";
        }
    }
}
