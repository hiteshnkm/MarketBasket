import javax.swing.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * William Trent Holliday
 * 1/30/15
 */
public class BasketGUI {
    private JTabbedPane tabPane;
    private JPanel panel1;
    private JPanel inventoryTab;
    private JPanel orderTab;
    private JList inventoryList;

    public BasketGUI() {

    }

    public static void main(String[] args) {
        java.sql.Connection connection = Connection.getConnection();
        BasketGUI gui = new BasketGUI();
        JFrame frame = new JFrame("BasketGUI");
        frame.setContentPane(gui.panel1);
        DefaultListModel listModel = new DefaultListModel();
        try {
            Statement invStatement = connection.createStatement();
            ResultSet invResults = invStatement.executeQuery("select * from item");
            while(invResults.next())
                listModel.addElement(invResults.getString("itemname") + " - $" + invResults.getDouble("price"));
        }catch(SQLException ex){
            System.out.println(ex);
        }
        gui.inventoryList.setModel(listModel);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
