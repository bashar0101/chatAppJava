/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.chatpapp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author basha
 */
public class Client extends Thread {

    int port;
    InetAddress ipAddress;
    Socket socket;
    DataInputStream in;
    DataOutputStream out;

    String name;
    String lastName;
    String email;

    boolean isConnected;
    String ServerResponse;

    //
    SignIn signInFrm;

    Client(int port, String ipAddress) throws UnknownHostException {
        this.port = port;
        this.ipAddress = InetAddress.getByName(ipAddress);
    }

    public boolean ConnectToServer() throws IOException {
        socket = new Socket(this.ipAddress, this.port);
        out = new DataOutputStream(socket.getOutputStream());
        in = new DataInputStream(socket.getInputStream());
        System.out.println("Connection accepted with server -> " + socket.getInetAddress() + ":" + socket.getPort());
        isConnected = true;
        return true;
    }

    public void sendDataToServer(String message) throws IOException {
        out.writeUTF(message);

    }

    public void Listen() {
        this.start();
    }

    @Override
    public void run() {
        while (isConnected) {
            try {
                ServerResponse = "";
                ServerResponse = in.readUTF();
                System.out.println(ServerResponse);
                String[] responses = ServerResponse.split(",");
                if (responses[0].equals("11")) {
                    System.out.println("correct data");
                } else if (responses[0].equals("10")) {
                    System.out.println("Invalid email or password.");
                    JOptionPane.showMessageDialog(signInFrm, "Invalid email or password.");
                }

            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void DisconnectFromServer() {
        isConnected = false;
        try {

            if (socket != null) {
                this.socket.close();
            }
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }

        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

//    public static void main(String[] args) throws UnknownHostException, IOException {
//        Client c = new Client(5000, "localhost");
//        c.ConnectToServer();
//    }
}
