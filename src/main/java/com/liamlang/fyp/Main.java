package com.liamlang.fyp;

import com.liamlang.fyp.Utils.EncryptionUtils;
import com.liamlang.fyp.Utils.Utils;
import com.liamlang.fyp.gui.NodeLoaderWindow;
import java.security.KeyPair;

public class Main {

    public static void main(String[] args) {
        
        /*
        try {
                        
            NodeLoaderWindow win = new NodeLoaderWindow();
            win.show();
            
        } catch (Exception ex) {
            
            System.out.println("Exception caught in Main:");
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
*/
        
        KeyPair myKeyPair = EncryptionUtils.generateEcKeyPair();
        
        String myString = "Hello, world!";
        
        byte[] myBytes = Utils.toByteArray(myString);
        
        byte[] ciphertext = EncryptionUtils.encrypt(myBytes, myKeyPair.getPublic());
        
        byte[] plaintext = EncryptionUtils.decrypt(ciphertext, myKeyPair.getPrivate());
        
        String myPlainString = Utils.toString(plaintext);
        
        System.out.println(myPlainString);
    }
}
