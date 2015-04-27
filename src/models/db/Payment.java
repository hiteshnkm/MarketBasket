package models.db;

import utils.Connection;

import javax.swing.*;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * William Trent Holliday
 * 4/26/15
 */
public class Payment {

    private long paymentID;
    private Date paymentDate;
    private double paymentAmount;
    private double balance;
    private String paymentMethod;
    private long customerID;
    private long orderID;

    public Payment(long orderID, long paymentID, Date paymentDate, double paymentAmount, double balance, String paymentMethod, long customerID) {
        this.orderID = orderID;
        this.paymentID = paymentID;
        this.paymentDate = paymentDate;
        this.paymentAmount = paymentAmount;
        this.balance = balance;
        this.paymentMethod = paymentMethod;
        this.customerID = customerID;
    }

    public static ArrayList<Payment> getPaymentsForOrder(long orderid) {
        String sqlQuery = "select * from payment where orderid = ?";
        ResultSet paymentResults = Connection.getResultsFromQuery(sqlQuery, String.valueOf(orderid));

        ArrayList<Payment> paymentsForOrder = new ArrayList<Payment>();
        try {
            while (paymentResults.next()) {
                long paymentID = paymentResults.getLong("paymentid");
                Date paymentDate = paymentResults.getDate("paymentdate");
                double paymentAmount = paymentResults.getDouble("paymentAmount");
                double balance = paymentResults.getDouble("balance");
                String paymentMethod = paymentResults.getString("paymentmethod");
                long customerID = paymentResults.getLong("customerid");
                long orderID = paymentResults.getLong("orderID");

                Payment payment = new Payment(orderID, paymentID, paymentDate,
                        paymentAmount, balance, paymentMethod, customerID);
                paymentsForOrder.add(payment);
            }
        }
        catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Error loading payments.", JOptionPane.ERROR_MESSAGE);
        }

        return paymentsForOrder;
    }

    public static void createNewPayment(Order order, double paymentAmount, double balance, String paymentMethod){
        String sqlQuery = "insert into payment (paymentdate, paymentamount, balance, paymentmethod, customerid, orderid) " +
                            "VALUES (TO_DATE(?, 'MM/DD/YYYY'), ?, ?, ?, ?, ?)";

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/YYYY");

        java.sql.Date sqlDate = new java.sql.Date(new java.util.Date().getTime());

        ResultSet createPaymentResult = Connection.getResultsFromQuery(
                sqlQuery, // Sql query insert statement
                dateFormat.format(sqlDate), // payment date
                String.valueOf(paymentAmount), // payment amount
                String.valueOf(balance), // balance on order
                paymentMethod, // payment method type
                String.valueOf(order.getCustomerid()), // customer id
                String.valueOf(order.getOrderid()) // order id
        );
        try {
            while (createPaymentResult.next()) {
                JOptionPane.showMessageDialog(null, createPaymentResult.getString(0));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Error creating payment.", JOptionPane.ERROR_MESSAGE);
        }

    }

    public long getPaymentID() {
        return paymentID;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public double getPaymentAmount() {
        return paymentAmount;
    }

    public double getBalance() {
        return balance;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public long getCustomerID() {
        return customerID;
    }

    public long getOrderID() {
        return orderID;
    }
}
