package models;

import java.sql.ResultSet;
import java.sql.SQLException;

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
    private Address address;

    public Customer(int id, String fName, String middleInitial, String lName, String email,
                    long phoneNum, int age, String company, String receiveNotification, Address address) {
        this.customerID = id;
        this.firstName = fName;
        this.middleInit = middleInitial;
        this.lastName = lName;
        this.email = email;
        this.phoneNumber = phoneNum;
        this.age = age;
        this.company = company;
        this.receiveNotification = receiveNotification;
        this.address = address;
    }

    public static Customer createCustomerFromQuery(ResultSet user) {
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
                String firstName = user.getString("FIRSTNAME");
                String middleInit = user.getString("MIDDLEINIT");
                String lastName = user.getString("LASTNAME");
                String email = user.getString("EMAIL");
                long phoneNum = user.getLong("PHONENUMBER");
                int age = user.getInt("AGE");
                String company = user.getString("COMPANY");
                String notif = user.getString("RECEIVENOTIF");
                int addressID = user.getInt("ADDRESS");
                Address address = Address.getAddressByID(addressID);

                return new Customer(
                        customerID, firstName, middleInit, lastName, email,
                        phoneNum, age, company, notif, address
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
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

    public Address getAddress() {
        return address;
    }
}
