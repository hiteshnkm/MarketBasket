package models.db;

import javax.swing.*;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * William Trent Holliday
 * 4/17/15
 */
public class Item {
    private long itemid;
    private String itemName;
    private String description;
    private String categoryType;
    private double price;
    private int quantity;

    public Item(long itemid, String itemName, String description,
                String categoryType, double price, int quantity) {
        this.itemid = itemid;
        this.itemName = itemName;
        this.description = description;
        this.categoryType = categoryType;
        this.price = price;
        this.quantity = quantity;
    }

    public static Item createItemFromQuery(ResultSet inventoryResults) {
        try {
            Long itemid = inventoryResults.getLong("itemid");
            String itemname = inventoryResults.getString("itemname");
            String description = inventoryResults.getString("description");
            String category = inventoryResults.getString("category_type");
            Double price = inventoryResults.getDouble("price");
            int quantity = inventoryResults.getInt("quantity");

            return new Item(itemid, itemname, description, category, price, quantity);
        } catch(SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error loading items from database.", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    public long getItemid() {
        return itemid;
    }

    public String getItemName() {
        return itemName;
    }

    public String getDescription() {
        return description;
    }

    public String getCategoryType() {
        return categoryType;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }
}
