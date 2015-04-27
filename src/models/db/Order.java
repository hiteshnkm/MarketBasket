package models.db;

import utils.Connection;

import javax.swing.*;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

/**
 * Created by Dr.H on 4/17/2015.
 */
public class Order {

    private long orderid;
    private int customerid;
    private Date orderDate;
    private Date shipDate;
    private double subTotal;
    private double taxes;
    private double totalPrice;
    private String shippingName;
    private Address shippingAddress;
    private Address BillingAddress;

    public Order(long orderid, int customerid, Date orderDate, Date shipDate, double subTotal, double taxes, double totalPrice, String shippingName, Address shippingAddress, Address billingAddress) {
        this.orderid = orderid;
        this.customerid = customerid;
        this.orderDate = orderDate;
        this.shipDate = shipDate;
        this.shipDate = shipDate;
        this.subTotal = subTotal;
        this.taxes = taxes;
        this.totalPrice = totalPrice;
        this.shippingName = shippingName;
        this.shippingAddress = shippingAddress;
        BillingAddress = billingAddress;
    }

    public static Order createNewOrder(Customer customer, double subTotal, double taxes, double totalPrice, Address shippingAddress, Address billingAddress){
        String sqlQuery = "insert into ORDERS (" +
                "CUSTOMERID, ORDERDATE, " +
                "SUBTOTAL, TAXES, " +
                "TOTALPRICE, SHIPPINGNAME, " +
                "SHIPADDRESS, BILLINGADDRESS" +
                ")" +
                "VALUES (" +
                "?, TO_DATE(?, 'MM/DD/YYYY'), ?, ?, ?, ?, ?, ?)";
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/YYYY");

        java.sql.Date sqlDate = new java.sql.Date(new java.util.Date().getTime());
        ResultSet createOrderResult = Connection.getResultsFromQuery(
                sqlQuery,
                // Parameters to the sql query
                String.valueOf(customer.getCustomerID()),
                dateFormat.format(sqlDate),
                String.valueOf(subTotal),
                String.valueOf(taxes),
                String.valueOf(totalPrice),
                customer.getFirstName() + " " + customer.getLastName(),
                String.valueOf(shippingAddress.getAddressid()),
                String.valueOf(billingAddress.getAddressid())
        );

        String getNewCustomerQuery = "select * from orders where orderid = (select max(orderid) from orders)";
        ResultSet lastCreatedOrder = Connection.getResultsFromQuery(getNewCustomerQuery);
        try{
            while(lastCreatedOrder.next()){
                return createOrderFromQuery(lastCreatedOrder);
            }
        } catch(SQLException ex){
            ex.printStackTrace();
        }
        return null;
    }

    public static Order createOrderFromQuery(ResultSet orderResults) {
        try {
            long orderid = orderResults.getLong("ORDERID");
            int customerid = orderResults.getInt("CUSTOMERID");
            Date orderDate = orderResults.getDate("ORDERDATE");
            Date shipDate = orderResults.getDate("SHIPPEDDATE");
            double subTotal = orderResults.getDouble("SUBTOTAL");
            double taxes = orderResults.getDouble("TAXES");
            double totalPrice = orderResults.getDouble("TOTALPRICE");
            String shippingName = orderResults.getString("SHIPPINGNAME");
            int shippingAddressID = orderResults.getInt("SHIPADDRESS");
            Address shippingAddress = Address.getAddressByID(shippingAddressID);
            int billingAddressID = orderResults.getInt("BILLINGADDRESS");
            Address billingAddress = Address.getAddressByID((billingAddressID));

            return new Order(orderid, customerid, orderDate, shipDate, subTotal,
                    taxes, totalPrice, shippingName, shippingAddress, billingAddress);

        } catch (SQLException ex){
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Error loading orders from database.", JOptionPane.ERROR_MESSAGE);
            return null;
        }
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

    public Date getShipDate() {
        return shipDate;
    }
}
