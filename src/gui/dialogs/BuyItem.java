package gui.dialogs;

import models.db.Customer;
import models.db.Item;
import utils.Connection;

import javax.swing.*;
import java.awt.event.*;
import java.text.NumberFormat;

public class BuyItem extends JDialog {
    private JPanel contentPane;
    private JButton addToCartButton;
    private JButton buttonCancel;
    private JLabel buyLabel;
    private JLabel descriptionLabel;
    private JLabel pricePerUnit;
    private JLabel category;
    private JComboBox purchaseQuantityBox;
    private JLabel itemQuantity;
    private JLabel totalOrderCost;
    private Item itemBuying;
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();

    public BuyItem(Item item) {
        itemBuying = item;

        buyLabel.setText(itemBuying.getItemName());
        descriptionLabel.setText("<html><body style='width:150px;'>"+itemBuying.getDescription());
        pricePerUnit.setText(currencyFormat.format(itemBuying.getPrice()));
        category.setText(itemBuying.getCategoryType());

        itemQuantity.setText(String.valueOf(itemBuying.getQuantity()));
        totalOrderCost.setText(currencyFormat.format(0));

        final Object[] quantitiesAvailable = new Integer[itemBuying.getQuantity() + 1];
        for (int i=0; i<=itemBuying.getQuantity(); i++){
            quantitiesAvailable[i] = i;
        }

        DefaultComboBoxModel quantityModel = new DefaultComboBoxModel(quantitiesAvailable);
        purchaseQuantityBox.setModel(quantityModel);

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(addToCartButton);

        addToCartButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Integer quantity = (Integer) purchaseQuantityBox.getSelectedItem();
                if (quantity > 0) {
                    Customer currentCustomer = Connection.getLoggedInCustomer();
                    currentCustomer.addItemToCart(itemBuying, quantity);
                    onOK();
                } else {
                    JOptionPane.showMessageDialog(JOptionPane.getFrameForComponent(purchaseQuantityBox), "Must select the quantity.", "Error adding to cart", JOptionPane.ERROR_MESSAGE);
                }
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

        purchaseQuantityBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                totalOrderCost.setText(currencyFormat.format(itemBuying.getPrice() * ((Integer) purchaseQuantityBox.getSelectedItem())));
            }
        });
    }

    private void onOK() {
        dispose();
    }

    private void onCancel() {
        dispose();
    }
}
