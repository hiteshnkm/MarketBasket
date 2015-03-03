import javax.swing.*;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * William Trent Holliday
 * 3/2/15
 */
public class mainGUI {

    public mainGUI(){
        JFrame mainFrame = new JFrame("Market Basket");
        JPanel contentPane = new JPanel();
        mainFrame.setContentPane(contentPane);
        contentPane.add(new JLabel("test"));

        mainFrame.setSize(400, 300);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setVisible(true);
    }

    public static void main(String[] args) {
        new mainGUI();
    }

}
