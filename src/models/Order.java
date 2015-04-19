package models;

import java.sql.Date;

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
    private char shippingName;
    private Address shippingAddress;
    private Address BillingAddress;

    public Order(long orderid, int customerid, Date orderDate, double subTotal, double taxes, double totalPrice, char shippingName, Address shippingAddress, Address billingAddress) {
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

    public char getShippingName() {
        return shippingName;
    }

    public Address getShippingAddress() {
        return shippingAddress;
    }

    public Address getBillingAddress() {
        return BillingAddress;
    }
}
