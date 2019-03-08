package com.liamlang.fyp.gui;

import com.liamlang.fyp.Utils.Utils;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import javax.swing.JButton;
import javax.swing.JTextField;

public class ChooseMyIpWindow {

    public interface ChooseIpCallback {

        public void callback(String ip, int port);
    }

    private ChooseIpCallback callback;

    public ChooseMyIpWindow(ChooseIpCallback callback) {
        this.callback = callback;
    }

    public void show() {

        WindowBase window = new WindowBase("Network Settings", 600);
        window.init();

        window.addImage("resources/connections.png");

        window.addVerticalSpace(20);

        window.addLabel("Choose port:");

        JTextField portTextField = new JTextField();

        window.add(portTextField);

        portTextField.setText("12345");

        window.addVerticalSpace(20);

        window.addLabel("Choose the IP address other nodes should use to contact you:");

        window.addVerticalSpace(10);

        ArrayList<String> possibleIps = getPossibleIps();

        if (possibleIps != null && possibleIps.size() > 0) {

            for (String ip : possibleIps) {

                JButton button = new JButton(ip);

                button.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {

                        if (portTextField.getText() == null || portTextField.getText().equals("")) {
                            return;
                        }

                        int port = 0;

                        try {

                            port = Integer.parseInt(portTextField.getText());
                            if (port < 0 || port > 65535) {
                                throw new Exception();
                            }

                            callback.callback(ip, port);
                            window.close();
                            
                        } catch (Exception ex) {

                            Utils.showOkPopup("Check port number!");
                            return;
                        }

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
