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
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;

/**
 *
 * @author basha
 */
public class ClientHandler extends Thread {

    DataOutputStream out;
    DataInputStream in;
    Client client;
    boolean isClientConnected;
    Socket clientSocket;
    public static ArrayList<Client> clientsInServer = new ArrayList<>();
    ;
    
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
            client = new Client(clientSocket.getInetAddress().getHostName(), clientSocket.getPort());
            clientsInServer.add(client);
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

                for (Client client1 : clientsInServer) {
                    System.out.println(client1.ipAddress + ":" + client1.port);
                }
                String[] clientMessages = dataFromClient.split(",");

                if (clientMessages[0].equals("1")) {
                    // here we will check the data inf the database for signIn
                    String email = clientMessages[1];
                    String password = clientMessages[2];
                    switch (checkSignIn(email, password)) {
                        case 11:
                            out.writeUTF("11");
                            break;
                        case 10:
                            out.writeUTF("10");
                            break;
                        case 00:
                            out.writeUTF("00");
                            break;
                        default:
                            break;
                    }
                }
                if (dataFromClient.equals("exit")) {
                    removeClient(client);
                    out.writeUTF("clinetRemoved");
                }
                if (clientMessages[0].equals("2")) {
                    String email = clientMessages[1];
                    String password = clientMessages[2];
                    String name = clientMessages[3];
                    String lastName = clientMessages[4];
                    int age = Integer.parseInt(clientMessages[5]);
                    String gander = clientMessages[6];

                    switch (addNewUser(email, password, name, lastName, age, gander)) {
                        case 20:
                            //email already rigstere
                            out.writeUTF("20");
                            break;
                        case 21:
                            out.writeUTF("21");
                            break;
                        case 201:
                            out.writeUTF("201");
                            break;
                    }
                }

            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
        closeResources();
    }

    public static int addNewUser(String email, String password, String firstName, String lastName, int age, String gender) {
        String checkEmailQuery = "SELECT * FROM users WHERE email = ?";
        String insertUserQuery = "INSERT INTO users (email, password, first_name, last_name, age, gender) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(urlDB, userNameDB, passwordDB); PreparedStatement checkEmailStatement = connection.prepareStatement(checkEmailQuery); PreparedStatement insertUserStatement = connection.prepareStatement(insertUserQuery)) {

            // Check if the email already exists
            checkEmailStatement.setString(1, email);
            ResultSet emailResultSet = checkEmailStatement.executeQuery();

            if (emailResultSet.next()) {
                // Email is already registered
                return 20;
            } else {
                // Email is not registered, insert new user
                insertUserStatement.setString(1, email);
                insertUserStatement.setString(2, password);
                insertUserStatement.setString(3, firstName);
                insertUserStatement.setString(4, lastName);
                insertUserStatement.setInt(5, age);
                insertUserStatement.setString(6, gender);

                int rowsInserted = insertUserStatement.executeUpdate();
                if (rowsInserted > 0) {
                    return 21;
                } else {
                    return 201;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private int checkSignIn(String email, String password) {
        String emailQuery = "SELECT * FROM users WHERE email = ?";
        String passwordQuery = "SELECT * FROM users WHERE email = ? AND password = ?";

        try (Connection connection = DriverManager.getConnection(urlDB, userNameDB, passwordDB); PreparedStatement emailPreparedStatement = connection.prepareStatement(emailQuery); PreparedStatement passwordPreparedStatement = connection.prepareStatement(passwordQuery)) {

            // Check if email exists
            emailPreparedStatement.setString(1, email);
            ResultSet emailResultSet = emailPreparedStatement.executeQuery();

            if (emailResultSet.next()) {
                // Email exists, now check password
                passwordPreparedStatement.setString(1, email);
                passwordPreparedStatement.setString(2, password);
                ResultSet passwordResultSet = passwordPreparedStatement.executeQuery();

                if (passwordResultSet.next()) {
                    // Password matches
                    return 11;
                } else {
                    // Password does not match
                    return 10;
                }
            } else {
                // Email does not exist
                return 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private void removeClient(Client c) {
        clientsInServer.remove(c);

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
