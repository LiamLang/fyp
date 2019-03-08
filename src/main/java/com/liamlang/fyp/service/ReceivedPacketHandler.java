package com.liamlang.fyp.service;

import com.liamlang.fyp.Model.Block;
import com.liamlang.fyp.Model.Component;
import com.liamlang.fyp.Model.ConnectedNode;
import com.liamlang.fyp.Model.EncryptedMessage;
import com.liamlang.fyp.Model.OwnershipChangeSignature;
import com.liamlang.fyp.Model.SignedMessage;
import com.liamlang.fyp.Model.Transaction;
import com.liamlang.fyp.Utils.HashUtils;
import com.liamlang.fyp.Utils.NetworkUtils;
import com.liamlang.fyp.Utils.Utils;
import com.liamlang.fyp.gui.ViewComponentWindow;
import com.liamlang.fyp.service.Node.NodeType;
import java.io.Serializable;
import java.net.InetAddress;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Collections;

public class ReceivedPacketHandler implements Serializable {

    private Node node;

    public ReceivedPacketHandler(Node node) {
        this.node = node;
    }

    public void onPacketReceived(byte[] bytes) {

        if (bytes == null || bytes.length == 0) {
            return;
        }

        SignedMessage message = null;

        Object object = Utils.deserialize(bytes);

        if (object == null) {
            return;
        }

        if (object instanceof SignedMessage) {

            message = (SignedMessage) object;

        } else if (object instanceof EncryptedMessage) {

            EncryptedMessage encryptedMessage = (EncryptedMessage) object;

            try {
                message = (SignedMessage) Utils.deserialize(encryptedMessage.decrypt(node.getEcKeyPair().getPrivate()));

            } catch (Exception ex) {
                System.out.println("Error decrypting message!");
                return;
            }
        }

        if (message == null) {
            System.out.println("Failed to understand received message!");
            return;
        }

        if (!message.verify()) {
            System.out.println("Failed to verify the signature of the received message!");
            return;
        }

        if (!node.keyIsTrusted(message.getPublicKey(), message.getSignee())) {
            System.out.println("We do not trust the public key that has signed this message!");
            return;
        }

        String messageStr = message.getMessage();
        if (messageStr.equals("")) {
            return;
        }
        String[] parts = messageStr.split(" ");

        System.out.println("Received: " + messageStr + "\n");

        if (parts[0].equals("SYNC") && parts.length >= 8) {

            // Recombine parts of the serialized ec public key which may be split up because there happen to be spaces present
            String keyStr = "";
            for (int i = 7; i < parts.length; i++) {
                keyStr += parts[i] + " ";
            }
            keyStr = keyStr.substring(0, keyStr.length() - 1);

            onSyncPacketReceived(parts[1], parts[2], parts[3], parts[4], parts[5], parts[6], keyStr);

        } else if (parts[0].equals("BLOCK") && parts.length >= 3) {

            // Recombine parts of the serialized block which may be split up because there happen to be spaces present
            String blockStr = "";
            for (int i = 2; i < parts.length; i++) {
                blockStr += parts[i] + " ";
            }
            blockStr = blockStr.substring(0, blockStr.length() - 1);

            onBlockPacketReceived(parts[1], blockStr);

        } else if (parts[0].equals("CONNECTIONS") && parts.length >= 2) {

            // Recombine parts of the serialized connections object which may be split up because there happen to be spaces present
            String connectionsStr = "";
            for (int i = 1; i < parts.length; i++) {
                connectionsStr += parts[i] + " ";
            }
            connectionsStr = connectionsStr.substring(0, connectionsStr.length() - 1);

            onConnectionsPacketReceived(connectionsStr);

        } else if (parts[0].equals("UNCONFIRMED_TRANSACTION_SET") && parts.length >= 2) {

            // Recombine parts of the serialized unconfirmed transactions set object which may be split up because there happen to be spaces present
            String transactionsStr = "";
            for (int i = 1; i < parts.length; i++) {
                transactionsStr += parts[i] + " ";
            }
            transactionsStr = transactionsStr.substring(0, transactionsStr.length() - 1);

            onTransactionsPacketReceived(transactionsStr);

        } else if (parts[0].equals("COMPONENT_HASH_REQUEST") && parts.length == 4) {

            onComponentHashRequestReceived(parts[1], parts[2], parts[3]);

        } else if (parts[0].equals("COMPONENT_INFO_REQUEST") && parts.length >= 4) {

            // Recombine parts of the component info String which may be split up because there happen to be spaces present
            String infoStr = "";
            for (int i = 3; i < parts.length; i++) {
                infoStr += parts[i] + " ";
            }
            infoStr = infoStr.substring(0, infoStr.length() - 1);

            onComponentInfoRequestReceived(parts[1], parts[2], infoStr);

        } else if (parts[0].equals("SHOW_COMPONENT_REQUEST") && parts.length >= 3) {

            // Recombine parts of the serialized component object which may be split up because there happen to be spaces present
            String componentStr = "";
            for (int i = 2; i < parts.length; i++) {
                componentStr += parts[i] + " ";
            }
            componentStr = componentStr.substring(0, componentStr.length() - 1);

            onShowComponentRequestReceived(parts[1], componentStr);

        } else if (parts[0].equals("CREATE_COMPONENT_TRANSACTION_REQUEST") && parts.length >= 5) {

            // Recombine parts of the serialized public key object which may be split up because there happen to be spaces present
            String pubKeyStr = "";
            for (int i = 4; i < parts.length; i++) {
                pubKeyStr += parts[i] + " ";
            }
            pubKeyStr = pubKeyStr.substring(0, pubKeyStr.length() - 1);

            onCreateComponentTransactionRequestReceived(parts[1], parts[2], parts[3], pubKeyStr);

        } else if (parts[0].equals("ASSEMBLE_COMPONENTS_TRANSACTION_REQUEST") && parts.length == 3) {

            onAssembleComponentsTransactionRequestReceived(parts[1], parts[2]);

        } else if (parts[0].equals("DISASSEMBLE_COMPONENTS_TRANSACTION_REQUEST") && parts.length == 3) {

            onDisassembleComponentsTransactionRequestReceived(parts[1], parts[2]);

        } else if (parts[0].equals("CHANGE_OWNERSHIP_TRANSACTION_REQUEST") && parts.length >= 4) {

            // Recombine parts of the serialized signature object which may be split up because there happen to be spaces present
            String sigStr = "";
            for (int i = 3; i < parts.length; i++) {
                sigStr += parts[i] + " ";
            }

            onChangeOwnershipTransactionRequestReceived(parts[1], parts[2], sigStr);
        }
    }

    private void onSyncPacketReceived(String ip, String portStr, String heightStr, String numConnections, String unconfirmedTransactionSetHash, String isSupernodeStr, String ecPubKeyString) {
        try {

            InetAddress inetAddress = NetworkUtils.toIp(ip);
            int port = Integer.parseInt(portStr);
            PublicKey ecPubKey = (PublicKey) Utils.deserialize(Utils.toByteArray(ecPubKeyString));
            ConnectedNode newConnection = new ConnectedNode(inetAddress, port, ecPubKey);

            boolean isSupernode = isSupernodeStr.equals("super");

            if (node.getNodeType() == NodeType.LIGHTWEIGHT) {
                if (isSupernode) {
                    node.addConnection(newConnection);
                }
            } else {
                node.addConnection(newConnection);
            }

            int height = Integer.parseInt(heightStr);
            if (height < node.getBlockchain().getHeight()) {
                node.getPacketSender().sendBlocks(newConnection, height, node.getBlockchain().getHeight());
            }
            if (Integer.parseInt(numConnections) < node.getConnections().size()) {
                node.getPacketSender().sendConnections(newConnection);
            }
            if (!unconfirmedTransactionSetHash.equals(Utils.toHexString(HashUtils.sha256(Utils.serialize(node.getUnconfirmedTransactionSet()))))) {
                node.getPacketSender().sendTransactions(newConnection);
            }
        } catch (Exception ex) {
            System.out.println("Exception in ReceivedPacketHandler.onSyncPacketReceived");
        }
    }

    private void onBlockPacketReceived(String heightStr, String blockStr) {

        if (node.getNodeType() == NodeType.LIGHTWEIGHT) {
            return;
        }

        try {
            int height = Integer.parseInt(heightStr);

            if (height == node.getBlockchain().getHeight() + 1) {
                Block block = (Block) Utils.deserialize(Utils.toByteArray(blockStr));

                if (node.getBlockchain().addToTop(block, node)) {

                    for (Transaction t : block.getData().getTransactions()) {

                        node.getUnconfirmedTransactionSet().remove(t);
                    }
                }
                node.saveSelf();
            }

        } catch (Exception ex) {
            System.out.println("Exception in ReceivedPacketHandler.onBlockPacketReceived");
        }
    }

    private void onConnectionsPacketReceived(String otherConnectionsStr) {
        try {
            ArrayList<ConnectedNode> otherConnections = (ArrayList<ConnectedNode>) Utils.deserialize(Utils.toByteArray(otherConnectionsStr));
            for (ConnectedNode connection : otherConnections) {

                if (!node.getConnections().contains(connection) && !connection.getIp().getHostAddress().equals(node.getMyIp())) {

                    node.getConnections().add(connection);
                }
            }
            node.saveSelf();

        } catch (Exception ex) {
            System.out.println("Exception in ReceivedPacketHandler.onConnectionsPacketRecevied");
        }
    }

    private void onTransactionsPacketReceived(String otherUnconfirmedTransactionSetStr) {
        try {
            ArrayList<Transaction> otherUnconfirmedTransactionSet = (ArrayList<Transaction>) Utils.deserialize(Utils.toByteArray(otherUnconfirmedTransactionSetStr));
            for (Transaction t : otherUnconfirmedTransactionSet) {

                boolean matchFound = false;

                for (Transaction existing : node.getUnconfirmedTransactionSet()) {
                    if (existing.equals(t)) {
                        matchFound = true;
                    }
                }

                if (!matchFound && !node.getBlockchain().isConfirmed(t) && node.verifyTransaction(t, false)) {

                    node.getUnconfirmedTransactionSet().add(t);

                    Collections.sort(node.getUnconfirmedTransactionSet());
                }
            }
            node.saveSelf();

        } catch (Exception ex) {
            System.out.println("Exception in ReceivedPacketHandler.onTransactionsPacketRecevied");
        }
    }

    private void onComponentHashRequestReceived(String ip, String portStr, String hash) {

        if (node.getNodeType() != NodeType.SUPERNODE) {
            return;
        }

        if (hash.equals("")) {
            return;
        }

        try {

            for (Component component : node.getUnspentComponents()) {

                if (component.getHash().equals(hash)) {

                    String confirmationStatus = supernodeGetComponentConfirmationStatus(component);

                    node.getPacketSender().sendShowComponentRequest(ip, Integer.parseInt(portStr), component, confirmationStatus);
                }
            }
        } catch (Exception ex) {
            System.out.println("Exception in ReceivedPacketHandler.onComponentHashRequestReceived");
        }
    }

    private void onComponentInfoRequestReceived(String ip, String portStr, String info) {

        if (node.getNodeType() != NodeType.SUPERNODE) {
            return;
        }

        if (info.equals("")) {
            return;
        }

        try {

            for (Component component : node.getUnspentComponents()) {

                // Inefficient, but it'll do for this proof of concept
                if (component.getInfo().toString().contains(info)) {

                    String confirmationStatus = supernodeGetComponentConfirmationStatus(component);

                    node.getPacketSender().sendShowComponentRequest(ip, Integer.parseInt(portStr), component, confirmationStatus);
                }
            }
        } catch (Exception ex) {
            System.out.println("Exception in ReceivedPacketHandler.onComponentInfoRequestReceived");
        }
    }

    private void onShowComponentRequestReceived(String confirmationStatus, String componentStr) {

        if (node.getNodeType() != NodeType.LIGHTWEIGHT) {
            return;
        }

        try {

            Component component = (Component) Utils.deserialize(Utils.toByteArray(componentStr));

            ViewComponentWindow win = new ViewComponentWindow(component, node, confirmationStatus);
            win.show();

        } catch (Exception ex) {
            System.out.println("Exception in ReceivedPacketHandler.onShowComponentRequestReceived");
        }
    }

    private void onCreateComponentTransactionRequestReceived(String info, String quantityStr, String ownerName, String pubKeyStr) {

        if (node.getNodeType() != NodeType.SUPERNODE) {
            return;
        }

        try {

            info = info.replace("_", " ");
            long quantity = Long.parseLong(quantityStr);
            ownerName = ownerName.replace("_", " ");
            PublicKey pubKey = (PublicKey) Utils.deserialize(Utils.toByteArray(pubKeyStr));

            Transaction transaction = node.getTransactionBuilder().buildNewComponentTransaction(info, quantity, ownerName, pubKey);

            if (node.verifyTransaction(transaction, false)) {
                node.broadcastTransaction(transaction);
                System.out.println("Broadcasting valid transaction created on request from connected light node!");
            } else {
                System.out.println("Transaction created on request from connected light node was INVALID!");
            }

        } catch (Exception ex) {
            System.out.println("Exception caught processing create component transaction request");
        }
    }

    private void onAssembleComponentsTransactionRequestReceived(String parentHash, String childHash) {

        if (node.getNodeType() != NodeType.SUPERNODE) {
            return;
        }

        try {

            Component parentComponent = null;
            Component childComponent = null;

            for (Component component : node.getUnspentComponents()) {

                if (component.getHash().equals(parentHash)) {
                    parentComponent = component;
                } else if (component.getHash().equals(childHash)) {
                    childComponent = component;
                }

                if (parentComponent != null && childComponent != null) {
                    break;
                }
            }

            if (parentComponent == null || childComponent == null) {
                return;
            }

            ArrayList<Component> children = new ArrayList<>();
            children.add(childComponent);

            Transaction transaction = node.getTransactionBuilder().addComponetsToOther(parentComponent, children);

            if (node.verifyTransaction(transaction, false)) {
                node.broadcastTransaction(transaction);
                System.out.println("Broadcasting valid transaction created on request from connected light node!");
            } else {
                System.out.println("Transaction created on request from connected light node was INVALID!");
            }

        } catch (Exception ex) {
            System.out.println("Exception caught processing assemble components transaction request");
        }
    }

    private void onDisassembleComponentsTransactionRequestReceived(String parentHash, String childHash) {

        if (node.getNodeType() != NodeType.SUPERNODE) {
            return;
        }

        try {

            Component parentComponent = null;

            for (Component component : node.getUnspentComponents()) {

                if (component.getHash().equals(parentHash)) {
                    parentComponent = component;
                    break;
                }
            }

            if (parentComponent == null) {
                return;
            }

            Component childComponent = null;

            for (Component subcomponent : parentComponent.getSubcomponents()) {

                if (subcomponent.getHash().equals(childHash)) {
                    childComponent = subcomponent;
                    break;
                }
            }

            if (childComponent == null) {
                return;
            }

            ArrayList<Component> children = new ArrayList<>();
            children.add(childComponent);

            Transaction transaction = node.getTransactionBuilder().removeComponentsFromOther(parentComponent, children);

            if (node.verifyTransaction(transaction, false)) {
                node.broadcastTransaction(transaction);
                System.out.println("Broadcasting valid transaction created on request from connected light node!");
            } else {
                System.out.println("Transaction created on request from connected light node was INVALID!");
            }

        } catch (Exception ex) {
            System.out.println("Exception caught processing disassemble components transaction request");
        }
    }

    private void onChangeOwnershipTransactionRequestReceived(String hash, String newOwner, String sigStr) {

        if (node.getNodeType() != NodeType.SUPERNODE) {
            return;
        }

        try {
            newOwner = newOwner.replace("_", " ");
            OwnershipChangeSignature signature = (OwnershipChangeSignature) Utils.deserialize(Utils.toByteArray(sigStr));

            Component oldComponent = null;

            for (Component component : node.getUnspentComponents()) {

                if (component.getHash().equals(hash)) {
                    oldComponent = component;
                    break;
                }
            }

            if (oldComponent == null) {
                return;
            }

            Transaction transaction = node.getTransactionBuilder().changeOwner(oldComponent, newOwner, signature);

            if (node.verifyTransaction(transaction, false)) {
                node.broadcastTransaction(transaction);
                System.out.println("Broadcasting valid transaction created on request from connected light node!");
            } else {
                System.out.println("Transaction created on request from connected light node was INVALID!");
            }

        } catch (Exception ex) {
            System.out.println("Exception caught processing ownership change transaction request");
        }
    }

    private String supernodeGetComponentConfirmationStatus(Component component) {

        Transaction confirmingTx = node.getBlockchain().getTransactionConfirmingComponent(component);
        boolean isUnspent = node.isUnspent(component);

        String res;

        if (confirmingTx == null) {
            res = "UNCONFIRMED";
        } else {
            if (isUnspent) {
                res = "Unspent";
            } else {
                res = "SPENT";
            }
            String time = Utils.toHumanReadableTime(confirmingTx.getTimestamp());
            time = time.replace(" ", "_");
            res = res + "_-_Confirmed_at:_" + time;
        }

        return res;
    }
}
