package com.liamlang.fyp.service;

import com.liamlang.fyp.Model.Block;
import com.liamlang.fyp.Model.Blockchain;
import com.liamlang.fyp.Utils.Utils;
import com.liamlang.fyp.gui.ViewBlockWindow;
import com.liamlang.fyp.gui.WindowBase;
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

public class ViewBlockchainWindow {

    private WindowBase window;

    private final Blockchain blockchain;

    public ViewBlockchainWindow(Blockchain blockchain) {
        this.blockchain = blockchain;
    }

    public void show() {

        window = new WindowBase("View Blockchain");

        updatePanel();

        window.show(600);

        Utils.scheduleRepeatingTask(1000, new Runnable() {
            @Override
            public void run() {

                updatePanel();

                window.refresh();
            }
        });
    }

    public void updatePanel() {

        JPanel panel = window.getPanel();

        panel.removeAll();

        try {
            panel.add(new JLabel(new ImageIcon(ImageIO.read(new File("src/main/resources/blockchain.png")))));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        panel.add(new JLabel("Height: " + blockchain.getHeight()));

        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        panel.add(new JLabel("Hash of Latest Block:" + blockchain.getTop().getHash()));

        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        panel.add(new JLabel("Timestamp of Latest Block: " + Utils.toHumanReadableTime(blockchain.getTop().getTimestamp())));

        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        panel.add(new JLabel("Last 20 blocks:"));

        panel.add(Box.createRigidArea(new Dimension(0, 10)));

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

                panel.add(button);
            }
        }
    }
}
