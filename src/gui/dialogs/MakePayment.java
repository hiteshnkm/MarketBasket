package gui.dialogs;

import models.db.Order;
import models.db.Payment;

import javax.swing.*;
import java.awt.event.*;

public class MakePayment extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField paymentMethod;
    private JTextField paymentAmount;
    private JLabel balanceRemaining;
    private JLabel balanceAfterPayment;
    private double payAmt;

    public MakePayment(final Order order) {

        balanceRemaining.setText(String.valueOf(order.getBalanceRemaining()));
        balanceAfterPayment.setText(balanceRemaining.getText());

        paymentAmount.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                payAmt = Double.valueOf(paymentAmount.getText());
            }
        });

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Payment.createNewPayment(order, payAmt, order.getBalanceRemaining() - payAmt, paymentMethod.getText());
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
