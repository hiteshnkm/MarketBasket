package gui.dialogs;

import models.db.Address;
import models.db.Order;
import models.db.Payment;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.util.ArrayList;

public class OrderDetail extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonClose;
    private JTable paymentsTable;
    private JLabel orderID;
    private JLabel orderDate;
    private JLabel shippingDate;
    private JLabel shipAddressLine;
    private JLabel shipCityLabel;
    private JLabel shipState;
    private JLabel shipZip;
    private JLabel shipCountry;
    private JLabel billingAddressLine;
    private JLabel billingCity;
    private JLabel billingState;
    private JLabel billingZip;
    private JLabel billingCountry;

    public OrderDetail(Order order) {

        orderID.setText(String.valueOf(order.getOrderid()));
        orderDate.setText(String.valueOf(order.getOrderDate()));
        shippingDate.setText(String.valueOf(order.getShipDate()));

        Address shipAddress = order.getShippingAddress();
        Address billingAddress = order.getBillingAddress();

        shipAddressLine.setText(shipAddress.getAddressLine());
        shipCityLabel.setText(shipAddress.getCity());
        shipState.setText(shipAddress.getState());
        shipZip.setText(String.valueOf(shipAddress.getZip()));
        shipCountry.setText(shipAddress.getCountry());

        billingAddressLine.setText(billingAddress.getAddressLine());
        billingCity.setText(billingAddress.getCity());
        billingState.setText(billingAddress.getState());
        billingZip.setText(String.valueOf(billingAddress.getZip()));
        billingCountry.setText(billingAddress.getCountry());

        ArrayList<Payment> payments = order.getPayments();
        Object[][] paymentData = new Object[payments.size()][3];

        for (int i = 0; i < payments.size(); i++) {
            Payment currentPayment = payments.get(i);
            Object[] paymentRow = new Object[3];
            for (int j = 0; j < paymentRow.length; j++) {
                Object paymentInfo;
                switch (j) {
                    case 0:
                        paymentInfo = currentPayment.getPaymentID();
                        break;
                    case 1:
                        paymentInfo = currentPayment.getPaymentDate();
                        break;
                    case 2:
                        paymentInfo = currentPayment.getPaymentAmount();
                        break;
                    default:
                        paymentInfo = "";
                        break;
                }
                paymentRow[j] = paymentInfo;
            }
            paymentData[i] = paymentRow;
        }

        String[] paymentColumnNames = {"Payment ID", "Payment Date", "Payment Amt"};

        DefaultTableModel paymentsTableModel = new DefaultTableModel(paymentData, paymentColumnNames);
        paymentsTable.setModel(paymentsTableModel);

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonClose.addActionListener(new ActionListener() {
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

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
