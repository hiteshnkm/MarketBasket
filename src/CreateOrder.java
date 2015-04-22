import javax.swing.*;
import java.awt.event.*;

public class CreateOrder extends JDialog {
    private JPanel contentPane;
    private JButton placeOrder;
    private JButton buttonCancel;
    private JList itemReceiptList;
    private JLabel subtotalLabel;
    private JLabel taxesLabel;
    private JLabel totalPriceLabel;

    public CreateOrder() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(placeOrder);

        placeOrder.addActionListener(new ActionListener() {
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
