package com.liamlang.fyp.gui;

import java.awt.Insets;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

public class WindowBase {
    
    private final String title;
    
    private JFrame frame;
    
    public WindowBase(String title) {
        this.title = title;
    }
    
    public JPanel getPanel() {
        
        frame = new JFrame(title);
        JFrame.setDefaultLookAndFeelDecorated(true);

        JPanel panel = new JPanel();
        BoxLayout boxLayout = new BoxLayout(panel, BoxLayout.Y_AXIS);
        panel.setLayout(boxLayout);
        panel.setBorder(new EmptyBorder(new Insets(20, 20, 20, 20)));
        
        frame.add(panel);
        
        return panel;
    }
    
    public void show() {
        
        frame.pack();
        frame.setVisible(true);
        frame.setLocation(300, 300);

        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception e) {
        }
        SwingUtilities.updateComponentTreeUI(frame);
    }
}
