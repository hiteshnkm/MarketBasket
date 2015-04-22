package models;

import utils.Connection;

import javax.swing.*;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Dr.H on 4/17/2015.
 */
public class Order {

    private long orderid;
    private int customerid;
    private Date orderDate;
    private double subTotal;
    private double taxes;
    private double totalPrice;
    private String shippingName;
    private Address shippingAddress;
    private Address BillingAddress;

    public Order(long orderid, int customerid, Date orderDate, double subTotal, double taxes, double totalPrice, String shippingName, Address shippingAddress, Address billingAddress) {
        this.orderid = orderid;
        this.customerid = customerid;
        this.orderDate = orderDate;
        this.subTotal = subTotal;
        this.taxes = taxes;
        this.totalPrice = totalPrice;
        this.shippingName = shippingName;
        this.shippingAddress = shippingAddress;
        BillingAddress = billingAddress;
    }

    public boolean createNewOrder(Customer customer, double subTotal, double taxes, double totalPrice, Address shippingAddress, Address billingAddress){
        String sqlQuery = "insert into orders (" +
                "CUSTOMERID, ORDERDATE, " +
                "SUBTOTAL, TAXES, " +
                "TOTALPRICE, SHIPPINGNAME, " +
                "SHIPADDRESS, BILLINGADDRESS" +
                ")" +
                "VALUES (" +
                "?, ?, ?, ?, ?, ?, ?, ?);";
        java.sql.Date sqlDate = new java.sql.Date(new java.util.Date().getTime());
        ResultSet createOrderResult = Connection.getResultsFromQuery(
                sqlQuery,
                // Parameters to the sql query
                String.valueOf(customer.getCustomerID()),
                String.valueOf(sqlDate),
                String.valueOf(subTotal),
                String.valueOf(taxes),
                String.valueOf(totalPrice),
                customer.getFirstName() + " " + customer.getLastName(),
                String.valueOf(shippingAddress.getAddressid()),
                String.valueOf(billingAddress.getAddressid())
        );
        try {
            while(createOrderResult.next()){
                JOptionPane.showMessageDialog(null, createOrderResult.getString(0));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public long getOrderid() {
        return orderid;
    }

    public int getCustomerid() {
        return customerid;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public double getSubTotal() {
        return subTotal;
    }

    public double getTaxes() {
        return taxes;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public String getShippingName() {
        return shippingName;
    }

    public Address getShippingAddress() {
        return shippingAddress;
    }

    public Address getBillingAddress() {
        return BillingAddress;
    }
}
