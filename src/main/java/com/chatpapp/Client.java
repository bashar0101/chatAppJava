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
import java.util.Scanner;

/**
 *
 * @author basha
 */
public class Client {

    int serverPort;
    InetAddress ServerIpAddress;
    String name;
    String lastName;
    String email;

    Socket socket;
    DataInputStream in;
    DataOutputStream out;

    Client(int port, String ipAddress) throws UnknownHostException {
        this.serverPort = port;
        this.ServerIpAddress = InetAddress.getByName(ipAddress);
    }

    public boolean ConnectToServer() throws IOException {
        socket = new Socket(this.ServerIpAddress, this.serverPort);

        out = new DataOutputStream(socket.getOutputStream());
        in = new DataInputStream(socket.getInputStream());

        System.out.println("Connection accepted with server -> " + socket.getInetAddress() + ":" + socket.getPort());
        Scanner scanner = new Scanner(System.in);
        String message = "";

        System.out.println("Enter message please: ");
        while (!message.equals("exit")) {
            message = scanner.nextLine();
            out.writeUTF(message);
            if(message.equals("exit")){
                socket.close();
            }
        }
        return true;
    }

    public static void main(String[] args) throws UnknownHostException, IOException {
        Client c = new Client(5000, "localhost");
        c.ConnectToServer();
    }
}
