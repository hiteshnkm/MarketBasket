package models;

import utils.Connection;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * William Trent Holliday
 * 4/22/15
 */
public class OrderLineItem {

    private long lineid;
    private long itemid;
    private long orderid;
    private double amountMoney;
    private int itemQuantity;

    public OrderLineItem(long lineid, long itemid, long orderid, double amountMoney, int itemQuantity) {
        this.lineid = lineid;
        this.itemid = itemid;
        this.orderid = orderid;
        this.amountMoney = amountMoney;
        this.itemQuantity = itemQuantity;
    }

    public static void createOrderLine(Order order, Item item, Integer quantity){
        String sqlQuery = "insert into ORDER_LINE_ITEM (" +
                "ITEMID, AMTMONEY, ITEMQUANTITY, ORDERID" +
                ") " +
                "VALUES (" +
                "?, ?, ?, ? )";
        double amtMoney = item.getPrice() * quantity;
        ResultSet resultSet = Connection.getResultsFromQuery(
                sqlQuery,
                // SQL parameters
                String.valueOf(item.getItemid()),
                String.valueOf(amtMoney),
                String.valueOf(quantity),
                String.valueOf(order.getOrderid())
        );

    }

    public long getLineid() {
        return lineid;
    }

    public long getItemid() {
        return itemid;
    }

    public long getOrderid() {
        return orderid;
    }

    public double getAmountMoney() {
        return amountMoney;
    }

    public int getItemQuantity() {
        return itemQuantity;
    }
}
