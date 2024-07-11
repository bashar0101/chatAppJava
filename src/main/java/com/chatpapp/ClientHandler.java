/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.chatpapp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    // database section
    public static String urlDB = "jdbc:mysql://localhost:3306/chatappdatabase";
    public static String userNameDB = "root";
    public static String passwordDB = "20142007";

    public ClientHandler(Socket cSocket) {
        try {
//            c = new Client(clientSocket.getPort(), clientSocket.getInetAddress().getHostName());
            this.clientSocket = cSocket;
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());
            c = new Client(clientSocket.getPort(), clientSocket.getInetAddress().getHostName());
            Server.connectedClients.add(c);
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
                String dataFromClient = in.readUTF();
                System.out.println("data from client is -->" + dataFromClient);
                String[] clientMessages = dataFromClient.split(",");

                if (clientMessages[0].equals("1")) {
                    // here we will check the data inf the database for signIn
                    String email = clientMessages[1];
                    String password = clientMessages[2];
                    if (checkSignIn(email, password)) {
                        out.writeUTF("11,");

                    } else {
                        out.writeUTF("10,");
                    }
                }

            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
        closeResources();
    }

    private boolean checkSignIn(String email, String password) {
        String query = "SELECT * FROM users WHERE email = ? AND password = ?";

        try (Connection connection = DriverManager.getConnection(urlDB, userNameDB, passwordDB); PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                // User found
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void removeClient(Client c) {
        Server.connectedClients.remove(c);

    }

    private void closeResources() {
        try {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (clientSocket != null) {
                clientSocket.close();
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
