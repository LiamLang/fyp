package com.liamlang.fyp.gui;

import com.liamlang.fyp.Model.Block;
import com.liamlang.fyp.Model.Transaction;
import com.liamlang.fyp.Utils.Utils;
import com.liamlang.fyp.service.Node;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;

public class ViewBlockWindow {

    private final Block block;
    private final Node node;

    public ViewBlockWindow(Block block, Node node) {
        this.block = block;
        this.node = node;
    }

    public void show() {

        WindowBase window = new WindowBase("View Block", 600);
        window.init();
        
        window.addImage("src/main/resources/block.png");

        window.addVerticalSpace(20);

        window.addSelectableTextField("Hash: " + block.getHash());

        window.addVerticalSpace(20);

        window.addSelectableTextField("Previous hash: " + block.getPreviousHash());

        window.addVerticalSpace(20);

        window.addLabel("Height: " + Integer.toString(block.getHeight()));

        window.addVerticalSpace(20);

        window.addLabel("Transactions: " + Integer.toString(block.getData().getTransactions().size()));

        window.addVerticalSpace(10);

        for (Transaction transaction : block.getData().getTransactions()) {

            JButton button = new JButton(Utils.toHumanReadableTime(transaction.getTimestamp()));

            button.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {

                    ViewTransactionWindow win = new ViewTransactionWindow(transaction, node);
                    win.show();
                }
            });

            window.add(button);
        }

        window.addVerticalSpace(20);

        window.addLabel("Timestamp: " + Utils.toHumanReadableTime(block.getTimestamp()));

        window.show();
    }
}
