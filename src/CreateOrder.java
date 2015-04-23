import models.*;
import utils.Connection;

import javax.swing.*;
import java.awt.event.*;
import java.text.NumberFormat;
import java.util.List;
import java.util.Map;

public class CreateOrder extends JDialog {
    private JPanel contentPane;
    private JButton placeOrder;
    private JButton buttonCancel;
    private JList itemReceiptList;
    private JLabel subtotalLabel;
    private JLabel taxesLabel;
    private JLabel totalPriceLabel;
    private JComboBox shippingAddress;
    private JComboBox billingAddress;
    private List<Map> orderItems;
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
    private Customer loggedInCustomer;
    private List<Address> shippingAddressList;
    private List<Address> billingAddressList;

    public CreateOrder(List<Map> items) {
        orderItems = items;
        DefaultListModel<String> listModel = new DefaultListModel<String>();
        itemReceiptList.setModel(listModel);

        loggedInCustomer = Connection.getLoggedInCustomer();

        shippingAddressList = loggedInCustomer.getShippingAddresses();
        billingAddressList = loggedInCustomer.getBillingAddresses();

        DefaultComboBoxModel shippingModel = new DefaultComboBoxModel();
        for (int i = 0; i < shippingAddressList.size(); i++) {
            shippingModel.addElement(shippingAddressList.get(i).getAddressLine());
        }
        shippingAddress.setModel(shippingModel);

        DefaultComboBoxModel billingModel = new DefaultComboBoxModel();
        for (int i = 0; i < billingAddressList.size(); i++) {
            billingModel.addElement(billingAddressList.get(i).getAddressLine());
        }
        billingAddress.setModel(billingModel);

        double subtotal = 0.0;

        for (int i = 0; i < orderItems.size(); i++) {
            // get the map for each item that contains the item object
            // and the quantity to buy of the item
            Map itemMap = items.get(i);

            Item item = (Item) itemMap.get("item");
            Integer quantity = (Integer) itemMap.get("quantity");
            double itemPrice = item.getPrice();
            double itemCost = itemPrice * quantity;

            subtotal += itemCost;

            listModel.addElement(item.getItemName() + " x " + quantity + "\t" + currencyFormat.format(itemCost));
        }
        final double subtotalAmt = subtotal;
        final double taxAmount = subtotal * .04;
        final double totalPrice = subtotal + taxAmount;

        subtotalLabel.setText(currencyFormat.format(subtotal));
        taxesLabel.setText(currencyFormat.format(taxAmount));
        totalPriceLabel.setText(currencyFormat.format(totalPrice));

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(placeOrder);

        placeOrder.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int shipAddressIndex = shippingAddress.getSelectedIndex();
                int billAddressIndex = billingAddress.getSelectedIndex();

                Address shipAddress = shippingAddressList.get(shipAddressIndex);
                Address billAddress = billingAddressList.get(billAddressIndex);

                Order createdOrder = Order.createNewOrder(loggedInCustomer, subtotalAmt, taxAmount, totalPrice, shipAddress, billAddress);
                for(int i=0; i<orderItems.size(); i++){
                    Map itemMap = orderItems.get(i);

                    Item item = (Item) itemMap.get("item");
                    Integer quantity = (Integer) itemMap.get("quantity");

                    OrderLineItem.createOrderLine(createdOrder, item, quantity);
                }
                JOptionPane.showMessageDialog(JOptionPane.getFrameForComponent(placeOrder), "Order successfully placed. Thank You!", "Order Placed", JOptionPane.PLAIN_MESSAGE);
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
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }
}
