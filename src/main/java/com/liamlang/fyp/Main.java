package com.liamlang.fyp;

import com.liamlang.fyp.gui.NodeLoaderWindow;

public class Main {

    public static void main(String[] args) {

        try {

            for (int i = 0; i < 3; i++) {

                NodeLoaderWindow win = new NodeLoaderWindow();
                win.show();

            }

        } catch (Exception ex) {

            System.out.println("Exception caught in Main:");
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }
}
