package gui.dialogs;

import models.db.Order;
import models.db.Payment;
import utils.Connection;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.*;
import java.text.NumberFormat;

public class MakePayment extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField paymentMethod;
    private JTextField paymentAmount;
    private JLabel balanceRemaining;
    private JLabel balanceAfterPayment;
    private double payAmt;
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();


    public MakePayment(final Order order) {

        balanceRemaining.setText(currencyFormat.format(order.getBalanceRemaining()));
        balanceAfterPayment.setText(balanceRemaining.getText());

        paymentAmount.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                warn();
                updateAmount();
            }
            public void removeUpdate(DocumentEvent e) {
                warn();
                updateAmount();
            }
            public void insertUpdate(DocumentEvent e) {
                warn();
                updateAmount();
            }

            public void warn() {
                if(!paymentAmount.getText().equals("")) {
                    if (Double.parseDouble(paymentAmount.getText()) <= 0) {
                        JOptionPane.showMessageDialog(null,
                                "Error: Please enter number bigger than 0", "Error Massage",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
            public void updateAmount() {
                if (paymentAmount.getText().equals("")) {
                    payAmt = 0;
                }else {
                    payAmt = Double.valueOf(paymentAmount.getText());
                }
                double newBalance = order.getBalanceRemaining() - payAmt;
                balanceAfterPayment.setText(currencyFormat.format(newBalance));
            }
        });

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (payAmt <= 0) {
                    JOptionPane.showMessageDialog(null,
                            "Please enter payment amount bigger than 0.", "Error Making Payment",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
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
