package models.db;

/**
 * William Trent Holliday
 * 4/21/15
 */
public class Account {
    private String receiveNotif;

    public Account(String receiveNotif) {
        this.receiveNotif = receiveNotif;
    }

    public String getReceiveNotif() {
        return receiveNotif;
    }
}
