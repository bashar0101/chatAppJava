/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.chatpapp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author basha
 */
public class Server {

    boolean isListening = false;
    ServerSocket serverSocket;

    public static ArrayList<Client> connectedClients;

    int port;
    InetAddress ipAddress;

    ClientHandler clientHandler;
    Client client;

 

    Server() {
    }

    public boolean Create(int port) {
        try {
            this.port = port;
            serverSocket = new ServerSocket(this.port);
            System.out.println("Server started");
            this.ipAddress = serverSocket.getInetAddress();
            connectedClients = new ArrayList<>();
            return true;
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    public void Listen() {
        this.isListening = true;
//        this.start();
        this.run();
    }

    /**
     *
     */
//    @Override
    public void run() {
        while (isListening) {
            try {
                System.out.println("Server wating for clients....");
                Socket clientSocket = serverSocket.accept();
                String cinfo = clientSocket.getInetAddress().toString() + ":" + clientSocket.getPort();
                System.out.println("client connected to server ---> " + cinfo);
                clientHandler = new ClientHandler(clientSocket);
                clientHandler.start();
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void main(String[] args) {
        Server s = new Server();
        s.Create(5000);
        s.Listen();
    }

}
