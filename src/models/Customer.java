package models;

import utils.Connection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * William Trent Holliday
 * 4/15/15
 */
public class Customer {

    private int customerID;
    private String firstName;
    private String middleInit;
    private String lastName;
    private String email;
    private long phoneNumber;
    private int age;
    private String company;
    private String receiveNotification;
    private List<Map> cartItems = new ArrayList<Map>();
    private List<Address> shippingAddresses = new ArrayList<Address>();
    private List<Address> billingAddresses = new ArrayList<Address>();

    public Customer(int id, String fName, String middleInitial, String lName, String email,
                    long phoneNum, int age, String company, String receiveNotification, List<Address> shipList, List<Address> billList) {
        this.customerID = id;
        this.firstName = fName;
        this.middleInit = middleInitial;
        this.lastName = lName;
        this.email = email;
        this.phoneNumber = phoneNum;
        this.age = age;
        this.company = company;
        this.receiveNotification = receiveNotification;
        this.shippingAddresses = shipList;
        this.billingAddresses = billList;
    }

    public static Customer createCustomerFromQuery(ResultSet user, String password) {
        try {

            int rowcount = 0;
            if (user.last()) {
                rowcount = user.getRow();
                user.beforeFirst(); // not rs.first() because the rs.next() below will move on, missing the first element
            }

            if (rowcount == 0) {
                return null;
            }

            while(user.next()) {
                int customerID = user.getInt("CUSTOMERID");
                Account account = getAccountInfo(customerID, password);
                // If account is null then that means we were unable to find an account for the
                // specified customer id and password, which means that we were given bad login
                // credentials.
                if (account == null){
                    return null;
                }
                String firstName = user.getString("FIRSTNAME");
                String middleInit = user.getString("MIDDLEINIT");
                String lastName = user.getString("LASTNAME");
                String email = user.getString("EMAIL");
                long phoneNum = user.getLong("PHONENUMBER");
                int age = user.getInt("AGE");
                String company = user.getString("COMPANY");
                String notif = account.getReceiveNotif();
                List<Address> billingList = Address.getAddressesForCustomer(customerID, true);
                List<Address> shippingList = Address.getAddressesForCustomer(customerID, false);

                return new Customer(
                        customerID, firstName, middleInit, lastName, email,
                        phoneNum, age, company, notif, shippingList, billingList
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Account getAccountInfo(int customerID, String password) {
        String accountQuery = "select * from account where customerid = ? and password = ?";
        ResultSet resultSet = Connection.getResultsFromQuery(accountQuery, String.valueOf(customerID), password);

        try {
            while(resultSet.next()) {
                String receiveNotif = resultSet.getString("receivenotif");
                return new Account(receiveNotif);
            }
        } catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public void addItemToCart(Item item, int quantity) {
        Map itemMap = new HashMap();
        itemMap.put("item", item);
        itemMap.put("quantity", quantity);
        cartItems.add(itemMap);
    }

    public void removeItemFromCart(int itemIndex) {
        cartItems.remove(itemIndex);
    }

    public List<Map> getCartItems() {
        return cartItems;
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleInit() {
        return middleInit;
    }

    public void setMiddleInit(String middleInit) {
        this.middleInit = middleInit;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(long phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getReceiveNotification() {
        return receiveNotification;
    }

    public void setReceiveNotification(String receiveNotification) {
        this.receiveNotification = receiveNotification;
    }

    public List<Address> getShippingAddresses() {
        return shippingAddresses;
    }

    public List<Address> getBillingAddresses() {
        return billingAddresses;
    }
}
