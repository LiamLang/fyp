package com.liamlang.fyp.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import javax.swing.JButton;

public class ChooseMyIpWindow {

    public interface ChooseIpCallback {

        public void callback(String ip);
    }

    private ChooseIpCallback callback;

    public ChooseMyIpWindow(ChooseIpCallback callback) {
        this.callback = callback;
    }

    public void show() {

        WindowBase window = new WindowBase("Choose IP", 600);
        window.init();

        window.addImage("resources/connections.png");

        window.addVerticalSpace(20);

        window.addLabel("Choose the IP address at which you would like to be contacted:");

        window.addVerticalSpace(10);

        ArrayList<String> possibleIps = getPossibleIps();

        if (possibleIps != null && possibleIps.size() > 0) {

            for (String ip : possibleIps) {

                JButton button = new JButton(ip);

                button.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {

                        callback.callback(ip);
                        
                        window.close();
                    }
                });

                window.add(button);
            }
        }

        window.show();
    }

    private ArrayList<String> getPossibleIps() {

        ArrayList<String> res = new ArrayList<>();

        try {

            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();

            while (networkInterfaces.hasMoreElements()) {

                NetworkInterface networkInterface = networkInterfaces.nextElement();
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();

                while (inetAddresses.hasMoreElements()) {

                    InetAddress inetAddress = inetAddresses.nextElement();
                    res.add(inetAddress.getHostAddress());
                }
            }

        } catch (Exception ex) {

            System.out.println("Error getting available IP addresses!");
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }

        return res;
    }
}
