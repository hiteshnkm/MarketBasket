import models.Address;
import models.Customer;

import javax.swing.*;

/**
 * William Trent Holliday
 * 4/16/15
 */
public class HomeScreen {
    private JLabel usernameLabel;
    private JLabel customerID;
    private JLabel firstName;
    private JLabel middleInitial;
    private JLabel lastName;
    private JLabel email;
    private JLabel phoneNumber;
    private JLabel age;
    private JLabel company;
    private JLabel getNotifications;
    private JPanel welcomePanel;
    private JButton logoutButton;

    public HomeScreen(Customer customer){
        usernameLabel.setText(customer.getFirstName());
        customerID.setText(String.valueOf(customer.getCustomerID()));
        firstName.setText(customer.getFirstName());
        middleInitial.setText(customer.getMiddleInit());
        lastName.setText(customer.getLastName());
        email.setText(customer.getEmail());
        phoneNumber.setText(String.valueOf(customer.getPhoneNumber()));
        age.setText(String.valueOf(customer.getAge()));
        company.setText(customer.getCompany());
        getNotifications.setText(customer.getReceiveNotification());
    }

    public JPanel getMainPanel(){
        return welcomePanel;
    }

    public JButton getLogoutButton(){
        return this.logoutButton;
    }
}
