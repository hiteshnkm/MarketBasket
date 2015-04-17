package models;

import java.sql.Date;

/**
 * Created by Dr.H on 4/17/2015.
 */
public class Order {

    private long orderid;
    private int customerid;
    private Date orderdate;
    private double subtotal;
    private double taxes;
    private char shippingname;


    public Order(long orderid, int customerid, Date orderdate, double subtotal, double taxes, char shippingname) {
        this.orderid = orderid;
        this.customerid = customerid;
        this.orderdate = orderdate;
        this.subtotal = subtotal;
        this.taxes = taxes;
        this.shippingname = shippingname;
    }

    public long getOrderid() {
        return orderid;
    }

    public int getCustomerid() {
        return customerid;
    }

    public Date getOrderdate() {
        return orderdate;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public double getTaxes() {
        return taxes;
    }

    public char getShippingname() {
        return shippingname;
    }
}
