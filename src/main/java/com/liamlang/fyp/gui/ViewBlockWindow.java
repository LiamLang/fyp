package com.liamlang.fyp.gui;

import com.liamlang.fyp.Model.Block;
import com.liamlang.fyp.Utils.Utils;
import java.awt.Dimension;
import javax.swing.Box;
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

        panel.add(new JLabel("Hash: " + block.getHash()));

        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        panel.add(new JLabel("Previous hash: " + block.getPreviousHash()));
        
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        panel.add(new JLabel("Height: " + Integer.toString(block.getHeight())));
        
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        // transactions
        
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        panel.add(new JLabel("Timestamp: " + Utils.toHumanReadableTime(block.getTimestamp())));
        
        window.show(600);
    }
}
