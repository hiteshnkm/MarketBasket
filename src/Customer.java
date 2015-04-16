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
    private int phoneNumber;
    private int age;
    private String company;
    private String receiveNotification;

    public Customer(int id, String fName, String middleInitial, String lName, String email,
                    int phoneNum, int age, String company, String receiveNotification) {
        this.customerID = id;
        this.firstName = fName;
        this.middleInit = middleInitial;
        this.lastName = lName;
        this.email = email;
        this.phoneNumber = phoneNum;
        this.age = age;
        this.company = company;
        this.receiveNotification = receiveNotification;
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

            int customerID;
            String firstName;
            String middleInit;
            String lastName;
            String email;
            int phoneNum;
            int age;
            String company;
            String notif;
            while(user.next()) {
                customerID = user.getInt("CUSTOMERID");
                firstName = user.getString("FIRSTNAME");
                middleInit = user.getString("MIDDLEINIT");
                lastName = user.getString("LASTNAME");
                email = user.getString("EMAIL");
                phoneNum = user.getInt("PHONENUMBER");
                age = user.getInt("AGE");
                company = user.getString("COMPANY");
                notif = user.getString("RECEIVENOTIF");

                return new Customer(
                        customerID, firstName, middleInit, lastName, email,
                        phoneNum, age, company, notif
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
