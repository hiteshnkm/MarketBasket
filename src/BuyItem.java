import javax.swing.*;
import java.awt.event.*;

public class BuyItem extends JDialog {
    private JPanel contentPane;
    private JButton buyButton;
    private JButton buttonCancel;
    private JLabel buyLabel;
    private JLabel descriptionLabel;
    private JLabel pricePerUnit;
    private JLabel category;
    private JComboBox purchaseQuantityBox;
    private JLabel itemQuantity;
    private JLabel totalOrderCost;

    public BuyItem() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buyButton);

        buyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

// call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

// call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
// add your code here
        dispose();
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }
}
