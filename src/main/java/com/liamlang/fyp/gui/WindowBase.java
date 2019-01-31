package com.liamlang.fyp.gui;

import java.awt.Insets;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

public class WindowBase {

    private final String title;

    private JFrame frame;

    private JPanel panel;

    public WindowBase(String title) {
        this.title = title;
    }

    public JPanel getPanel() {

        if (panel != null) {
            return panel;
        }

        frame = new JFrame(title);
        JFrame.setDefaultLookAndFeelDecorated(true);

        panel = new JPanel();
        BoxLayout boxLayout = new BoxLayout(panel, BoxLayout.Y_AXIS);
        panel.setLayout(boxLayout);
        panel.setBorder(new EmptyBorder(new Insets(20, 20, 20, 20)));

        frame.add(panel);

        return panel;
    }

    public void show(int width) {

        frame.pack();
        frame.setVisible(true);
        frame.setLocation(300, 300);

        frame.setSize(width, frame.getHeight());

        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception e) {
        }

        SwingUtilities.updateComponentTreeUI(frame);
    }

    public void refresh() {

        frame.pack();
        SwingUtilities.updateComponentTreeUI(frame);
    }

    public void close() {

        frame.setVisible(false);
        frame.dispose();
    }

    public static void addSelectableTextField(JPanel panel, String text) {

        JTextField textField = new JTextField(text);
        textField.setEditable(false);
        panel.add(textField);
    }
}
