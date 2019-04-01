# A Blockchain-based Application for Traceability in Smart Manufacturing

## About
This application has been developed as my Final Year Project for a degree in Electronic & Computer Engineering at NUI Galway.
[Please see this poster for an overview.](https://drive.google.com/file/d/15ub5N7vmmolEUo48GpieOyUoD-eQru5H/view)

## Introduction
* The traceability of materials, components, products, etc., is an important issue in many areas of manufacturing.
* A Blockchain is a data structure that allows for a peer-to-peer network of nodes to immutably store information, and to propagate and verify new information; without the need for backend infrastructure.
* A desktop application has been developed to implement a system, based on a private blockchain, to assist with traceability in manufacturing, through the supply chain, through different stages of production, up to finished products.
* The system has been developed from scratch in Java, in order to allow for the acquisition of a deep and thorough understanding of the workings of a blockchain.
* A modified version of the application, designed to run on machines with less resources, has also been developed, and has been tested on a Raspberry Pi.

## Design and Implementation
* Private blockchains, on which this project is based, differ from public blockchains used in cryptocurrencies such as Bitcoin in that they can be owned and maintained by an organisation for their own benefit, and can be customised so as to be applied to any application.
* Any number of nodes can be run simultaneously, and, when made aware of one another, will form a peer-to-peer network. Each node maintains its own copy of the blockchain, a sequence of ‘blocks’, along with validation rules which will be used to accept or reject new blocks it receives from other nodes.
* A ‘block’ is a group of ‘transactions’, plus some metadata, including the hash of the previous block in the chain. Thus, an attempt to modify the data in any block breaks this chain of hashes.
* A ‘transaction’ represents some event occurring, such as production of new components, assembly or disassembly of components, or change of ownership. Any node can create its own transactions, and disseminate them to the other nodes in the network.
* Components represent something which exists in the world. They can have many subcomponents, which can themselves have subcomponents, forming a tree-like structure in which every part is traceable.
* An additional problem solved by the system is tracking the ownership of products over their lifetimes.
* All communications between nodes are digitally signed, to verify their origin, and encrypted, to avoid exposing potentially proprietary data. Communication takes place over UDP.
* Nodes frequently synchronise with one another – they send each other a summary of their state (blockchain, connections to other nodes, transactions not yet included in a block); and act to share information which other nodes do not have, until consistency is achieved.
* A GUI has been developed using Java Swing to allow user interaction with the system.
* The state of a node can be saved to a file, which could be used to migrate it to another machine. As the system has been developed in Java, it will run in a variety of environments.
* A ‘light running’ version of the application has been developed, which allows for nodes that do not store a copy of the blockchain, saving storage space, and also the processing power required to verify incoming blocks. Light nodes query full nodes for information, and send them requests to create transactions.
* Multiple nodes can be run on the same machine, using different ports to receive messages. This facilitates easy demonstration of the system.

## Build Instructions
It is recommended to build the project using the NetBeans IDE, which includes support for Maven, the build tool. A new Maven Java project should be created in the IDE, and the contents of the repository copied in.
