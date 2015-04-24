package gui.dialogs;

import models.db.Item;

import javax.swing.*;
import java.awt.event.*;
import java.text.NumberFormat;

public class ItemDetail extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JLabel itemHeader;
    private JLabel descriptionLabel;
    private JLabel categoryLabel;
    private JLabel quantityLabel;
    private JLabel priceLabel;
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();

    public ItemDetail(Item item) {

        itemHeader.setText(item.getItemName());
        descriptionLabel.setText(item.getDescription());
        categoryLabel.setText(item.getCategoryType());
        quantityLabel.setText(String.valueOf(item.getQuantity()));
        priceLabel.setText(currencyFormat.format(item.getPrice()));

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
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
