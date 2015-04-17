package models;

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
