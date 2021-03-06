package com.liamlang.fyp.service;

import com.liamlang.fyp.Model.Component;
import com.liamlang.fyp.Model.OwnershipChangeSignature;
import com.liamlang.fyp.Model.Transaction;
import com.liamlang.fyp.service.Node.NodeType;
import java.io.Serializable;
import java.util.ArrayList;

public class TransactionVerifier implements Serializable {

    private Node node;

    public TransactionVerifier(Node node) {
        this.node = node;
    }

    public boolean verify(Transaction transaction, boolean commitResults) {

        if (node.getNodeType() == NodeType.LIGHTWEIGHT) {
            return false;
        }

        // Make sure the hashes of all output components verify
        for (Component component : transaction.getComponentsCreated()) {

            if (!component.verifyHash()) {
                return false;
            }
        }

        if (transaction.getInputHashes().isEmpty()) {

            // Transaction consists of newly created components
            for (Component component : transaction.getComponentsCreated()) {

                if (!component.getSubcomponents().isEmpty()) {
                    return false;
                }
            }

            if (commitResults) {
                for (Component component : transaction.getComponentsCreated()) {

                    node.getUnspentComponents().add(component);
                }
            }

            return true;
        }

        // From the list of input hashes, get a list of the existing unspent components
        ArrayList<Component> oldComponents = new ArrayList<>();

        for (String inputHash : transaction.getInputHashes()) {

            for (Component unspentComponent : node.getUnspentComponents()) {

                if (unspentComponent.getHash().equals(inputHash)) {

                    oldComponents.add(unspentComponent);
                }
            }
        }

        // If one component is being disassembled from another, the 'old component' is not unspent,
        // but is a subcomponent of the old parent component
        ArrayList<Component> oldComponents2 = new ArrayList<>();

        for (String inputHash : transaction.getInputHashes()) {

            for (Component oldComponent : oldComponents) {

                for (Component subcomponent : oldComponent.getSubcomponents()) {

                    if (subcomponent.getHash().equals(inputHash)) {

                        // Can't add to ArrayList oldComponents at same time as iterating over it
                        oldComponents2.add(subcomponent);
                    }
                }
            }
        }

        for (Component oldComponent : oldComponents2) {
            oldComponents.add(oldComponent);
        }

        if (!transaction.getOwnershipChangeSignatures().isEmpty()) {

            // This transaction is changing ownership
            // If there isn't one that exactly equals each newly created component, fail
            for (Component newComponent : transaction.getComponentsCreated()) {

                boolean inputAndSignatureValid = false;

                for (Component oldComponent : oldComponents) {

                    if (oldComponent.equalsExceptForOwnership(newComponent)) {

                        // Input component matched. Now check for a valid signature
                        for (OwnershipChangeSignature signature : transaction.getOwnershipChangeSignatures()) {

                            if (signature.verify(oldComponent, newComponent)) {

                                inputAndSignatureValid = true;
                            }
                        }
                    }
                }

                if (!inputAndSignatureValid) {
                    return false;
                }
            }

            if (commitResults) {

                // Remove old components from node's unspent component list
                for (Component oldComponent : oldComponents) {
                    node.getUnspentComponents().remove(oldComponent);
                }

                // Add newly created components to the node's unspent component list
                for (Component newComponent : transaction.getComponentsCreated()) {
                    node.getUnspentComponents().add(newComponent);
                }
            }

            return true;
        }

        // Check that transaction is the remaining type - involving assembly/disassembly of components
        // Every output must be the same as an input, with either a different quantity remaining, or set of subcomponents
        ArrayList<Component> componentsAssembled = new ArrayList<>();
        ArrayList<Component> componentsDisassembled = new ArrayList<>();

        ArrayList<Component> subcomponentsAdded = new ArrayList<>();
        ArrayList<Component> subcomponentsRemoved = new ArrayList<>();

        for (Component newComponent : transaction.getComponentsCreated()) {

            boolean matchFound = false;

            for (Component oldComponent : oldComponents) {

                if (oldComponent.equalsExceptForQuantity(newComponent)) {

                    matchFound = true;

                    if (oldComponent.getQuantity() > newComponent.getQuantity()) {
                        componentsAssembled.add(oldComponent);
                    } else {
                        componentsDisassembled.add(newComponent);
                    }

                } else if (oldComponent.equalsExceptForSubcomponents(newComponent)) {

                    for (Component newSubcomponent : newComponent.getSubcomponents()) {

                        boolean subMatchFound = false;

                        for (Component oldSubcomponent : oldComponent.getSubcomponents()) {

                            if (newSubcomponent.equals(oldSubcomponent)) {

                                subMatchFound = true;
                            }
                        }

                        if (!subMatchFound) {

                            subcomponentsAdded.add(newSubcomponent);
                        }
                    }

                    for (Component oldSubcomponent : oldComponent.getSubcomponents()) {

                        boolean subMatchFound = false;

                        for (Component newSubcomponent : newComponent.getSubcomponents()) {

                            if (oldSubcomponent.equals(newSubcomponent)) {

                                subMatchFound = true;
                            }
                        }

                        if (!subMatchFound) {

                            subcomponentsRemoved.add(oldSubcomponent);
                        }
                    }

                    matchFound = true;
                }
            }

            if (!matchFound) {
                return false;
            }
        }

        for (Component assembledComponent : componentsAssembled) {

            boolean matchFound = false;

            for (Component addedSubcomponent : subcomponentsAdded) {

                if (assembledComponent.equalsExceptForQuantity(addedSubcomponent)) {

                    matchFound = true;
                }
            }

            if (!matchFound) {
                return false;
            }
        }

        for (Component disassembledComponent : componentsDisassembled) {

            boolean matchFound = false;

            for (Component removedSubcomponent : subcomponentsRemoved) {

                if (disassembledComponent.equalsExceptForQuantity(removedSubcomponent)) {

                    matchFound = true;
                }
            }

            if (!matchFound) {
                return false;
            }
        }

        if (commitResults) {
            // Remove old components from node's unspent component list
            for (Component oldComponent : oldComponents) {
                node.getUnspentComponents().remove(oldComponent);
            }

            // Add newly created components to the node's unspent component list
            for (Component newComponent : transaction.getComponentsCreated()) {
                node.getUnspentComponents().add(newComponent);
            }
        }
        return true;
    }
}
