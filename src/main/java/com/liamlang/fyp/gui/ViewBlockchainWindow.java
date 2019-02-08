package com.liamlang.fyp.gui;

import com.liamlang.fyp.Model.Block;
import com.liamlang.fyp.Model.Blockchain;
import com.liamlang.fyp.Utils.Utils;
import com.liamlang.fyp.service.Node;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;

public class ViewBlockchainWindow {

    private WindowBase window;

    private final Blockchain blockchain;
    private final Node node;

    private String lastBlockHash;

    public ViewBlockchainWindow(Blockchain blockchain, Node node) {
        this.blockchain = blockchain;
        this.node = node;
        if (blockchain.getHeight() > 0) {
            lastBlockHash = blockchain.getTop().getHash();
        }
    }

    public void show() {

        window = new WindowBase("View Blockchain", 600);
        window.init();

        updateWindow();

        window.show();

        Utils.scheduleRepeatingTask(1000, new Runnable() {
            @Override
            public void run() {

                if (!lastBlockHash.equals(blockchain.getTop().getHash())) {

                    lastBlockHash = blockchain.getTop().getHash();

                    updateWindow();
                    window.refresh();
                }
            }
        });
    }

    public void updateWindow() {

        window.removeAll();

        window.addImage("src/main/resources/blockchain.png");

        window.addVerticalSpace(20);

        window.addLabel("Height: " + blockchain.getHeight());

        window.addVerticalSpace(20);

        if (blockchain.getHeight() > 0) {
            
            window.addLabel("Hash of Latest Block: " + blockchain.getTop().getHash());

            window.addVerticalSpace(20);

            window.addLabel("Timestamp of Latest Block: " + Utils.toHumanReadableTime(blockchain.getTop().getTimestamp()));

            window.addVerticalSpace(20);
        }

        window.addLabel("Last 20 blocks:");

        window.addVerticalSpace(10);

        if (blockchain.getHeight() != 0) {

            for (int i = blockchain.getHeight(); i > blockchain.getHeight() - 20; i--) {

                if (i < 1) {
                    break;
                }

                Block block = blockchain.getAtHeight(i);

                JButton button = new JButton("Block " + Integer.toString(block.getHeight()) + " - "
                        + Integer.toString(block.getData().getTransactions().size()) + " transactions - " + block.getHash());

                button.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {

                        ViewBlockWindow win = new ViewBlockWindow(block, node);
                        win.show();
                    }
                });

                window.add(button);
            }
        }

        if (!node.isCreatingBlocks()) {

            window.addVerticalSpace(20);

            JButton createBlocksButton = new JButton("Start Creating Blocks");

            createBlocksButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {

                    node.startCreatingBlocks();

                    updateWindow();
                    window.refresh();
                }
            });

            window.add(createBlocksButton);
        }
    }
}
