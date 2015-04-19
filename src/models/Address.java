package models;

import utils.Connection;

import javax.swing.*;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Dr.H on 4/17/2015.
 */
public class Address {

    private long addressid;
    private String addressLine;
    private String city;
    private int zip;
    private String country;
    private String state;

    public Address(long addressid, String addressLine, String city, int zip, String country, String state) {
        this.addressid = addressid;
        this.addressLine = addressLine;
        this.city = city;
        this.zip = zip;
        this.country = country;
        this.state = state;
    }

    public long getAddressid() {
        return addressid;
    }

    public String getAddressLine() {
        return addressLine;
    }

    public String getCity() {
        return city;
    }

    public int getZip() {
        return zip;
    }

    public String getCountry() {
        return country;
    }

    public String getState() {
        return state;
    }

    public static Address getAddressByID(int addressID) {
        String addressQuery = "Select * from address where addressid = ?";
        ResultSet addressResults = Connection.getResultsFromQuery(addressQuery, String.valueOf(addressID));
        try {
            while (addressResults.next()) {
                long addressid = addressResults.getLong("ADDRESSID");
                String addressLine = addressResults.getString("ADDRESSLINE");
                String city = addressResults.getString("CITY");
                int zip = addressResults.getInt("ZIP");
                String country = addressResults.getString("COUNTRY");
                String state = addressResults.getString("STATE");

                return new Address(addressid, addressLine, city, zip, country, state);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Error loading Address from database.", JOptionPane.ERROR_MESSAGE);
        }

        return null;
    }
}
