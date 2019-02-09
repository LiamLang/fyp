package com.liamlang.fyp.gui;

import com.liamlang.fyp.Utils.Utils;
import com.liamlang.fyp.service.Node;
import com.liamlang.fyp.service.NodeManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JTextField;

public class NodeLoaderWindow {
    
    public NodeLoaderWindow() {
    }
    
    public void show() {
        
        WindowBase window = new WindowBase("Entry Point", 500);
        window.init();
        
        window.addImage("src/main/resources/folder.png");
        
        window.addVerticalSpace(20);
        
        window.addLabel("Load Node from File:");
        
        window.addVerticalSpace(5);
        
        JTextField loadFileTextField = new JTextField();
        window.add(loadFileTextField);
        
        loadFileTextField.setText("node.txt");
        
        window.addVerticalSpace(5);
        
        JButton loadButton = new JButton("Load");
        
        loadButton.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                
                try {
                    
                    Node node = NodeManager.getSavedNode(loadFileTextField.getText());
                    
                    HomeWindow homeWindow = new HomeWindow(node);
                    homeWindow.show();
                    
                    window.close();
                    
                } catch (Exception ex) {
                    
                    Utils.showOkPopup("Error loading node:\n\n" + ex.getMessage());
                }
            }
        });
        
        window.add(loadButton);
        
        window.addVerticalSpace(20);
        
        window.addLabel("Create new Node:");
        
        window.addVerticalSpace(10);
        
        window.addLabel("Owner name:");
        
        JTextField ownerNameTextField = new JTextField();
        
        window.add(ownerNameTextField);
        
        window.addVerticalSpace(10);
        
        window.addLabel("Save to path:");
        
        JTextField savePathTextField = new JTextField();
        
        window.add(savePathTextField);
        
        savePathTextField.setText("node.txt");
        
        window.addVerticalSpace(10);
        
        JButton createNodeButton = new JButton("Create new Node for existing Blockchain");
        
        createNodeButton.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                
                if (ownerNameTextField.getText().equals("") || savePathTextField.getText().equals("")) {
                    return;
                }
                
                try {
                    
                    Node node = NodeManager.startNodeWithEmptyBlockchain(ownerNameTextField.getText(), savePathTextField.getText());
                    
                    HomeWindow homeWindow = new HomeWindow(node);
                    homeWindow.show();
                    
                    window.close();
                    
                } catch (Exception ex) {
                    
                    Utils.showOkPopup("Error creating node:\n\n" + ex.getMessage());
                }
                
            }
        });
        
        window.add(createNodeButton);
        
        JButton createNodeAndBlockchainButton = new JButton("Create new Node with new Blockchain");
        
        createNodeAndBlockchainButton.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                
                if (ownerNameTextField.getText().equals("") || savePathTextField.getText().equals("")) {
                    return;
                }
                
                try {
                    
                    Node node = NodeManager.startNodeWithFirstBlock(ownerNameTextField.getText(), savePathTextField.getText());
                    
                    HomeWindow homeWindow = new HomeWindow(node);
                    homeWindow.show();
                    
                    window.close();
                    
                } catch (Exception ex) {
                    
                    Utils.showOkPopup("Error creating node:\n\n" + ex.getMessage());
                }
                
            }
        });
        
        window.add(createNodeAndBlockchainButton);
        
        window.show();
    }
}