package com.liamlang.fyp.gui;

import com.liamlang.fyp.Model.Block;
import com.liamlang.fyp.Model.Transaction;
import com.liamlang.fyp.Utils.Utils;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ViewBlockWindow {

    private final Block block;

    public ViewBlockWindow(Block block) {
        this.block = block;
    }

    public void show() {

        WindowBase window = new WindowBase("View Block");
        JPanel panel = window.getPanel();

        try {
            panel.add(new JLabel(new ImageIcon(ImageIO.read(new File("src/main/resources/block.png")))));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        panel.add(new JLabel("Hash: " + block.getHash()));

        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        panel.add(new JLabel("Previous hash: " + block.getPreviousHash()));

        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        panel.add(new JLabel("Height: " + Integer.toString(block.getHeight())));

        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        panel.add(new JLabel("Transactions: " + Integer.toString(block.getData().getTransactions().size())));
        
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        for (Transaction transaction : block.getData().getTransactions()) {
            
            JButton button = new JButton(Utils.toHumanReadableTime(transaction.getTimestamp()));

            button.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {

                    ViewTransactionWindow win = new ViewTransactionWindow(transaction);
                    win.show();
                }
            });

            panel.add(button);
        }
        
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        panel.add(new JLabel("Timestamp: " + Utils.toHumanReadableTime(block.getTimestamp())));

        window.show(600);
    }
}
