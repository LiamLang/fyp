package com.liamlang.fyp.gui;

import com.liamlang.fyp.Model.Block;
import com.liamlang.fyp.Model.Blockchain;
import com.liamlang.fyp.Utils.Utils;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;

public class ViewBlockchainWindow {

    private WindowBase window;

    private final Blockchain blockchain;

    public ViewBlockchainWindow(Blockchain blockchain) {
        this.blockchain = blockchain;
    }

    public void show() {

        window = new WindowBase("View Blockchain");
        window.init();

        updateWindow();

        window.show(600);

        Utils.scheduleRepeatingTask(1000, new Runnable() {
            @Override
            public void run() {

                updateWindow();

                window.refresh();
            }
        });
    }

    public void updateWindow() {

        window.removeAll();

        window.addImage("src/main/resources/blockchain.png");

        window.addVerticalSpace(20);
        
        window.addLabel("Height: " + blockchain.getHeight());

        window.addVerticalSpace(20);

        window.addLabel("Hash of Latest Block: " + blockchain.getTop().getHash());

        window.addVerticalSpace(20);

        window.addLabel("Timestamp of Latest Block: " + Utils.toHumanReadableTime(blockchain.getTop().getTimestamp()));

        window.addVerticalSpace(20);

        window.addLabel("Last 20 blocks:");

        window.addVerticalSpace(10);

        if (blockchain.getHeight() != 0) {

            for (int i = blockchain.getHeight(); i > blockchain.getHeight() - 20; i--) {

                if (i < 1) {
                    break;
                }

                Block block = blockchain.getAtHeight(i);

                JButton button = new JButton("Block " + Integer.toString(block.getHeight()) + " - " + block.getHash());

                button.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {

                        ViewBlockWindow win = new ViewBlockWindow(block);
                        win.show();
                    }
                });

                window.add(button);
            }
        }
    }
}