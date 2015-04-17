package models;

/**
 * Created by Dr.H on 4/17/2015.
 */
public class Address {

    private long addressid;
    private char addressLine;
    private char city;
    private int zip;
    private char country;
    private char state;

    public Address(long addressid, char addressLine, char city, int zip, char country, char state) {
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

    public char getAddressLine() {
        return addressLine;
    }

    public char getCity() {
        return city;
    }

    public int getZip() {
        return zip;
    }

    public char getCountry() {
        return country;
    }

    public char getState() {
        return state;
    }
}
