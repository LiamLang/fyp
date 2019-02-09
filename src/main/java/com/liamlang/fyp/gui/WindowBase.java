package com.liamlang.fyp.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

public class WindowBase {

    private final String title;
    private final int width;

    private JFrame frame;

    private JPanel panel;

    public WindowBase(String title, int width) {
        this.title = title;
        this.width = width;
    }

    public void init() {

        if (panel != null) {
            return;
        }

        frame = new JFrame(title);
        JFrame.setDefaultLookAndFeelDecorated(true);

        panel = new JPanel();
        BoxLayout boxLayout = new BoxLayout(panel, BoxLayout.Y_AXIS);
        panel.setLayout(boxLayout);
        panel.setBorder(new EmptyBorder(new Insets(20, 20, 20, 20)));

        frame.add(panel);
    }

    public void show() {

        frame.pack();
        frame.setVisible(true);
        frame.setLocation(300, 300);

        frame.setSize(width, frame.getHeight());
        frame.setMinimumSize(frame.getSize());

        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception e) {
        }

        SwingUtilities.updateComponentTreeUI(frame);
    }

    public void removeAll() {

        if (panel == null) {
            return;
        }

        panel.removeAll();
    }

    public void refresh() {

        frame.pack();
        SwingUtilities.updateComponentTreeUI(frame);
    }

    public void close() {

        frame.setVisible(false);
        frame.dispose();
    }

    public void addSelectableTextField(String text) {

        if (panel == null) {
            return;
        }

        JTextField textField = new JTextField(text);
        textField.setEditable(false);
        panel.add(textField);
    }

    public void addVerticalSpace(int px) {

        if (panel == null) {
            return;
        }

        panel.add(Box.createRigidArea(new Dimension(0, px)));
    }

    public void addLabel(String text) {

        if (panel == null) {
            return;
        }

        panel.add(new JLabel(text));
    }

    public void add(Component component) {

        if (panel == null) {
            return;
        }

        panel.add(component);
    }

    public void addImage(String path) {

        if (panel == null) {
            return;
        }

        try {
            panel.add(new JLabel(new ImageIcon(ImageIO.read(new File(path)))));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
