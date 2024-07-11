/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.chatpapp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author basha
 */
public class ClientHandler extends Thread {

    DataOutputStream out;
    DataInputStream in;
    Client c;
    boolean isClientConnected;
    Socket clientSocket;

    public ClientHandler(Socket clientSocket) {
        try {
            this.clientSocket = clientSocket;
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());
            isClientConnected = true;
        } catch (IOException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        this.HandleClient();
    }

    public void HandleClient() {
        while (isClientConnected) {
            try {
                String name = in.readUTF();
                if (name.equals("exit")) {
                    System.out.println(clientSocket.getPort() +" left the chat");
                    isClientConnected = false;
                } else {
                    System.out.println(clientSocket.getPort() + ":" + name);
                }
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
}
